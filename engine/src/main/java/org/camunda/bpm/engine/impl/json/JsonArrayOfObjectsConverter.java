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
package org.camunda.bpm.engine.impl.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import org.camunda.bpm.engine.impl.util.JsonMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thorben Lindhauer
 */
public class JsonArrayOfObjectsConverter<T> extends JsonArrayConverter<List<T>> {

  protected JsonObjectConverter<T> objectConverter;

  public JsonArrayOfObjectsConverter(JsonObjectConverter<T> objectConverter) {
    this.objectConverter = objectConverter;
  }

  public JsonArray toJsonArray(List<T> objects) {
    JsonArray jsonArray = JsonMapper.createArrayNode();

    for (T object : objects) {
      JsonElement jsonObject = objectConverter.toJsonObject(object);
      jsonArray.add(jsonObject);
    }

    return jsonArray;
  }

  public List<T> toObject(JsonArray jsonArray) {
    List<T> result = new ArrayList<T>();
    for (JsonElement jsonElement : jsonArray) {
      T object = objectConverter.toObject(jsonElement.getAsJsonObject());
      result.add(object);
    }

    return result;
  }
}
