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

package org.cleverbus.core.reqres;

import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import org.cleverbus.api.entity.Request;
import org.cleverbus.api.entity.Response;
import org.cleverbus.core.common.dao.RequestResponseDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;


/**
 * Default implementation of {@link RequestResponseService} interface.
 * <p/>
 * Implementation saves directly requests/responses into database in synchronous manner.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @since 0.4
 */
@Transactional
public class RequestResponseServiceDefaultImpl implements RequestResponseService {

    @Autowired
    private RequestResponseDao requestResponseDao;

    @Override
    public void insertRequest(Request request) {
        Assert.notNull(request, "the request must not be null");

        requestResponseDao.insertRequest(request);
    }

    @Override
    public void insertResponse(Response response) {
        Assert.notNull(response, "the response must not be null");

        requestResponseDao.insertResponse(response);
    }

    @Nullable
    @Override
    public Request findLastRequest(String uri, String responseJoinId) {
        return requestResponseDao.findLastRequest(uri, responseJoinId);
    }

    @Override
    public List<Request> findByCriteria(Date from, Date to, String subUri, String subRequest) {
        return requestResponseDao.findByCriteria(from, to, subUri, subRequest);
    }
}
