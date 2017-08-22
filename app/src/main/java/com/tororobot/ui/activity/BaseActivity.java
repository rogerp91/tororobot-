package com.tororobot.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.tororobot.TororobotAplications;
import com.tororobot.di.module.ActivityModule;

import java.util.ArrayList;
import java.util.List;

import dagger.ObjectGraph;

/**
 * Created by roger on 20/10/16.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private ObjectGraph activityGraph;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();

    }

    private void injectDependencies() {
        TororobotAplications application = (TororobotAplications) getApplication();
        List<Object> activityScopeModules = new ArrayList<>();
        activityScopeModules.add(new ActivityModule(this));
        activityGraph = application.buildGraphWithAditionalModules(activityScopeModules);
        inject(this);
    }

    private void inject(Object entityToGetInjected) {
        activityGraph.inject(entityToGetInjected);
    }


}