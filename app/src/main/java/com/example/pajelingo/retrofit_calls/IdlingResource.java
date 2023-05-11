package com.example.pajelingo.retrofit_calls;

import androidx.test.espresso.idling.CountingIdlingResource;

public abstract class IdlingResource {
    protected static CountingIdlingResource idlingResource = null;

    public static CountingIdlingResource getIdlingResource() {
        if (idlingResource == null) {
            idlingResource = new CountingIdlingResource("IdlingResource");
        }

        return idlingResource;
    }

    protected void incrementIdlingResource() {
        if (idlingResource != null) {
            idlingResource.increment();
        }
    }

    protected void decrementIdlingResource() {
        if (idlingResource != null) {
            idlingResource.decrement();
        }
    }
}
