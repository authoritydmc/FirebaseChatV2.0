package chatapp.beast.firebasechat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {
FirebaseAuth mAuth;
private int Login_attempt_count=0;
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
private Button _loginButton;
private  TextView _signupLink;
private EditText _emailText,_passwordText;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();
_loginButton=findViewById(R.id.btn_login);
        _signupLink=findViewById(R.id.link_signup);
        _emailText=findViewById(R.id.input_email);
        _passwordText=findViewById(R.id.input_password);
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });
findViewById(R.id.link_reset_password).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        String email = _emailText.getText().toString();
        if(!validate()) {return;}
        Toast.makeText(login.this,"Password being reset for "+email,Toast.LENGTH_SHORT).show();
        if (!_emailText.getText().toString().isEmpty()){
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(login.this,"Reset link sent...Goto your email account",Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(login.this,"Error in sending Reset Email...",Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    else
        Toast.makeText(getBaseContext(),"Enter Email id First then click on forgot password",Toast.LENGTH_LONG).show();
    }
});
        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");
Login_attempt_count++;
if(Login_attempt_count>=3)
    Toast.makeText(login.this,"Don't have account?Create One ",Toast.LENGTH_SHORT).show();

        if (!validate()) {
            onLoginFailed("can not validate (E8701)");
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(login.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            progressDialog.dismiss();
                            onLoginSuccess();


                        } else {
                            // If sign in fails, display a message to the user.
                            ;
                          onLoginFailed(task.getException().getLocalizedMessage()); progressDialog.dismiss();
                        }

                        // ...
                    }
                });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
onLoginSuccess();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Intent intent=new Intent(login.this,MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void onLoginFailed(String msg) {
        Toast.makeText(getBaseContext(), "Login failed\n"+msg, Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
