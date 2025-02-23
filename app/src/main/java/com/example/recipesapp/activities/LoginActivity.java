package com.example.recipesapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recipesapp.R;
import com.example.recipesapp.models.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    // פונקציה להמרת DataSnapshot לרשימה של מחרוזות
    private List<String> getListFromSnapshot(DataSnapshot snapshot) {
        List<String> list = new ArrayList<>();
        if (snapshot.exists()) {
            Object data = snapshot.getValue();
            if (data instanceof List) {
                list = (List<String>) data;
            } else if (data instanceof Map) {
                Map<String, String> map = (Map<String, String>) data;
                list.addAll(map.values());
            }
        }
        return list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText etEmail = findViewById(R.id.et_email);
        EditText etPassword = findViewById(R.id.et_password);
        Button login = findViewById(R.id.btn_login);
        TextView signup = findViewById(R.id.buttonRegister);

        signup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SingUpActivity.class);
            startActivity(intent);
        });

        login.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            FirebaseApp.initializeApp(this);
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (currentUser != null) {
                                String userId = currentUser.getUid();
                                String userEmail = currentUser.getEmail();
                                // ערכי ברירת מחדל – ניתן לעדכן בהתאם לדרישות
                                String defaultUserName = "Username";
                                String defaultUserImage = "url_to_image";

                                DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users");
                                database.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            // קריאה ידנית לשדות מתוך ה-snapshot
                                            String nameFromDb = snapshot.child("name").getValue(String.class);
                                            String emailFromDb = snapshot.child("email").getValue(String.class);
                                            String imageFromDb = snapshot.child("image").getValue(String.class);

                                            List<String> favoriteRecipes = getListFromSnapshot(snapshot.child("favoriteRecipes"));
                                            List<String> externalRecipes = getListFromSnapshot(snapshot.child("externalRecipes"));

                                            if (nameFromDb == null) {
                                                nameFromDb = defaultUserName;
                                            }
                                            if (emailFromDb == null) {
                                                emailFromDb = userEmail;
                                            }
                                            if (imageFromDb == null) {
                                                imageFromDb = defaultUserImage;
                                            }

                                            User user = new User(userId, nameFromDb, emailFromDb, imageFromDb, favoriteRecipes, externalRecipes);

                                            database.child(userId).setValue(user)
                                                    .addOnSuccessListener(aVoid -> {
                                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                        startActivity(intent);
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(LoginActivity.this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    });
                                        } else {
                                            // אם אין מידע קיים – יצירת משתמש חדש עם רשימות ריקות
                                            User user = new User(userId, defaultUserName, userEmail, defaultUserImage,
                                                    new ArrayList<>(), new ArrayList<>());
                                            database.child(userId).setValue(user)
                                                    .addOnSuccessListener(aVoid -> {
                                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                        startActivity(intent);
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(LoginActivity.this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
