package com.example.recipesapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.recipesapp.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SingUpActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        EditText etEmail = findViewById(R.id.et_email);
        EditText etPassword = findViewById(R.id.et_password);
        EditText etReEnterPassword = findViewById((R.id.et_passwordRe));

        Button btnSingUp = findViewById(R.id.btn_singUp);

        TextView signup = findViewById(R.id.button_back);



        signup.setOnClickListener(v -> {
            Intent intent = new Intent(SingUpActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnSingUp.setOnClickListener(v -> {

         String email = etEmail.getText().toString().trim();
         String password = etPassword.getText().toString().trim();
         String reEnterPassword = etReEnterPassword.getText().toString().trim();
            if (password.equals(reEnterPassword)) {
                // register user firebase
                createNewUser(email, password);
            } else {
                Toast.makeText(SingUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }


        });
    }

    private void createNewUser(String email, String password) {
        FirebaseApp.initializeApp(this);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // User creation successful
                        FirebaseUser user = auth.getCurrentUser();
                        String UserID = user.getUid();

                        // Write user data to Firebase Realtime Database
                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(UserID);
                        usersRef.child("email").setValue(email);
                        usersRef.child("id").setValue(UserID);
                        usersRef.child("image").setValue("");
                        usersRef.child("name").setValue("");

                        // Navigate to MainActivity or any other activity after successful registration
                        Intent intent = new Intent(SingUpActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        // User creation failed
                        Toast.makeText(SingUpActivity.this, "User not created", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}