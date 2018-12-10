/*
 * Copyright © 2012 - 2018 camunda services GmbH and various authors (info@camunda.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.engine.impl.batch.externaltask;

import com.google.gson.JsonObject;
import org.camunda.bpm.engine.impl.batch.SetRetriesBatchConfiguration;
import org.camunda.bpm.engine.impl.json.JsonObjectConverter;
import org.camunda.bpm.engine.impl.util.JsonMapper;

import java.util.List;

public class SetExternalTaskRetriesBatchConfigurationJsonConverter extends JsonObjectConverter<SetRetriesBatchConfiguration> {

  public static final SetExternalTaskRetriesBatchConfigurationJsonConverter INSTANCE = new SetExternalTaskRetriesBatchConfigurationJsonConverter();

  public static final String EXTERNAL_TASK_IDS = "externalTaskIds";
  public static final String RETRIES = "retries";
  
  @Override
  public JsonObject toJsonObject(SetRetriesBatchConfiguration configuration) {
    JsonObject jsonObject = JsonMapper.createObjectNode();
    
    JsonMapper.addListField(jsonObject, EXTERNAL_TASK_IDS, configuration.getIds());
    JsonMapper.addField(jsonObject, RETRIES, configuration.getRetries());
    
    return jsonObject;
  }

  @Override
  public SetRetriesBatchConfiguration toObject(JsonObject json) {
    return new SetRetriesBatchConfiguration(readExternalTaskIds(json), json.get(RETRIES).getAsInt());
  }
  
  protected List<String> readExternalTaskIds(JsonObject json) {
    return JsonMapper.asList(json.get(EXTERNAL_TASK_IDS));
  }

}
