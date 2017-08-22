package com.tororobot.login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tororobot.R;

import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, LoginFragment
                            .newInstance())
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .commit();

        }
    }

}