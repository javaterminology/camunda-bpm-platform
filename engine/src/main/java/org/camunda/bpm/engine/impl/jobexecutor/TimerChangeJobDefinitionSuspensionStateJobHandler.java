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
package org.camunda.bpm.engine.impl.jobexecutor;

import com.google.gson.JsonObject;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.impl.cmd.AbstractSetJobDefinitionStateCmd;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.jobexecutor.TimerChangeJobDefinitionSuspensionStateJobHandler.JobDefinitionSuspensionStateConfiguration;
import org.camunda.bpm.engine.impl.management.UpdateJobDefinitionSuspensionStateBuilderImpl;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.JobEntity;
import org.camunda.bpm.engine.impl.util.JsonMapper;

import static org.camunda.bpm.engine.impl.util.JsonMapper.addField;
import static org.camunda.bpm.engine.impl.util.JsonMapper.addNullField;

/**
 * @author roman.smirnov
 */
public abstract class TimerChangeJobDefinitionSuspensionStateJobHandler implements JobHandler<JobDefinitionSuspensionStateConfiguration> {

  protected static final String JOB_HANDLER_CFG_BY = "by";
  protected static final String JOB_HANDLER_CFG_JOB_DEFINITION_ID = "jobDefinitionId";
  protected static final String JOB_HANDLER_CFG_PROCESS_DEFINITION_ID = "processDefinitionId";
  protected static final String JOB_HANDLER_CFG_PROCESS_DEFINITION_KEY = "processDefinitionKey";
  protected static final String JOB_HANDLER_CFG_PROCESS_DEFINITION_TENANT_ID = "processDefinitionTenantId";

  protected static final String JOB_HANDLER_CFG_INCLUDE_JOBS = "includeJobs";

  public void execute(JobDefinitionSuspensionStateConfiguration configuration, ExecutionEntity execution, CommandContext commandContext, String tenantId) {
    AbstractSetJobDefinitionStateCmd cmd = getCommand(configuration);
    cmd.disableLogUserOperation();
    cmd.execute(commandContext);
  }

  protected abstract AbstractSetJobDefinitionStateCmd getCommand(JobDefinitionSuspensionStateConfiguration configuration);

  @Override
  public JobDefinitionSuspensionStateConfiguration newConfiguration(String canonicalString) {
    return JobDefinitionSuspensionStateConfiguration.fromJson(JsonMapper.mapAsObjectNode(canonicalString));
  }

  public static class JobDefinitionSuspensionStateConfiguration implements JobHandlerConfiguration {

    protected String jobDefinitionId;
    protected String processDefinitionKey;
    protected String processDefinitionId;
    protected boolean includeJobs;
    protected String tenantId;
    protected boolean isTenantIdSet;
    protected String by;

    @Override
    public String toCanonicalString() {
      JsonObject jsonObject = JsonMapper.createObjectNode();
      addField(jsonObject, JOB_HANDLER_CFG_BY, by);
      addField(jsonObject, JOB_HANDLER_CFG_JOB_DEFINITION_ID, jobDefinitionId);
      addField(jsonObject, JOB_HANDLER_CFG_PROCESS_DEFINITION_KEY, processDefinitionKey);
      addField(jsonObject, JOB_HANDLER_CFG_INCLUDE_JOBS, includeJobs);
      addField(jsonObject, JOB_HANDLER_CFG_PROCESS_DEFINITION_ID, processDefinitionId);

      if (isTenantIdSet) {
        if (tenantId != null) {
          addField(jsonObject, JOB_HANDLER_CFG_PROCESS_DEFINITION_TENANT_ID, tenantId);
        } else {
          addNullField(jsonObject, JOB_HANDLER_CFG_PROCESS_DEFINITION_TENANT_ID);
        }
      }

      return JsonMapper.writeValueAsString(jsonObject);
    }

