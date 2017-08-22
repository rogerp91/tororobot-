package com.tororobot.login;

import android.support.annotation.NonNull;

import com.tororobot.executor.Executor;
import com.tororobot.executor.Interactor;
import com.tororobot.executor.MainThread;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by roger on 19/10/16.
 */

public class LoginInteractor implements LoginContract.LoginInteractor, Interactor {

    private String TAG = LoginInteractor.class.getSimpleName();


    private LoginContract.LoginInteractor.Callback callback;
    private String password;
    private String email;

    private Executor executor;
    private MainThread mainThread;

    @Inject
    LoginInteractor(Executor executor, MainThread mainThread) {
        this.executor = executor;
        this.mainThread = mainThread;
    }

    @Override
    public void execute(@NonNull String email, @NonNull String password, @NonNull Callback call) {
        checkNotNull(call, "Callback cannot be null!");
        checkArgument(!email.isEmpty(), "Email can not be empty");
        checkArgument(!password.isEmpty(), "Email can not be empty");
        this.callback = call;
        this.password = password;
        this.email = email;
        this.executor.run(this);
    }

    @Override
    public void run() {
        mainThread.post(new Interactor() {
            @Override
            public void run() {
                callback.onSuccess();

//                AndroidNetworking.post(URLs.getAbsoluteUrl("login"))
//                        .addBodyParameter(Constants.EMAIL, email)
//                        .addBodyParameter(Constants.PASSWORD, password)
//                        .setTag("login")
//                        .setPriority(Priority.HIGH)
//                        .build()
//                        .getAsJSONObject(new JSONObjectRequestListener() {
//                            @Override
//                            public void onResponse(JSONObject response) {
//                                Log.d(TAG, "onResponse: ");
//                            }
//
//                            @Override
//                            public void onError(ANError error) {
//                                Log.e(TAG, "onError: " + error.getMessage());
//                            }
//                        });
            }
        });
    }
}