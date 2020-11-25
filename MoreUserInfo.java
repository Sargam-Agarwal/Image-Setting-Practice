package com.sargam.college.socialmediaappfirestoretutorial;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MoreUserInfo extends AppCompatActivity {

    private static final int REQ_CODE_PERMISSION = 200 ;
    CircleImageView mUserDp;
    EditText mUserMoreInfoET;
    Button mSaveUserMoreInfoBTN;
    FloatingActionButton mWriteBTN;
    private int REQ_CODE_IMAGE = 100;
    FirebaseAuth mAuth;
    String mUniqueId;
    DocumentReference mDocumentRef;
    StorageReference mStorageRefDP;

    private Uri mUri;
    String permissions[] = {Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_user_info);
        mUserDp = findViewById(R.id.profile_image);
        mUserMoreInfoET = findViewById(R.id.more_info_edit_text);
        mSaveUserMoreInfoBTN = findViewById(R.id.more_info_save_button);
        mWriteBTN = findViewById(R.id.write_floating_btn);
        mAuth = FirebaseAuth.getInstance();
        mUniqueId = mAuth.getUid();
        mDocumentRef = FirebaseFirestore.getInstance().collection("Users").document(mUniqueId);
        requestFunction();

        mUserDp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQ_CODE_IMAGE);
            }
        });

        mSaveUserMoreInfoBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadUserProfileDetails();
            }
        });


        mWriteBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    private void uploadUserProfileDetails() {


        mStorageRefDP = FirebaseStorage.getInstance().getReference().child("Profile Pix");
        UploadTask task = mStorageRefDP.child(mUniqueId).putFile(mUri);
        task.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){

                    task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String mDpUrl = uri.toString();
                            saveUserData(mDpUrl);
                        }
                    });

                }
                else{
                    Toast.makeText(MoreUserInfo.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });






    }

    private void saveUserData(String uri) {
        String userMoreInfo = mUserMoreInfoET.getText().toString().trim();
        Map<String,Object> userData = new HashMap<>();
        userData.put("About",userMoreInfo );
        userData.put("Dp Url",uri);
        mDocumentRef.update(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MoreUserInfo.this, "Update Successful !", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MoreUserInfo.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_IMAGE && resultCode == RESULT_OK) {
            CropImage.activity(data.getData()).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).setOutputCompressQuality(50).start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null) {
                mUri = result.getUri();
                mUserDp.setImageURI(mUri);


            }


        }
    }
    private void requestFunction(){
        if(ActivityCompat.checkSelfPermission(this,permissions[0])!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,permissions,REQ_CODE_PERMISSION);
        }
    }




}