    public UpdateJobDefinitionSuspensionStateBuilderImpl createBuilder() {
      UpdateJobDefinitionSuspensionStateBuilderImpl builder = new UpdateJobDefinitionSuspensionStateBuilderImpl();

      if (JOB_HANDLER_CFG_PROCESS_DEFINITION_ID.equals(by)) {
        builder.byProcessDefinitionId(processDefinitionId);

      }
      else if (JOB_HANDLER_CFG_JOB_DEFINITION_ID.equals(by)) {
        builder.byJobDefinitionId(jobDefinitionId);
      }
      else if (JOB_HANDLER_CFG_PROCESS_DEFINITION_KEY.equals(by)) {
        builder.byProcessDefinitionKey(processDefinitionKey);

        if (isTenantIdSet) {

          if (tenantId != null) {
            builder.processDefinitionTenantId(tenantId);

          } else {
            builder.processDefinitionWithoutTenantId();
          }
        }

      } else {
        throw new ProcessEngineException("Unexpected job handler configuration for property '" + JOB_HANDLER_CFG_BY + "': " + by);
      }

      builder.includeJobs(includeJobs);

      return builder;
    }

    public static JobDefinitionSuspensionStateConfiguration fromJson(JsonObject jsonObject) {
      JobDefinitionSuspensionStateConfiguration config = new JobDefinitionSuspensionStateConfiguration();

      config.by = jsonObject.get(JOB_HANDLER_CFG_BY).getAsString();
      if (jsonObject.has(JOB_HANDLER_CFG_JOB_DEFINITION_ID)) {
        config.jobDefinitionId = jsonObject.get(JOB_HANDLER_CFG_JOB_DEFINITION_ID).getAsString();
      }
      if (jsonObject.has(JOB_HANDLER_CFG_PROCESS_DEFINITION_ID)) {
        config.processDefinitionId = jsonObject.get(JOB_HANDLER_CFG_PROCESS_DEFINITION_ID).getAsString();
      }
      if (jsonObject.has(JOB_HANDLER_CFG_PROCESS_DEFINITION_KEY)) {
        config.processDefinitionKey = jsonObject.get(JOB_HANDLER_CFG_PROCESS_DEFINITION_KEY).getAsString();
      }
      if (jsonObject.has(JOB_HANDLER_CFG_PROCESS_DEFINITION_TENANT_ID)) {
        config.isTenantIdSet = true;
        if (!jsonObject.get(JOB_HANDLER_CFG_PROCESS_DEFINITION_TENANT_ID).isJsonNull()) {
          config.tenantId = jsonObject.get(JOB_HANDLER_CFG_PROCESS_DEFINITION_TENANT_ID).getAsString();
        }
      }
      if (jsonObject.has(JOB_HANDLER_CFG_INCLUDE_JOBS)) {
        config.includeJobs = jsonObject.get(JOB_HANDLER_CFG_INCLUDE_JOBS).getAsBoolean();
      }

      return config;
    }

    public static JobDefinitionSuspensionStateConfiguration byJobDefinitionId(String jobDefinitionId, boolean includeJobs) {
      JobDefinitionSuspensionStateConfiguration configuration = new JobDefinitionSuspensionStateConfiguration();
      configuration.by = JOB_HANDLER_CFG_JOB_DEFINITION_ID;
      configuration.jobDefinitionId = jobDefinitionId;
      configuration.includeJobs = includeJobs;

      return configuration;
    }

    public static JobDefinitionSuspensionStateConfiguration byProcessDefinitionId(String processDefinitionId, boolean includeJobs) {
      JobDefinitionSuspensionStateConfiguration configuration = new JobDefinitionSuspensionStateConfiguration();

      configuration.by = JOB_HANDLER_CFG_PROCESS_DEFINITION_ID;
      configuration.processDefinitionId = processDefinitionId;
      configuration.includeJobs = includeJobs;

      return configuration;
    }

    public static JobDefinitionSuspensionStateConfiguration byProcessDefinitionKey(String processDefinitionKey, boolean includeJobs) {
      JobDefinitionSuspensionStateConfiguration configuration = new JobDefinitionSuspensionStateConfiguration();

      configuration.by = JOB_HANDLER_CFG_PROCESS_DEFINITION_KEY;
      configuration.processDefinitionKey = processDefinitionKey;
      configuration.includeJobs = includeJobs;

      return configuration;
    }

    public static JobDefinitionSuspensionStateConfiguration ByProcessDefinitionKeyAndTenantId(String processDefinitionKey, String tenantId, boolean includeProcessInstances) {
      JobDefinitionSuspensionStateConfiguration configuration = byProcessDefinitionKey(processDefinitionKey, includeProcessInstances);

      configuration.isTenantIdSet = true;
      configuration.tenantId = tenantId;

      return configuration;

    }


  }

  public void onDelete(JobDefinitionSuspensionStateConfiguration configuration, JobEntity jobEntity) {
    // do nothing
  }

}
