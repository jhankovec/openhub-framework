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

package org.openhubframework.openhub.core.common.ws;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.wsdl.wsdl11.Wsdl11Definition;


/**
 * Implementation of {@link WsdlRegistry} based on Spring capabilities that supposes the following prerequisites:
 * <ul>
 *     <li>all WSDLs are defined/published via {@link Wsdl11Definition}
 *     <li>Spring bean IDs will contain the WSDL name
 * </ul>
 *
 * @author Petr Juza
 */
@Service
public class WsdlRegistrySpringImpl implements WsdlRegistry {

    // note: I can't use binding via constructor because it throws error if there is no any autowired Wsdl11Definition
    @Autowired(required = false)
    private Map<String, Wsdl11Definition> wsdls;

    @Override
    public Collection<String> getWsdls() {
        if (wsdls != null) {
            return wsdls.keySet();
        } else {
            return Collections.emptyList();
        }
    }
}
