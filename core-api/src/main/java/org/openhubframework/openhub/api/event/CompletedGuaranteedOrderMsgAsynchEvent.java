/*
 * Copyright 2020 the original author or authors.
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

package org.openhubframework.openhub.api.event;

import org.apache.camel.Exchange;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.springframework.util.Assert;


/**
 * Event for successfully completed message in guaranteed order,
 * i.e. the message is in {@link MsgStateEnum#OK} state and in guaranteed order at the same time
 *
 * @author Michal Sabol
 */
public class CompletedGuaranteedOrderMsgAsynchEvent extends AbstractAsynchEvent {

    /**
     * Creates new event.
     *
     * @param exchange the exchange
     * @param message the message
     */
    public CompletedGuaranteedOrderMsgAsynchEvent(Exchange exchange, Message message) {
        super(exchange, message);

        Assert.isTrue(message.getState() == MsgStateEnum.OK,
                "the message must be in the state " + MsgStateEnum.OK);
        Assert.isTrue(message.isGuaranteedOrder(),
                "the message must be in guaranteed order");
    }

    @Override
    public String toString() {
        return this.getClass().getName() + ": the message " + getMessage().toHumanString()
                + " successfully processed guaranteed ordered message (state = " + MsgStateEnum.OK + ").";
    }
}
