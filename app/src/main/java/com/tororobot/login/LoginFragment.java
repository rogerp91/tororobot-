package com.tororobot.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.tororobot.R;
import com.tororobot.di.module.InteractorModule;
import com.tororobot.login.LoginContract.Presenter;
import com.tororobot.service.CommandSocketService;
import com.tororobot.ui.activity.MainActivity;
import com.tororobot.ui.fragment.BaseFragment;
import com.tororobot.util.MS;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginFragment extends BaseFragment implements LoginContract.View {


    public static LoginFragment newInstance() {
        return new LoginFragment();
    }


    // Input
    @BindView(R.id.input_email)
    TextInputEditText inputEmail;
    @BindView(R.id.input_password)
    TextInputEditText inputPassword;

    @BindView(R.id.input_layout_email)
    TextInputLayout inputLayoutEmail;
    @BindView(R.id.input_layout_password)
    TextInputLayout inputLayoutPassword;

    @BindView(R.id.scroll_form)
    View mForm;

    @Inject
    Presenter presenter;

    @Inject
    MS ms;

    private MaterialDialog dialogLogin = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
//        MaterialDialog.Builder builderLogin = new MaterialDialog.Builder(getActivity()).title(R.string.title_loading).content(R.string.loading).progress(true, 0).progressIndeterminateStyle(false);
//        dialogLogin = builderLogin.build();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.setView(this);
        inputEmail.addTextChangedListener(textWatcher);
        inputPassword.addTextChangedListener(textWatcher);
        inputPassword.setOnEditorActionListener((textView, i, keyEvent) -> {
            boolean check = false;
            if (i == EditorInfo.IME_ACTION_UNSPECIFIED) {
//                presenter.attemptLogin(inputEmail.getText().toString(), inputPassword.getText().toString());
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                check = true;
            }
            return check;
        });
    }

    @Override
    public void onSuccess() {
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }

    @Override
    public void showDialog() {
        dialogLogin.show();
    }

    @Override
    public void showNoDialog() {
        dialogLogin.dismiss();
    }

    @Override
    public void showEmailError(@NonNull String error) {
        ms.showSnackbar(getView(), error, ContextCompat.getColor(getActivity(), R.color.alert), 0);
    }

    @Override
    public void showPasswordError(@NonNull String error) {
        ms.showSnackbar(getView(), error, ContextCompat.getColor(getActivity(), R.color.alert), 0);
    }

    @Override
    public void showError() {
        ms.showSnackbar(getView(), getActivity().getResources().getString(R.string.error_occurred2), ContextCompat.getColor(getActivity(), R.color.alert), 0);
    }

    @Override
    public void showNetworkError() {
        ms.showSnackbar(getView(), getActivity().getResources().getString(R.string.no_connection), ContextCompat.getColor(getActivity(), R.color.alert), 0);
    }

    @Override
    public void showUnauthorizedError() {
        ms.showSnackbar(getView(), getActivity().getResources().getString(R.string.error_email_password), ContextCompat.getColor(getActivity(), R.color.alert), 0);
    }

    @Override
    public void showServerError() {
        ms.showSnackbar(getView(), getActivity().getResources().getString(R.string.error_many), ContextCompat.getColor(getActivity(), R.color.alert), 0);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    protected List<Object> getModules() {
        List<Object> objects = new ArrayList<>();
        objects.add(new InteractorModule());
        return objects;
    }

    @OnClick(R.id.btn_login)
    void clickBtnLogin() {
        CommandSocketService.actionStart(getActivity());
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
//        presenter.attemptLogin(inputEmail.getText().toString(), inputPassword.getText().toString());
    }

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            inputLayoutEmail.setErrorEnabled(false);
            inputLayoutPassword.setErrorEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}