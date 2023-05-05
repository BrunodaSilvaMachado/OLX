package com.cursoandroid.olx.activity.ui;

import android.util.Patterns;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.cursoandroid.olx.R;

public class AccessViewModel extends ViewModel {

    private final MutableLiveData<FormState> loginFormState = new MutableLiveData<>();

    public AccessViewModel() {
    }

    public LiveData<FormState> getLoginFormState() {
        return loginFormState;
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new FormState(R.string.invalid_username,null, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new FormState(null, null,  R.string.invalid_password));
        } else {
            loginFormState.setValue(new FormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    public static class LoginViewModelFactory implements ViewModelProvider.Factory {

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(AccessViewModel.class)) {
                return (T) new AccessViewModel();
            } else {
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        }
    }
}