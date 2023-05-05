package com.cursoandroid.olx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.cursoandroid.olx.R;
import com.cursoandroid.olx.activity.ui.AccessViewModel;
import com.cursoandroid.olx.config.FirebaseConfig;
import com.cursoandroid.olx.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;

import java.util.Objects;

public class AccessActivity extends AppCompatActivity {
    private final FirebaseAuth auth = FirebaseConfig.getFirebaseAuth();
    private ProgressBar loadingProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access);
        final EditText usernameEditText = findViewById(R.id.etLoginEmail);
        final EditText passwordEditText = findViewById(R.id.etLoginPassword);
        final Button accessButton = findViewById(R.id.btnAccess);
        loadingProgressBar = findViewById(R.id.loginProgressBar);
        final Switch tipoAcesso = findViewById(R.id.swAccess);

        AccessViewModel accessViewModel = new ViewModelProvider(this, new AccessViewModel.LoginViewModelFactory())
                .get(AccessViewModel.class);
        accessViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            accessButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                accessViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                access(usernameEditText.getText().toString(), passwordEditText.getText().toString(),
                        tipoAcesso.isChecked());
            }
            return false;
        });

        accessButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            access(usernameEditText.getText().toString(), passwordEditText.getText().toString(),
                    tipoAcesso.isChecked());
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            updateUiWithUser();
        }
    }

    private void updateUiWithUser() {
        startActivity(new Intent(this, AnunciosActivity.class));
    }

    private void showLoginFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void accessFailed(@NonNull Task<AuthResult> task){
        String excecao;
        try {
            throw Objects.requireNonNull(task.getException());
        }catch (FirebaseAuthInvalidCredentialsException ignored){
            excecao = getString(R.string.invalid_credentials);
        }catch (FirebaseAuthInvalidUserException | FirebaseAuthUserCollisionException ignored){
            excecao = getString(R.string.invalid_username);
        } catch (Exception e){
            excecao = getString(R.string.login_failed) + ": " + e.getMessage();
            e.printStackTrace();
        }
        showLoginFailed(excecao);
    }

    private void access(String username, String password, boolean isRegister) {
        Usuario usuario = new Usuario(null, username, password);

        OnCompleteListener<AuthResult> completeListener = task -> {
            loadingProgressBar.setVisibility(View.GONE);
            if (task.isSuccessful()){
                updateUiWithUser();
            }else {
                accessFailed(task);
            }
        };

        if (isRegister){
            auth.createUserWithEmailAndPassword(usuario.getEmail(),usuario.getSenha())
                    .addOnCompleteListener(completeListener);
        }else{
            auth.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                    .addOnCompleteListener(completeListener);
        }
    }
}