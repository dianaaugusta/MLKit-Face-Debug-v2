package com.example.mlkitintegration;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

public class MyCameraLifecycleOwner implements LifecycleOwner {
    private final LifecycleRegistry mLifecycleRegistry;

    public MyCameraLifecycleOwner() {
        mLifecycleRegistry = new LifecycleRegistry(this);
        mLifecycleRegistry.setCurrentState(Lifecycle.State.RESUMED);
    }

    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }
}
