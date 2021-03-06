package ei.eseptiyadi.notesapp.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.prefs.Preferences;

import ei.eseptiyadi.notesapp.R;
import ei.eseptiyadi.notesapp.model.auth.RequestLogin;
import ei.eseptiyadi.notesapp.network.ApiServices;
import ei.eseptiyadi.notesapp.network.RetrofitClient;
import ei.eseptiyadi.notesapp.preferences.Session;
import ei.eseptiyadi.notesapp.views.DashboardActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText logUsername, logPassword;
    Button btnActionLogin;
    ProgressDialog pd;

    String getUsername, getPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Bundle getPackageNewUser = getIntent().getExtras();

        logUsername = (EditText)findViewById(R.id.edtLogUsername);
        logPassword = (EditText)findViewById(R.id.edtLogPassword);
        btnActionLogin = (Button)findViewById(R.id.btnLogSignin);

        if (getIntent().getExtras() != null) {
            getUsername = getPackageNewUser.getString("key_username");
            getPassword = getPackageNewUser.getString("key_password");

            logUsername.setText(getUsername);
            logPassword.setText(getPassword);


        } else {
            // Toast.makeText(this, "Belum ada aksi", Toast.LENGTH_SHORT).show();
        }

        btnActionLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cekUsername, cekPassword;
                cekUsername = logUsername.getText().toString();
                cekPassword = logPassword.getText().toString();

                if (cekUsername.equals("")) {
                    logUsername.requestFocus();
                    logUsername.setError("Username anda belum diisi!");
                } else if (cekPassword.equals("")) {
                    logPassword.requestFocus();
                    logPassword.setError("Password anda belum diisi!");
                } else {

                    moudleLoginUser(cekUsername, cekPassword);
                    // Log.d("Log", "Data User : " + getUsername + " " + getPassword + " " + getHash + " " + getLevel);
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Session.getStatus_Userlog(getBaseContext())) {
            startActivity(new Intent(getBaseContext(), DashboardActivity.class));
            finish();
        }
    }

    private void moudleLoginUser(String cekUsername, String cekPassword) {
        // Log.d("Log", "Module Login : " + cekUsername + " " + cekPassword + " " + getHash + " " + getLevel);
        ApiServices apiServices = RetrofitClient.getInstance();
        Call<RequestLogin> requestLoginCall = apiServices.reqLoginUser(cekUsername, cekPassword);

        requestLoginCall.enqueue(new Callback<RequestLogin>() {
            @Override
            public void onResponse(Call<RequestLogin> call, Response<RequestLogin> response) {
                if (response.isSuccessful()){

                    int codeResponse = response.body().getCode();

                    if (codeResponse == 200) {

                        String id, username, key, hash, level;
                        id = response.body().getId().toString();
                        username = response.body().getUser().toString();
                        key = response.body().getPass().toString();
                        hash = response.body().getHash().toString();
                        level = response.body().getLevel().toString();

                        Session.setRegisesi_User(getBaseContext(), username);
                        Session.setRegisesi_Pass(getBaseContext(), key);
                        Session.setRegisesi_Hash(getBaseContext(), hash);
                        Session.setRegisesi_Lvl(getBaseContext(), level);

                        setSaveSession();
//
//                        Intent kirimresultLogin = new Intent(LoginActivity.this, DashboardActivity.class);
//                        startActivity(kirimresultLogin);
                        Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else if (codeResponse == 404) {
                        Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RequestLogin> call, Throwable t) {

            }
        });
    }

    public void registerNow(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));

    }

    public void clearLogField(View view) {
        logUsername.setText("");
        logPassword.setText("");
    }

    private void setSaveSession() {
        Session.setIdentify_Userlog(getBaseContext(), Session.getRegisesi_User(getBaseContext()));
        Session.setStatus_Userlog(getBaseContext(), true);
        startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
        finish();
    }
}