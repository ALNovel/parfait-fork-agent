/*
 * Copyright 2009-2017 Red Hat Inc.
 *
 * Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */

package io.pcp.parfait;

import com.fasterxml.jackson.databind.JsonNode;

class SpecificationAdapter {

    Specification fromJson(JsonNode jsonNode) {
        final String optionalParsingResult = jsonNode.path("optional").asText();
        boolean optional = !"false".equals(optionalParsingResult);

        return new Specification(jsonNode.path("name").asText(),
                optional,
                jsonNode.path("description").asText(),
                jsonNode.path("semantics").asText(),
                jsonNode.path("units").asText(),
                jsonNode.path("mBeanName").asText(),
                jsonNode.path("mBeanAttributeName").asText(),
                jsonNode.path("mBeanCompositeDataItem").asText());

    }



}
