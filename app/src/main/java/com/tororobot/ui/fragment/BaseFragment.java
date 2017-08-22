package com.tororobot.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.tororobot.TororobotAplications;
import com.tororobot.di.module.FragmentModule;

import java.util.ArrayList;
import java.util.List;

import dagger.ObjectGraph;

/**
 * Created by roger on 20/10/16.
 */

public abstract class BaseFragment extends Fragment {

    private ObjectGraph activityGraph;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();
    }

    protected abstract List<Object> getModules();

    private void injectDependencies() {
        TororobotAplications app = (TororobotAplications) getActivity().getApplication();
        List<Object> activityScopeModules = (getModules() != null) ? getModules() : new ArrayList<>();
        activityScopeModules.add(new FragmentModule(getActivity()));
        activityGraph = app.buildGraphWithAditionalModules(activityScopeModules);
        inject(this);
    }

    private void inject(Object entityToGetInjected) {
        activityGraph.inject(entityToGetInjected);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activityGraph = null;
    }

}