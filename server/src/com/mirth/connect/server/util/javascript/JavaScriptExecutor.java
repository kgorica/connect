/*
 * Copyright (c) Mirth Corporation. All rights reserved.
 * http://www.mirthcorp.com
 * 
 * The software in this package is published under the terms of the MPL
 * license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.mirth.connect.server.util.javascript;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.mozilla.javascript.Context;

public class JavaScriptExecutor<T> {
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public T execute(JavaScriptTask<T> task) throws JavaScriptExecutorException, InterruptedException {
        Future<T> future = executor.submit(task);
        
        try {
            return future.get();
        } catch (ExecutionException e) {
            throw new JavaScriptExecutorException(e.getCause());
        } catch (InterruptedException e) {
            // synchronize with JavaScriptTask.executeScript() so that it will not initialize the context while we are halting the task
            synchronized (task) {
                future.cancel(true);
                Context context = task.getContext();
            
                if (context != null && context instanceof StoppableContext) {
                    ((StoppableContext) context).setRunning(false);
                }
            }
            
            // TODO wait for the task thread to complete before exiting?
            throw e;
        }
    }
}
