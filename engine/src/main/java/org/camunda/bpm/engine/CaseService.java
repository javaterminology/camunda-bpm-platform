/* Licensed under the Apache License, Version 2.0 (the "License");
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
package org.camunda.bpm.engine;

import java.util.Collection;
import java.util.Map;

import org.camunda.bpm.engine.runtime.CaseExecution;
import org.camunda.bpm.engine.runtime.CaseExecutionCommandBuilder;
import org.camunda.bpm.engine.runtime.CaseExecutionQuery;
import org.camunda.bpm.engine.runtime.CaseInstance;
import org.camunda.bpm.engine.runtime.CaseInstanceBuilder;
import org.camunda.bpm.engine.runtime.CaseInstanceQuery;

/**
 * Service which provides access to {@link CaseInstance case instances}
 * and {@link CaseExecution case executions}.
 *
 * @author Roman Smirnov
 *
 */
public interface CaseService {

  /**
   * <p>Define a {@link CaseInstance} using a fluent builder.</p>
   *
   * @param caseDefinitionKey The key of case definition to create a new case instance in
   * the latest version of the case definition with the given key, cannot be null.
   *
   * @return a {@link CaseInstanceBuilder fluent builder} for defining a new case instance
   */
  CaseInstanceBuilder createCaseInstanceByKey(String caseDefinitionKey);

  /**
   * <p>Define a {@link CaseInstance} using a fluent builder.</p>
   *
   * <p>Starts a new case instance in the exactly specified version of the case definition
   * with the given id.</p>
   *
   * @param caseDefinitionId The id of case definition to create a new case instance in
   * the exactly specified version of the case definition with the given id, cannot be null.
   *
   * @return a {@link CaseInstanceBuilder fluent builder} for defining a new case instance
   */
  CaseInstanceBuilder createCaseInstanceById(String caseDefinitionId);

  /**
   * <p>Creates a new {@link CaseInstanceQuery} instance, that can be used
   * to query case instances.</p>
   */
  CaseInstanceQuery createCaseInstanceQuery();

  /**
   * <p>Creates a new {@link CaseExecutionQuery} instance,
   * that can be used to query the executions and case instances.</p>
   */
  CaseExecutionQuery createCaseExecutionQuery();

  /**
   * <p>Define a command to be executed for a {@link CaseExecution} using a fluent builder.</p>
   *
   * @param caseExecutionId The id of a case execution to define a command for it.
   *
   * @return a {@link CaseExecutionCommandBuilder fluent builder} for defining a command
   *         for a case execution
   */
  CaseExecutionCommandBuilder withCaseExecution(String caseExecutionId);

  /**
   * <p>All variables visible from the given execution scope (including parent scopes).</p>
   *
   * <p>If you have many local variables and you only need a few, consider
   * using {@link #getVariables(String, Collection)} for better performance.</p>
   *
   * @param caseExecutionId id of case instance or case execution, cannot be null.
   *
   * @return the variables or an empty map if no such variables are found.
   *
   * @throws ProcessEngineException when no case execution is found for the given case execution id.
   */
  Map<String, Object> getVariables(String caseExecutionId);

  /**
   * <p>All variable values that are defined in the case execution scope, without
   * taking outer scopes into account.</p>
   *
   * <p>If you have many local variables and you only need a few, consider
   * using {@link #getVariablesLocal(String, Collection)} for better performance.</p>
   *
   * @param caseExecutionId id of case execution, cannot be null.
   *
   * @return the variables or an empty map if no such variables are found.
   *
   * @throws ProcessEngineException when no case execution is found for the given case execution id.
   */
  Map<String, Object> getVariablesLocal(String caseExecutionId);

  /**
   * <p>The variable values for all given variableNames, takes all variables
   * into account which are visible from the given case execution scope
   * (including parent scopes).</p>
   *
   * @param caseExecutionId id of case instance or case execution, cannot be null.
   * @param variableNames the collection of variable names that should be retrieved.
   *
   * @return the variables or an empty map if no such variables are found.
   *
   * @throws ProcessEngineException when no case execution is found for the given case execution id.
   */
  Map<String, Object> getVariables(String caseExecutionId, Collection<String> variableNames);

  /**
   * <p>The variable values for the given variableNames only taking the given case
   * execution scope into account, not looking in outer scopes.</p>
   *
   * @param caseExecutionId id of case execution, cannot be null.
   * @param variableNames the collection of variable names that should be retrieved.
   *
   * @return the variables or an empty map if no such variables are found.
   *
   * @throws ProcessEngineException when no case execution is found for the given case execution id.
   */
  Map<String, Object> getVariablesLocal(String caseExecutionId, Collection<String> variableNames);

  /**
   * <p>Searching for the variable is done in all scopes that are visible
   * to the given case execution (including parent scopes).</p>
   *
   * <p>Returns null when no variable value is found with the given name o
   * when the value is set to null.</p>
   *
   * @param caseExecutionId id of case instance or case execution, cannot be null.
   * @param variableName name of variable, cannot be null.
   *
   * @return the variable value or null if the variable is undefined or the value of the variable is null.
   *
   * @throws ProcessEngineException when no case execution is found for the given case execution id.
   */
  Object getVariable(String caseExecutionId, String variableName);

  /**
   * <p>The variable value for an case execution. Returns the value when the variable is set
   * for the case execution (and not searching parent scopes).</p>
   *
   * <p>Returns null when no variable value is found with the given name or when the value is
   * set to null.</p>
   *
   * @param caseExecutionId id of case instance or case execution, cannot be null.
   * @param variableName name of variable, cannot be null.
   *
   * @return the variable value or null if the variable is undefined or the value of the variable is null.
   *
   * @throws ProcessEngineException when no case execution is found for the given case execution id.
   */
  Object getVariableLocal(String caseExecutionId, String variableName);
}
