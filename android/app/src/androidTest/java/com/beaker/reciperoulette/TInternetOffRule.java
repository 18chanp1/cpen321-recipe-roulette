package com.beaker.reciperoulette;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class TInternetOffRule implements TestRule {
    @NonNull
    @Override
    public Statement apply(@NonNull Statement base, @NonNull Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("svc wifi disable");
                InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("svc data disable");
                try {
                    base.evaluate();
                } finally {
                    InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("svc wifi enable");
                    InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("svc data enable");
                }
            }
        };
    }
}
