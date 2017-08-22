package com.tororobot.login;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Patterns;

import com.tororobot.R;
import com.tororobot.domain.model.ResponseLogin;
import com.tororobot.util.Networks;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by roger on 19/10/16.
 */

public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View view;
    private LoginContract.LoginInteractor loginInteractor;

    @Inject
    Context context;

    @Inject
    public LoginPresenter(LoginContract.LoginInteractor loginInteractor) {
        this.loginInteractor = loginInteractor;
    }

    @Override
    public void setView(@NonNull LoginContract.View view) {
        checkNotNull(view, "View cannot be null!");
        this.view = view;
    }

    @Override
    public void attemptLogin(@NonNull String email, @NonNull String password) {
        boolean cancel = false;

        // Validar datos
        if (TextUtils.isEmpty(password)) {
            cancel = true;
            view.showPasswordError(context.getResources().getString(R.string.not_empty_user));
        } else {
            if (!(password.length() > 3)) {
                cancel = true;
                view.showPasswordError(context.getResources().getString(R.string.min_length_password));
            }
        }

        if (TextUtils.isEmpty(email)) {
            cancel = true;
            view.showEmailError(context.getResources().getString(R.string.not_empty_user));
        } else {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                cancel = true;
                view.showEmailError(context.getResources().getString(R.string.error_email));
            }
        }

        if (!cancel) {
            if (Networks.isOnline(context)) {
                view.showDialog();
                attemptLoginInteractor(email, password);
            } else {
                view.showNoDialog();
                view.showNetworkError();
            }
        }


    }

    private void attemptLoginInteractor(String email, String password) {
        loginInteractor.execute(email, password, new LoginContract.LoginInteractor.Callback() {
            @Override
            public void onSuccess() {
                if (view.isActive()) {
                    view.showNoDialog();
                    view.onSuccess();
                }
            }

            @Override
            public void onUnauthorized() {
                if (view.isActive()) {
                    view.showNoDialog();
                    view.showUnauthorizedError();
                }
            }

            @Override
            public void onServerError() {
                if (view.isActive()) {
                    view.showNoDialog();
                    view.showServerError();
                }
            }

            @Override
            public void onError() {
                if (view.isActive()) {
                    view.showNoDialog();
                    view.showError();
                }
            }
        });
    }

}