package com.angubaidullin.testing.postprocessing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Field;

public class PostProcessingExtension implements TestInstancePostProcessor {
    @Override
    public void postProcessTestInstance(Object o, ExtensionContext extensionContext) throws Exception {
        System.out.println("Post processing test instance");
        Field[] declaredFields = o.getClass().getDeclaredFields();
        for (Field field: declaredFields) {
            if (field.isAnnotationPresent(Test.class)) {

            }
        }
    }
}
