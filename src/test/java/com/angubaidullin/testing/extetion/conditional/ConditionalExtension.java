package com.angubaidullin.testing.extetion.conditional;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

public class ConditionalExtension implements ExecutionCondition {
    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext extensionContext) {
        return System.getProperty("skip") !=null
                ? ConditionEvaluationResult.disabled("test is skipped"):
                ConditionEvaluationResult.enabled("test is enabled");
    }
}
