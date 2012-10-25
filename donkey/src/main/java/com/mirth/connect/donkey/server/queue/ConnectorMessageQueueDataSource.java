/*
 * Copyright (c) Mirth Corporation. All rights reserved.
 * http://www.mirthcorp.com
 * 
 * The software in this package is published under the terms of the MPL
 * license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.mirth.connect.donkey.server.queue;

import java.util.List;

import com.mirth.connect.donkey.model.message.ConnectorMessage;
import com.mirth.connect.donkey.model.message.Status;
import com.mirth.connect.donkey.server.Donkey;
import com.mirth.connect.donkey.server.data.DonkeyDao;
import com.mirth.connect.donkey.server.data.DonkeyDaoFactory;

public class ConnectorMessageQueueDataSource implements PersistedBlockingQueueDataSource<ConnectorMessage> {
    private DonkeyDaoFactory daoFactory;
    private String channelId;
    private int metaDataId;
    private Status status;

    public ConnectorMessageQueueDataSource(String channelId, int metaDataId, Status status) {
        this.channelId = channelId;
        this.metaDataId = metaDataId;
        this.status = status;
    }

    public DonkeyDaoFactory getDaoFactory() {
        if (daoFactory == null) {
            daoFactory = Donkey.getInstance().getDaoFactory();
        }

        return daoFactory;
    }

    public void setDaoFactory(DonkeyDaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public int getMetaDataId() {
        return metaDataId;
    }

    public void setMetaDataId(int metaDataId) {
        this.metaDataId = metaDataId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public int getSize() {
        DonkeyDao dao = getDaoFactory().getDao();

        try {
            return dao.getConnectorMessageCount(channelId, metaDataId, status);
        } finally {
            dao.close();
        }
    }

    @Override
    public List<ConnectorMessage> getItems(int offset, int limit) {
        DonkeyDao dao = getDaoFactory().getDao();

        try {
            return dao.getConnectorMessages(channelId, metaDataId, status, offset, limit);
        } finally {
            dao.close();
        }
    }
}