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

package org.openhubframework.openhub.admin.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.openhubframework.openhub.api.entity.ErrorsCatalog;


/**
 * Implementation of {@link ErrorCatalogService}.
 *
 * @author Tomas Hanus
 */
@Service
public class ErrorCatalogServiceImpl implements ErrorCatalogService {

    @Autowired
    private List<ErrorsCatalog> errorCodesCatalog;

    @Override
    public List<ErrorsCatalog> getErrorCatalog() {
        return errorCodesCatalog;
    }
}
