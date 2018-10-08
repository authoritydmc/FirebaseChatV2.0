package chatapp.beast.firebasechat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;


public class SettingActivity extends AppCompatActivity {
    private static int gallery_pick = 1;
    private static int change_profile_request_code = 2;
    private DatabaseReference dbReference;
    private Toolbar toolbar;
    private TextView username, userstatus;
    private CircleImageView user_profile_pic;
    private CircleImageView btn_change_profile_pic;
    private StorageReference storageReference,thumb_image_reference;
    private ProgressDialog progressBar;
    private ImageButton change_sts_btn;
    private String user_name, user_status;
    private Bitmap thumb_bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        toolbar = (Toolbar) findViewById(R.id.settings_app_bar);
        setSupportActionBar(toolbar);
        progressBar = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);

        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dbReference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        storageReference = FirebaseStorage.getInstance().getReference().child("profile_images ");
        thumb_image_reference=FirebaseStorage.getInstance().getReference().child("thumb_images");
        user_profile_pic = (CircleImageView) findViewById(R.id.settings_profile_pic);
        username = findViewById(R.id.settings_username);
        userstatus = findViewById(R.id.settings_status);
        btn_change_profile_pic = findViewById(R.id.settings_change_profile_btn);
        change_sts_btn = findViewById(R.id.edit_status_btn);

        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("user_name").getValue().toString();
                String status = dataSnapshot.child("user_status").getValue().toString();
                String image_url = dataSnapshot.child("user_image").getValue().toString();
                Picasso.get().load(image_url).placeholder(R.drawable.default_profile_pic).into(user_profile_pic);
                username.setText("Username:" + name);
                user_name = name;
                user_status = status;
                userstatus.setText("Status:" + status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        btn_change_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryintent = new Intent();
                galleryintent.setAction(Intent.ACTION_GET_CONTENT);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent, gallery_pick);
            }
        });
        change_sts_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, Change_profile.class);
                intent.putExtra("old_username", user_name);
                intent.putExtra("old_user_status", user_status);
                startActivityForResult(intent, change_profile_request_code);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == gallery_pick && resultCode == RESULT_OK && data != null) {
            Uri Imageuri = data.getData();
            CropImage.activity(Imageuri).setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == change_profile_request_code && resultCode == RESULT_OK) {

            String new_username = data.getStringExtra("new_username");
            String new_usersts = data.getStringExtra("old_user_sts");
            dbReference.child("user_name").setValue(new_username);
            dbReference.child("user_status").setValue(new_usersts).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(SettingActivity.this, "Profile updated!! Successfully", Toast.LENGTH_SHORT).show();
                }
            });
        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                progressBar.setIndeterminate(true);
                progressBar.setMessage("Updating Your Profile picture");
                progressBar.show();
                Uri resulturi = result.getUri();

                File thumb_filepathURi = new File(resulturi.getPath());
                try
                {
                    thumb_bitmap=new Compressor(this).setMaxHeight(200)
                            .setMaxWidth(200).setQuality(50).compressToBitmap(thumb_filepathURi);
                }catch (IOException r)
                {
                    r.printStackTrace();
                }
                ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
                final byte[] thumbByte=byteArrayOutputStream.toByteArray();

                final String FileUrl = FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg";

                final StorageReference filepath = storageReference.child(FileUrl);
                final StorageReference thumb_filepath=thumb_image_reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()+".jpg");

                filepath.putFile(resulturi).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {



                                dbReference.child("user_image").setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                 thumb_filepath.putBytes(thumbByte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                     @Override
                                     public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    thumb_filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                       dbReference.child("user_thumb_image").setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                           @Override
                                           public void onSuccess(Void aVoid) {

                                               ///uploaded Profile pic alongwith thumb pic
                                               SuccessProfileUpdated();
                                           }
                                       })     ;
                                        }
                                    });


                                     }
                                 });







                                    }
                                });
                            }
                        });
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();


            }
        }
    }

    private void SuccessProfileUpdated() { progressBar.dismiss();
        Toast.makeText(SettingActivity.this, "Profile Picture updated ", Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.menu_Logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(SettingActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        return true;
    }

}
