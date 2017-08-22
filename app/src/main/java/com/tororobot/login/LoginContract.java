package com.tororobot.login;

import android.support.annotation.NonNull;

import com.tororobot.domain.model.ResponseLogin;
import com.tororobot.presenter.BaseView;

/**
 * Created by roger on 19/10/16.
 */

public interface LoginContract {

    /**
     * TODO: Vista Login
     */
    interface View extends BaseView {

        void onSuccess();

        void showDialog();

        void showNoDialog();

        void showEmailError(@NonNull String error);

        void showPasswordError(@NonNull String error);

        void showError();

//        void showGooglePlayServicesDialog(int errorCode);
//
//        void showGooglePlayServicesError();

        void showNetworkError();

        void showUnauthorizedError();

        void showServerError();

    }

    /**
     * TODO: Presentador Login
     */
    interface Presenter {

        void setView(@NonNull View view);

        void attemptLogin(@NonNull String email, @NonNull String password);

    }

    /**
     * TODO: Interactor Login
     */
    interface LoginInteractor {

        interface Callback {

            void onSuccess();

            void onUnauthorized();

            void onServerError();

            void onError();

        }

        void execute(@NonNull String email, @NonNull String password, @NonNull Callback call);

    }

}