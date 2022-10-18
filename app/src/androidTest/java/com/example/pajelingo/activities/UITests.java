package com.example.pajelingo.activities;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.After;

import java.io.IOException;

public class UITests {
    public ActivityScenario<MainActivity> activityScenario;
    protected final Context context = ApplicationProvider.getApplicationContext();

    @After
    public void tearDown() throws IOException {
        if (activityScenario != null) {
            activityScenario.close();
        }
    }
}
