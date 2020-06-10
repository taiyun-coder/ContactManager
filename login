package com.example.contactmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    //set number of maximum attempt of login
    private int attemptCounter = 10;

    EditText loginName , loginPassword;
    TextView incorrectAttempts;
    Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginName = (EditText) findViewById(R.id.userName);
        loginPassword = (EditText) findViewById(R.id.userPassword);
        loginBtn = (Button) findViewById(R.id.loginButton);
        incorrectAttempts = (TextView) findViewById(R.id.numOfAttempt);
        incorrectAttempts.setText("Remaining Attempts: 10");

        //get the username and password once the user clicks the login button
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(loginName.getText().toString(), loginPassword.getText().toString());
            }
        });
    }

    //Check username and password
    private void validate(String userEnName , String userEnPassword){
        if((userEnName.equals("user1")) && (userEnPassword.equals("1234"))) {

            //goes to the main interface of the app once successfully login
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);

        }else{
            attemptCounter--;

            //disable the login button when run out of attempts
            if(attemptCounter == 0){
                incorrectAttempts.setText("Remaining Attempts: " + String.valueOf(attemptCounter) );
                loginBtn.setEnabled(false);
            }
        }
    }
}
