package br.com.inf.vacinado.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.com.inf.vacinado.DAO.LoginOfflineDAO;
import br.com.inf.vacinado.Model.Usuario;
import br.com.inf.vacinado.R;

import static br.com.inf.vacinado.R.string.login_error_message;

public class Login extends AppCompatActivity implements View.OnClickListener {

    protected EditText emailEditText;
    protected EditText passwordEditText;
    MaterialDialog dialog;
    CompoundButton checkBox;
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser user;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button bttnEntrar = (Button) findViewById(R.id.bttnEntrar);
        bttnEntrar.setOnClickListener(this);
        Button bttnCadastrar = (Button) findViewById(R.id.bttnCadastrar);
        bttnCadastrar.setOnClickListener(this);

        // Iniciando o FirebaseAuth
        try {
            mFirebaseAuth = FirebaseAuth.getInstance();
            user = Usuario.getFireBaseUser();
        } catch (Exception e) {
            mFirebaseAuth = null;
        }

        emailEditText = (EditText) findViewById(R.id.edtEmail);
        passwordEditText = (EditText) findViewById(R.id.edtSenha);
        checkBox = (CompoundButton) findViewById(R.id.checkBoxLembrar);
        SharedPreferences prefs = getSharedPreferences("login", MODE_PRIVATE);
        LoginOfflineDAO.recuperarLogin(prefs, emailEditText, passwordEditText, checkBox);
    }

    public void onStop() {
        super.onStop();
    }

    /**
     * Ao pressionar o botão "Back", existe o perigo do usuário voltar para a tela inicial do
     * sistema. onBackPressed alterado para evitar que isto ocorra.
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        Intent it;
        switch (v.getId()) {
            case R.id.bttnCadastrar:
                it = new Intent(this, Cadastro.class);
                startActivity(it);
                break;
            case R.id.bttnEntrar:

                final String email = emailEditText.getText().toString().trim();
                final String password = passwordEditText.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Snackbar.make(findViewById(android.R.id.content), login_error_message, Snackbar.LENGTH_LONG).show();
                } else {
                    dialog = new MaterialDialog.Builder(this).content(R.string.realizando_login).progress(true, 0).show();
                    mFirebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        SharedPreferences prefs = getSharedPreferences("login", MODE_PRIVATE);
                                        LoginOfflineDAO.persistirLogin(prefs, emailEditText, passwordEditText, checkBox);
                                        dialog.dismiss();
                                        Intent intent = new Intent(Login.this, Carteira.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.putExtra("Modo offline", false);
                                        startActivity(intent);
                                    } else {
                                        dialog.dismiss();
                                        String erro = task.getException().getMessage();
                                        if (erro.equals("The password is invalid or the user does not have a password.")) {
                                            Snackbar.make(findViewById(android.R.id.content),
                                                    R.string.login_error_message_return, Snackbar.LENGTH_LONG).show();
                                        } else if (erro.equals("A network error (such as timeout, interrupted connection or unreachable host) has occurred.")) {
                                            try {

                                                Log.e("FireBaseUser", String.valueOf(Usuario.getFireBaseUser()));
                                                Log.e("FireBaseUserID", String.valueOf(Usuario.getmUserId()));

                                                SharedPreferences prefs = getSharedPreferences("login", MODE_PRIVATE);
                                                if (LoginOfflineDAO.validarLoginOffline(prefs, emailEditText, passwordEditText)) {
                                                    LoginOfflineDAO.persistirLogin(prefs, emailEditText, passwordEditText, checkBox);
                                                    Intent intent = new Intent(Login.this, Carteira.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    intent.putExtra("Modo offline", true);
                                                    startActivity(intent);
                                                } else {
                                                    Snackbar.make(findViewById(android.R.id.content),
                                                            R.string.login_error_message_return, Snackbar.LENGTH_LONG).show();
                                                }
                                            } catch (java.lang.NullPointerException e) {
                                                Snackbar.make(findViewById(android.R.id.content),
                                                        R.string.user_not_found, Snackbar.LENGTH_LONG).show();
                                            }
                                        } else {
                                            Snackbar.make(findViewById(android.R.id.content),
                                                    R.string.general_error, Snackbar.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            });
                }
                break;
        }
    }
}
