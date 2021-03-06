/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.core.common.asynch.repair;

import static java.lang.Math.min;
import static org.openhubframework.openhub.api.configuration.CoreProps.ASYNCH_COUNT_PARTLY_FAILS_BEFORE_FAILED;
import static org.openhubframework.openhub.api.configuration.CoreProps.ASYNCH_REPAIR_REPEAT_TIME_SEC;

import java.time.Instant;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.asynch.AsynchConstants;
import org.openhubframework.openhub.api.configuration.ConfigurableValue;
import org.openhubframework.openhub.api.configuration.ConfigurationItem;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.api.exception.IntegrationException;
import org.openhubframework.openhub.api.exception.InternalErrorEnum;
import org.openhubframework.openhub.common.time.Seconds;
import org.openhubframework.openhub.core.common.dao.MessageDao;

/**
 * DB implementation of {@link RepairMessageService} interface.
 *
 * @author Petr Juza
 */
@Service(RepairMessageService.BEAN)
public class RepairMessageServiceDbImpl implements RepairMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(RepairMessageServiceDbImpl.class);

    private static final int BATCH_SIZE = 10;

    private TransactionTemplate transactionTemplate;

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private ProducerTemplate producerTemplate;

    /**
     * How often to run repair process (in seconds).
     */
    @ConfigurableValue(key = ASYNCH_REPAIR_REPEAT_TIME_SEC)
    private ConfigurationItem<Seconds> repeatInterval;

    /**
     * Count of partly fails before message will be marked as completely FAILED.
     */
    @ConfigurableValue(key = ASYNCH_COUNT_PARTLY_FAILS_BEFORE_FAILED)
    private ConfigurationItem<Integer> countPartlyFailsBeforeFailed;

    @Autowired
    public RepairMessageServiceDbImpl(PlatformTransactionManager transactionManager) {
        Assert.notNull(transactionManager, "the transactionManager must not be null");

        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public void repairProcessingMessages() {
        // find messages in PROCESSING state
        List<Message> messages = findProcessingMessages();

        LOG.debug("Found {} message(s) for repairing ...", messages.size());

        // repair messages in batches
        int batchStartIncl = 0;
        int batchEndExcl;
        while (batchStartIncl < messages.size()) {
            batchEndExcl = min(batchStartIncl + BATCH_SIZE, messages.size());
            updateMessagesInDB(messages.subList(batchStartIncl, batchEndExcl));
            batchStartIncl = batchEndExcl;
        }
    }

    private List<Message> findProcessingMessages() {
        return transactionTemplate.execute(new TransactionCallback<List<Message>>() {
            @Override
            @SuppressWarnings("unchecked")
            public List<Message> doInTransaction(TransactionStatus status) {
                return messageDao.findProcessingMessages(repeatInterval.getValue().toDuration());
            }
        });
    }

    /**
     * Updates bulk of messages in DB.
     *
     * @param messages the messages for update
     */
    private void updateMessagesInDB(final List<Message> messages) {
        final Instant currDate = Instant.now();

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                for (final Message msg : messages) {
                    // checks if failed count exceeds limit for failing
                    if (msg.getFailedCount() >= countPartlyFailsBeforeFailed.getValue()) {
                        LOG.warn("The message " + msg.toHumanString() + " was in PROCESSING state and exceeded "
                                + "max. count of failures. Message is redirected to processing of failed message.");

                        // redirect to "FAILED" route
                        producerTemplate.send(AsynchConstants.URI_ERROR_FATAL, ExchangePattern.InOnly, new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                IntegrationException ex = new IntegrationException(InternalErrorEnum.E116);
                                exchange.setProperty(Exchange.EXCEPTION_CAUGHT, ex);

                                exchange.getIn().setHeader(AsynchConstants.MSG_HEADER, msg);
                            }
                        });

                    } else {
                        msg.setLastUpdateTimestamp(currDate);
                        msg.setState(MsgStateEnum.PARTLY_FAILED);
                        // note: increase count of failures because if message stays in PROCESSING state it's almost sure
                        //  because of any error
                        msg.setFailedCount(msg.getFailedCount() + 1);

                        messageDao.update(msg);

                        LOG.warn("The message " + msg.toHumanString() + " was in PROCESSING state "
                                + "and changed to PARTLY_FAILED.", msg.getMsgId(), msg.getCorrelationId());
                    }
                }
            }
        });
    }
}
