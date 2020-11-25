package com.sargam.college.socialmediaappfirestoretutorial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText mNameET;
    EditText mEmailEt;
    EditText mPasswordEt;
    Button mSignUpBTN;
    FirebaseAuth mAuth;
    CollectionReference mCollectionRef;
    String name,email,password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNameET = findViewById(R.id.name);
        mEmailEt = findViewById(R.id.email);
        mPasswordEt = findViewById(R.id.password);
        mSignUpBTN = findViewById(R.id.sign_up);
        mAuth = FirebaseAuth.getInstance();
        mCollectionRef = FirebaseFirestore.getInstance().collection("Users");

        mSignUpBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkDataForSignUp();

            }
        });



    }

    private void checkDataForSignUp(){
        name = mNameET.getText().toString().trim();
        email = mEmailEt.getText().toString().trim();
        password = mPasswordEt.getText().toString().trim();

        if(name.isEmpty()||name.equals("")||email.isEmpty()||email.equals("")||password.isEmpty()||password.equals("")){
            Toast.makeText(this,"Invalid Credentials",Toast.LENGTH_SHORT).show();
        }
        else{

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        Toast.makeText(MainActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                    }
                    else{
                        saveUserData();

                    }
                }
            });

            }

        }

        private void saveUserData(){

            Map<String,String> userDataMap = new HashMap<>();
            userDataMap.put("Name",name);
            userDataMap.put("Email",email);
            userDataMap.put("Password",password);
            userDataMap.put("UniqueId",mAuth.getUid());
            mCollectionRef.document(mAuth.getUid()).set(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        Toast.makeText(MainActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this,MoreUserInfo.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);


                    }
                }
            });




        }

    }

