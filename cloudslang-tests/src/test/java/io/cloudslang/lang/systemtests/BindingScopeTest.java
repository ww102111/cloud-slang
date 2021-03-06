/**
 * ****************************************************************************
 * (c) Copyright 2014 Hewlett-Packard Development Company, L.P.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0 which accompany this distribution.
 * <p/>
 * The Apache License is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * *****************************************************************************
 */
package io.cloudslang.lang.systemtests;

import com.google.common.collect.Sets;
import io.cloudslang.lang.compiler.SlangSource;
import io.cloudslang.lang.compiler.SlangTextualKeys;
import io.cloudslang.lang.compiler.modeller.model.Executable;
import io.cloudslang.lang.compiler.modeller.model.Flow;
import io.cloudslang.lang.entities.CompilationArtifact;
import io.cloudslang.lang.entities.SystemProperty;
import io.cloudslang.lang.entities.bindings.Output;
import io.cloudslang.lang.entities.bindings.values.Value;
import io.cloudslang.lang.entities.utils.ExpressionUtils;
import java.util.HashSet;
import org.apache.commons.lang.StringUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.rules.ExpectedException;

/**
 * @author Bonczidai Levente
 * @since 3/18/2016
 */
public class BindingScopeTest extends SystemsTestsParent {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testStepPublishValues() throws Exception {
        URL resource = getClass().getResource("/yaml/binding_scope_flow.sl");
        URI operation = getClass().getResource("/yaml/binding_scope_op.sl").toURI();
        Set<SlangSource> path = Sets.newHashSet(SlangSource.fromFile(operation));
        CompilationArtifact compilationArtifact = slang.compile(SlangSource.fromFile(resource.toURI()), path);

        Map<String, Value> userInputs = Collections.emptyMap();
        Set<SystemProperty> systemProperties = Collections.emptySet();

        // trigger ExecutionPlan
        RuntimeInformation runtimeInformation = triggerWithData(compilationArtifact, userInputs, systemProperties);

        Map<String, StepData> executionData = runtimeInformation.getSteps();

        StepData stepData = executionData.get(FIRST_STEP_PATH);
        Assert.assertNotNull("step data is null", stepData);

        verifyStepPublishValues(stepData);
    }

    @Test
    public void testInputMissing() throws Exception {
        URL resource = getClass().getResource("/yaml/check_weather_missing_input.sl");
        CompilationArtifact compilationArtifact = slang.compile(SlangSource.fromFile(resource.toURI()), new HashSet<SlangSource>());

        Map<String, Value> userInputs = Collections.emptyMap();
        Set<SystemProperty> systemProperties = Collections.emptySet();

        exception.expect(RuntimeException.class);

        exception.expectMessage(new BaseMatcher<String>() {
            public void describeTo(Description description) {}
            public boolean matches(Object o) {
                String message = o.toString();
                return message.contains("Error running: 'check_weather_missing_input'") &&
                        message.contains("Error binding input: 'input_get_missing_input'") &&
                        message.contains("Error is: Error in running script expression: 'missing_input'") &&
                        message.contains("Exception is: name 'missing_input' is not defined");
            }
        });
        triggerWithData(compilationArtifact, userInputs, systemProperties);
    }

    private void verifyStepPublishValues(StepData stepData) {
        Map<String, Serializable> expectedPublishValues = new LinkedHashMap<>();
        expectedPublishValues.put("step1_publish_1", "op_output_1_value op_input_1_step step_arg_1_value");
        expectedPublishValues.put("step1_publish_2_conflict", "op_output_2_value");
        Map<String, Serializable> actualPublishValues = stepData.getOutputs();
        Assert.assertEquals("step publish values not as expected", expectedPublishValues, actualPublishValues);
    }

    @Test
    public void testFlowContextInStepPublishSection() throws Exception {
        URL resource = getClass().getResource("/yaml/binding_scope_flow_context_in_step_publish.sl");
        URI operation = getClass().getResource("/yaml/binding_scope_op.sl").toURI();
        Set<SlangSource> path = Sets.newHashSet(SlangSource.fromFile(operation));

        // pre-validation - step expression uses flow var name
        SlangSource flowSource = SlangSource.fromFile(resource.toURI());
        Executable flowExecutable = slangCompiler.preCompile(flowSource);
        String flowVarName = "flow_var";
        Assert.assertEquals(
                "Input name should be: " + flowVarName,
                flowVarName,
                flowExecutable.getInputs().get(0).getName()
        );
        @SuppressWarnings("unchecked")
        List<Output> stepPublishValues = (List<Output>) ((Flow) flowExecutable)
                .getWorkflow()
                .getSteps()
                .getFirst()
                .getPostStepActionData()
                .get(SlangTextualKeys.PUBLISH_KEY);
        Assert.assertEquals(
                "Step expression should contain: " + flowVarName,
                flowVarName,
                StringUtils.trim(ExpressionUtils.extractExpression(stepPublishValues.get(0).getValue().get()))
        );

        CompilationArtifact compilationArtifact = slang.compile(flowSource, path);

        Map<String, Value> userInputs = Collections.emptyMap();
        Set<SystemProperty> systemProperties = Collections.emptySet();

        exception.expect(RuntimeException.class);
        exception.expectMessage("flow_var");
        exception.expectMessage("not defined");

        // trigger ExecutionPlan
        triggerWithData(compilationArtifact, userInputs, systemProperties);
    }

}
