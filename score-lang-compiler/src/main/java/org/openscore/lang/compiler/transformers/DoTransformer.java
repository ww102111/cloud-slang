package org.openscore.lang.compiler.transformers;
/*
 * Licensed to Hewlett-Packard Development Company, L.P. under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
*/

/*
 * Created by orius123 on 05/11/14.
 */

import org.openscore.lang.entities.bindings.Input;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Component
public class DoTransformer extends AbstractInputsTransformer implements Transformer<LinkedHashMap<String, List>, List<Input>> {

    @Override
    public List<Input> transform(LinkedHashMap<String, List> rawData) {
        //todo handle also String type
        List<Input> result = new ArrayList<>();
        if (MapUtils.isEmpty(rawData)) {
            return result;
        }
        Map.Entry<String, List> inputsEntry = rawData.entrySet().iterator().next();
        if (inputsEntry.getValue() == null) {
            return result;
        }
        for (Object rawInput : inputsEntry.getValue()) {
            Input input = transformSingleInput(rawInput);
            result.add(input);
        }
        return result;
    }

    @Override
    public List<Scope> getScopes() {
        return Arrays.asList(Scope.BEFORE_TASK);
    }

    @Override
    public String keyToTransform() {
        return null;
    }

}