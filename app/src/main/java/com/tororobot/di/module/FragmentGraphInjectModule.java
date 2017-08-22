package com.tororobot.di.module;

import com.tororobot.chat.ChatFragment;
import com.tororobot.login.LoginFragment;
import com.tororobot.ui.fragment.MainFragment;

import dagger.Module;

/**
 * Created by Roger Patiño on 22/12/2015.
 */
@Module(
        injects = {
                LoginFragment.class,
                ChatFragment.class,
                MainFragment.class
        },
        complete = false,
        library = true
)
public class FragmentGraphInjectModule {
}
