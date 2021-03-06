/*******************************************************************************
 * (c) Copyright 2014 Hewlett-Packard Development Company, L.P.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0 which accompany this distribution.
 *
 * The Apache License is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/

package io.cloudslang.lang.compiler;

import io.cloudslang.lang.compiler.configuration.SlangCompilerSpringConfig;
import io.cloudslang.lang.compiler.modeller.ExecutableBuilder;
import io.cloudslang.lang.compiler.modeller.result.ExecutableModellingResult;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URI;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Ifat Gavish on 29/02/2016
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SlangCompilerSpringConfig.class)
public class PreCompilerErrorsTest {

    @Autowired
    private SlangCompiler compiler;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testNotOpFlowFile() throws Exception {
        URI resource = getClass().getResource("/corrupted/no_op_flow_file.sl").toURI();

        exception.expect(RuntimeException.class);
        exception.expectMessage("Error transforming source: no_op_flow_file to a Slang model. " +
                "Source no_op_flow_file has no content associated with flow/operation/properties property.");
        compiler.preCompileSource(SlangSource.fromFile(resource));
    }

    @Test
    public void testOpWithMissingNamespace() throws Exception {
        URI resource = getClass().getResource("/corrupted/op_without_namespace.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("Operation/Flow op_without_namespace must have a namespace");
        throw result.getErrors().get(0);
    }

    @Test
    public void testFlowWithMissingName() throws Exception {
        URI resource = getClass().getResource("/corrupted/missing_name_flow.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("Executable in source: missing_name_flow has no name");
        throw result.getErrors().get(0);
    }

    @Test
    public void testOperationWithNullFileName() throws Exception {
        URI resource = getClass().getResource("/corrupted/wrong_name_operation.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(new SlangSource(SlangSource.fromFile(resource).getSource(), null));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("should be declared in a file named \"test_op\" " +
                "plus a valid extension(sl, sl.yaml, sl.yml, prop.sl, yaml, yml)");
        throw result.getErrors().get(0);
    }

    @Test
     public void testOperationWithWrongName() throws Exception {
        URI resource = getClass().getResource("/corrupted/wrong_name_operation.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("should be declared in a file named \"test_op.sl\"");
        throw result.getErrors().get(0);
    }

    @Test
     public void testOperationWithWrongNameSLYAMLExtension() throws Exception {
        URI resource = getClass().getResource("/corrupted/wrong_name_operation.sl.yaml").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("should be declared in a file named \"test_op.sl.yaml\"");
        throw result.getErrors().get(0);
    }

    @Test
    public void testOperationWithWrongNameSLYMLExtension() throws Exception {
        URI resource = getClass().getResource("/corrupted/wrong_name_operation.sl.yml").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("should be declared in a file named \"test_op.sl.yml\"");
        throw result.getErrors().get(0);
    }

    @Test
    public void testOperationWithWrongNameYAMLExtension() throws Exception {
        URI resource = getClass().getResource("/corrupted/wrong_name_operation.yaml").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("should be declared in a file named \"test_op.yaml\"");
        throw result.getErrors().get(0);
    }

    @Test
    public void testOperationWithWrongNameYMLExtension() throws Exception {
        URI resource = getClass().getResource("/corrupted/wrong_name_operation.yml").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("should be declared in a file named \"test_op.yml\"");
        throw result.getErrors().get(0);
    }

    @Test
    public void testOperationWithWrongNameWrongExtension() throws Exception {
        URI resource = getClass().getResource("/corrupted/wrong_name_operation.wrong").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("it should be declared in a file named \"test_op\" " +
                "plus a valid extension(sl, sl.yaml, sl.yml, prop.sl, yaml, yml)");
        throw result.getErrors().get(0);
    }

    @Test
    public void testFlowSameInputAndOutputName() throws Exception {
        URI resource = getClass().getResource("/corrupted/same_input_and_output_name.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("Inputs and outputs names should be different for \"io.cloudslang.base.json.get_value\". " +
                "Please rename input/output \"json_path\"");
        throw result.getErrors().get(0);
    }

    @Test
    public void testFlowWithInputsAsString() throws Exception {
        URI resource = getClass().getResource("/corrupted/inputs_type_string_flow.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("For flow 'inputs_type_string_flow' syntax is illegal.\n" +
                "Under property: 'inputs' there should be a list of values, " +
                "but instead there is a string.");
        throw result.getErrors().get(0);
    }

    @Test
    public void testFlowWithInputsAsMap() throws Exception {
        URI resource = getClass().getResource("/corrupted/inputs_type_map_flow.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("For flow 'inputs_type_string_flow' syntax is illegal.\n" +
                "Under property: 'inputs' there should be a list of values, but instead there is a map.\n" +
                "By the Yaml spec lists properties are marked with a '- ' (dash followed by a space)");
        throw result.getErrors().get(0);
    }

    @Test
    public void testFlowWithIllegalTypeInput() throws Exception {
        URI resource = getClass().getResource("/corrupted/flow_with_wrong_type_input.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("For flow 'flow_with_wrong_type_input' syntax is illegal.\n" +
                "Could not transform Input : 3");
        throw result.getErrors().get(0);
    }

    @Test
    public void testFlowWithNoWorkflow() throws Exception {
        URI resource = getClass().getResource("/corrupted/no_workflow_flow.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("Error compiling no_workflow_flow. Flow: no_workflow has no workflow property");
        throw result.getErrors().get(0);
    }

    @Test
    public void testFlowWithNoWorkflowData() throws Exception {
        URI resource = getClass().getResource("/corrupted/no_workflow_data_flow.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("Error compiling no_workflow_data_flow. Flow: no_workflow_data has " +
                "no workflow property");
        throw result.getErrors().get(0);
    }

    @Test
    public void testFlowStepWithNoData() throws Exception {
        URI resource = getClass().getResource("/corrupted/no_step_data_flow.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("Step: step1 has no data");
        throw result.getErrors().get(0);
    }

    @Test
    public void testFlowStepWithTwoKeysUnderDo() throws Exception {
        URI resource = getClass().getResource("/corrupted/multiple_keys_under_do.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("For step 'step1' syntax is illegal.\n" +
                "Step has too many keys under the 'do' keyword,\n" +
                "May happen due to wrong indentation");
        throw result.getErrors().get(0);
    }

    @Test
    public void testFlowWithStepsAsList() throws Exception {
        URI resource = getClass().getResource("/corrupted/workflow_with_step_map.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("Flow: 'workflow_with_step_map' syntax is illegal.\n" +
                "Below 'workflow' property there should be a list of steps and not a map");
        throw result.getErrors().get(0);
    }

    @Test
    public void testFlowWithOnFailureStepsAsList() throws Exception {
        URI resource = getClass().getResource("/corrupted/on_failure_with_step_map.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("Flow: 'on_failure_with_step_map' syntax is illegal.\n" +
                "Below 'on_failure' property there should be a list of steps and not a map");
        throw result.getErrors().get(0);
    }

    @Test
    public void testFlowWithNoRefStep() throws Exception {
        URI resource = getClass().getResource("/corrupted/step_with_no_ref_flow.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("Step: 'step1' has no reference information");
        throw result.getErrors().get(0);
    }

    @Test
    public void testStepWithListOfOps() throws Exception {
        URI resource = getClass().getResource("/corrupted/step_with_list_of_ops.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("For step 'step1' syntax is illegal.\n" +
                "Under property: 'do' there should be a map of values, but instead there is a list.\n" +
                "By the Yaml spec maps properties are NOT marked with a '- ' (dash followed by a space)");
        throw result.getErrors().get(0);
    }

    @Test
    public void testStepWithListOfDos() throws Exception {
        URI resource = getClass().getResource("/corrupted/step_with_list_of_do_flow.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("Step: step1 syntax is illegal.\n" +
                "Below step name, there should be a map of values in the format:\n" +
                "do:\n" +
                "\top_name:");
        throw result.getErrors().get(0);
    }

    @Test
    public void testInputPrivateAndNoDefault() throws Exception {
        URI resource = getClass().getResource("/private_input_without_default.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("For operation 'private_input_without_default' syntax is illegal.\n" +
                "input: input_without_default is private but no default value was specified");
        throw result.getErrors().get(0);
    }

    @Test
    public void testWrongTag() throws Exception {
        URI resource = getClass().getResource("/corrupted/private_input_without_default_wrong_tag.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("Artifact {private_input_without_default} has unrecognized tag {action}. " +
                "Please take a look at the supported features per versions link");
        throw result.getErrors().get(0);
    }

    @Test
    public void testInputWithInvalidKey() throws Exception {
        URI resource = getClass().getResource("/illegal_key_in_input.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("For operation 'illegal_key_in_input' syntax is illegal.\n" +
                "key: karambula in input: input_with_illegal_key is not a known property");
        throw result.getErrors().get(0);
    }

    @Test
    public void testOperationWithNoActionData() throws Exception {
        URI resource = getClass().getResource("/corrupted/operation_with_no_action_data.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("Error compiling operation_with_no_action_data. " +
                "Operation: operation_with_no_action_data has no action data");
        throw result.getErrors().get(0);
    }

    @Test
    public void testOperationWithListOfActionTypes() throws Exception {
        URI resource = getClass().getResource("/corrupted/operation_with_list_of_action_types.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("Action syntax is illegal.\n" +
                "Under property: 'python_action' there should be a map of values, but instead there is a list.\n" +
                "By the Yaml spec maps properties are NOT marked with a '- ' (dash followed by a space)");
        throw result.getErrors().get(0);
    }

    @Test
    public void testParentFlowWithCorruptedSubFlow() throws Exception {
        URI subFlow = getClass().getResource("/corrupted/no_step_data_flow.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(subFlow));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("Step: step1 has no data");
        throw result.getErrors().get(0);
    }

    @Test
    public void testStepWithNavigateAsString() throws Exception {
        URI resource = getClass().getResource("/corrupted/step_with_string_navigate_value.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("For step 'step1' syntax is illegal.\n" +
                "Under property: 'navigate' there should be a list of values, but instead there is a string.");
        throw result.getErrors().get(0);
    }

    @Test
    public void testStepWithIllegalTypeOfNavigate() throws Exception {
        URI resource = getClass().getResource("/corrupted/step_with_illegal_navigate_type.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("For step 'step1' syntax is illegal.\n" +
                "Data for property: navigate -> 3 is illegal.\n" +
                " Transformer is: NavigateTransformer");
        throw result.getErrors().get(0);
    }

    @Test
    public void testDuplicateStepNamesInFlow() throws Exception {
        URI resource = getClass().getResource("/corrupted/duplicate_step_name.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("Step name: 'Step1' appears more than once in the workflow. " +
                "Each step name in the workflow must be unique");
        throw result.getErrors().get(0);
    }

    @Test
    public void testNullValueInputFlow() throws Exception {
        URI resource = getClass().getResource("/corrupted/flow_with_null_value_input.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("For flow 'flow_with_null_value_input' syntax is illegal.\n" +
                "Could not transform Input : {input1=null} since it has a null value.\n" +
                "\n" +
                "Make sure a value is specified or that indentation is properly done.");
        throw result.getErrors().get(0);
    }

    @Test
    public void testFlowWithResultExpressions() throws Exception {
        URI resource = getClass().getResource("/corrupted/flow_with_result_expressions.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));

        List<RuntimeException> errors = result.getErrors();
        assertEquals(2, errors.size());

        validateExceptionMessage(
                errors.get(0),
                "Flow: 'flow_with_result_expressions' syntax is illegal. Error compiling result: 'SUCCESS'. " +
                        "Explicit values are not allowed for flow results. Correct format is: '- SUCCESS'.",
                "SUCCESS",
                ExecutableBuilder.FLOW_RESULTS_WITH_EXPRESSIONS_MESSAGE
        );
        validateExceptionMessage(
                errors.get(1),
                "Flow: 'flow_with_result_expressions' syntax is illegal. Error compiling result: 'CUSTOM'. " +
                        "Explicit values are not allowed for flow results. Correct format is: '- CUSTOM'.",
                "CUSTOM",
                ExecutableBuilder.FLOW_RESULTS_WITH_EXPRESSIONS_MESSAGE
        );
    }

    private void validateExceptionMessage(
            RuntimeException ex,
            String flowName,
            String resultName,
            String expressionMessage) {
        String errorMessage = ex.getMessage();
        assertTrue(errorMessage.contains(flowName));
        assertTrue(errorMessage.contains(resultName));
        assertTrue(errorMessage.contains(expressionMessage));
    }


    @Test
    public void testFlowNavigateNull() throws Exception {
        URI resource = getClass().getResource("/corrupted/flow_navigate_map.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("For step 'Step1' syntax is illegal.\n" +
                "Under property: 'navigate' there should be a list of values, but instead there is a map.\n" +
                "By the Yaml spec lists properties are marked with a '- ' (dash followed by a space)");
        throw result.getErrors().get(0);
    }

    @Test
    public void testFlowNavigateMultipleElementsForRule() throws Exception {
        URI resource = getClass().getResource("/corrupted/flow_navigate_multiple_elements_for_rule.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("For step 'Step1' syntax is illegal.\n" +
                "Each list item in the navigate section should contain exactly one key:value pair.");
        throw result.getErrors().get(0);
    }

    @Test
    public void testFlowNavigateIntKey() throws Exception {
        URI resource = getClass().getResource("/corrupted/flow_navigate_int_key.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("For step 'Step1' syntax is illegal.\n" +
                "Each key in the navigate section should be a string.");
        throw result.getErrors().get(0);
    }

    @Test
    public void testFlowNavigateIntValue() throws Exception {
        URI resource = getClass().getResource("/corrupted/flow_navigate_int_value.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("For step 'Step1' syntax is illegal.\n" +
                "Each value in the navigate section should be a string.");
        throw result.getErrors().get(0);
    }

    @Test
    public void testFlowWithUnreachableSteps() throws Exception {
        URI resource = getClass().getResource("/corrupted/unreachable_steps.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("Step: print_message2 is unreachable");
        throw result.getErrors().get(0);
    }

    @Test
    public void testFlowWithUnreachableOnFailureStep() throws Exception {
        URI resource = getClass().getResource("/corrupted/unreachable_on_failure_step.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() == 0);
    }

    @Test
    public void testFlowWithUnreachableStepReachableFromOnFailureStep() throws Exception {
        URI resource = getClass().getResource("/corrupted/unreachable_step_reachable_from_on_failure.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() == 0);
    }

    @Test
    public void testFlowWithUnreachableTasksOneReachableFromOnFailureTask() throws Exception {
        URI resource = getClass().getResource("/corrupted/unreachable_tasks_one_reachable_from_on_failure.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("Step: print_message2 is unreachable");
        throw result.getErrors().get(0);
    }

    @Test
    public void testNullPublishValue() throws Exception {
        URI resource = getClass().getResource("/corrupted/null_publish_value.sl").toURI();

        ExecutableModellingResult result = compiler.preCompileSource(SlangSource.fromFile(resource));
        assertTrue(result.getErrors().size() > 0);
        exception.expect(RuntimeException.class);
        exception.expectMessage("For step 'CheckWeather' syntax is illegal.");
        exception.expectMessage("Could not transform Output : {var_with_null_value=null} since it has a null value.");
        throw result.getErrors().get(0);
    }
}
