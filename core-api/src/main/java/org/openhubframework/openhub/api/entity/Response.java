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

package org.openhubframework.openhub.api.entity;

import java.time.Instant;
import javax.annotation.Nullable;
import javax.persistence.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.util.Assert;


/**
 * Entity for saving response from external system or during internal communication.
 *
 * @author Petr Juza
 * @see Request
 * @since 0.4
 */
@Entity
@Table(name = "response")
public class Response extends SuperEntity<Long> {

    @Id
    @Column(name = "res_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "openhub_id_sequence")
    @SequenceGenerator(name="openhub_id_sequence", sequenceName="openhub_sequence", allocationSize=1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "msg_id", nullable = true)
    private Message message;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "req_id", nullable = true)
    private Request request;

    // in PostgreSQL it's defined as TEXT
    @Column(name = "res_envelope", length = Integer.MAX_VALUE, nullable = true)
    private String response;

    // in PostgreSQL it's defined as TEXT
    @Column(name = "failed_reason", length = Integer.MAX_VALUE, nullable = true)
    private String failedReason;

    @Column(name = "res_timestamp", nullable = true)
    private Instant resTimestamp;

    @Column(name = "failed", nullable = false)
    private boolean failed;

    /**
     * Creates a new response.
     *
     * @param request the corresponding request to this response; sometimes can happen that it's not possible to find
     *                 request and even so it's good to save response
     * @param response the response (response or failed reason must not be empty)
     * @param failedReason the failed reason (response or failed reason must not be empty)
     * @param msg the asynchronous message
     * @return response entity
     */
    public static Response createResponse(@Nullable Request request, @Nullable String response,
            @Nullable String failedReason, @Nullable Message msg) {

        Assert.isTrue(StringUtils.isNotEmpty(response) || StringUtils.isNotEmpty(failedReason),
                "response or failedReason must not be empty");

        Instant currDate = Instant.now();

        Response res = new Response();
        res.setRequest(request);
        res.setResTimestamp(currDate);
        res.setResponse(response);
        res.setFailedReason(failedReason);
        res.setMessage(msg);

        return res;
    }

    /**
     * Gets unique response ID.
     *
     * @return unique ID
     */
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets corresponding request to this response.
     *
     * @return request
     */
    public @Nullable Request getRequest() {
        return request;
    }

    public void setRequest(@Nullable Request request) {
        this.request = request;
    }

    /**
     * Gets response. If not specified then {@link #getFailedReason()} must be filled.
     *
     * @return response
     */
    public @Nullable String getResponse() {
        return response;
    }

    public void setResponse(@Nullable String response) {
        this.response = response;
    }

    /**
     * Gets reason (SOAP fault, exception, stackTrace, ...) why communication failed.
     *
     * @return failed reason
     */
    public @Nullable String getFailedReason() {
        return failedReason;
    }

    public void setFailedReason(@Nullable String failedReason) {
        this.failedReason = failedReason;

        if (!failed && StringUtils.isNotEmpty(failedReason)) {
            failed = true;
        }
    }

    /**
     * Gets timestamp when response/failed reason was received back.
     *
     * @return timestamp
     */
    public Instant getResTimestamp() {
        return resTimestamp;
    }

    public void setResTimestamp(Instant resTimestamp) {
        Assert.notNull(resTimestamp, "resTimestamp must not be null");

        this.resTimestamp = resTimestamp;
    }

    /**
     * Has been communication failed?
     * If failed then {@link #getFailedReason() failed reason} must be specified.
     *
     * @return {@code true} if yes otherwise {@code false}
     */
    public boolean isFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    /**
     * Gets asynch. message.
     *
     * @return message
     */
    public @Nullable Message getMessage() {
        return message;
    }

    public void setMessage(@Nullable Message message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof Response && super.equals(obj);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("request", request != null ? request.toHumanString() : "null")
                .append("response", response)
                .append("failedReason", failedReason)
                .append("resTimestamp", resTimestamp)
                .append("failed", failed)
                .append("msgId", (message != null ? message.getMsgId() : null))
                .toString();
    }

    @Override
    public String toHumanString() {
        return "(id = " + id + ", request = " + (request != null ? request.toHumanString() : "null") + ")";
    }
}