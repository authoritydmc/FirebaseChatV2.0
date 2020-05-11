package chatapp.beast.firebasechat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static chatapp.beast.firebasechat.CONSTANTS.gallery_pick;


public class ChatActivity extends AppCompatActivity {
    private Toolbar chatToolbar;
    private CircleImageView chat_to_pic;
    private TextView chat_to_user_name;
    private static boolean issendtype = true;
    private TextView chat_to_user_lastsen;
    private ImageButton backbutton;
    private EditText msg_edittext;
    private ImageButton btn_send_msg, btn_send_pic;
    private DatabaseReference rootref;
    private String Sender_user_id;
    private String Receiver_user_id;
    private RecyclerView msgrecyclerView;
    private List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private ChatRecyclerAdapter messageAdapter;
    private Messages messages;
    private Context context;
    private MediaPlayer senderplayer, receiverplayer;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == gallery_pick && resultCode == RESULT_OK && data != null) {
            Uri Imageuri = data.getData();
            Bitmap img = null;
            try {
                img = MediaStore.Images.Media.getBitmap(getContentResolver(), Imageuri);
                Toast.makeText(ChatActivity.this, "img selected", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                Toast.makeText(ChatActivity.this, "E Get Image", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            assert Imageuri != null;
            File thumb_filepathURi = new File(Imageuri.getPath());
              /*  try
                {
                    img=new Compressor(this)
                            .setQuality(50).compressToBitmap(thumb_filepathURi);
                }catch (IOException r)
                {
                    r.printStackTrace();
                }*/
            Toast.makeText(ChatActivity.this, "Sending Picture", Toast.LENGTH_SHORT).show();


            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            assert img != null;
            img.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            final byte[] thumbByte = byteArrayOutputStream.toByteArray();

            final String FileUrl = FirebaseAuth.getInstance().getCurrentUser().getUid() + System.nanoTime() + ".jpg";

            final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("media").child(FileUrl);
            filepath.putBytes(thumbByte).addOnSuccessListener(taskSnapshot -> filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    SendMessage(uri.toString(), "image");
                }
            }));


        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CONSTANTS.DatabaseReferenceToCurrentUser.child("online").setValue("online");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatToolbar = findViewById(R.id.chat_bar);
        setSupportActionBar(chatToolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(false);
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionbarview = layoutInflater.inflate(R.layout.chatbar_layout, null);
        actionbar.setCustomView(actionbarview);
        Receiver_user_id = getIntent().getStringExtra(CONSTANTS.CHAT_TO_USER_ID);
        Sender_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        btn_send_msg = findViewById(R.id.chat_activity_send_message);
        btn_send_pic = findViewById(R.id.chat_activity_select_picuter);
        msg_edittext = findViewById(R.id.chat_activity_message_edit_box);
        chat_to_user_lastsen = findViewById(R.id.chat_activity_profile_lastseen);
        chat_to_user_name = actionbar.getCustomView().findViewById(R.id.chat_activity_profile_name);
        chat_to_pic = actionbar.getCustomView().findViewById(R.id.chat_activity_profile_pic);
        backbutton = actionbar.getCustomView().findViewById(R.id.chat_activity_back);
        senderplayer = MediaPlayer.create(ChatActivity.this, R.raw.anxious);
        receiverplayer = MediaPlayer.create(this, R.raw.maybeone);

        //   chat_to_user_name.setText(getIntent().getStringExtra(CONSTANTS.CHAT_TO_USER_NAME));
        rootref = FirebaseDatabase.getInstance().getReference();
        msgrecyclerView = findViewById(R.id.Recycler_chat_msg);
        linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        msgrecyclerView.setLayoutManager(linearLayoutManager);

        FetchMessage();

        rootref.child(CONSTANTS.DATABASE_USER_nodE).child(Receiver_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String receivername = dataSnapshot.child(CONSTANTS.DATABASE_USER_name).getValue().toString();
                chat_to_user_name.setText(receivername);
                setUserOnlineSts(dataSnapshot);

                Picasso.get().load(dataSnapshot.child("user_thumb_image").getValue().toString()).placeholder(R.drawable.default_profile_pic).into(chat_to_pic, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(getIntent().getStringExtra(CONSTANTS.CHAT_TO_USER_DP)).placeholder(R.drawable.default_profile_pic).into(chat_to_pic);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                startActivity(intent);
//                finish();
                onBackPressed();
            }
        });
        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("CurrentCHAT").child(Sender_user_id).child(Receiver_user_id).child("ischatting").setValue(Receiver_user_id);
                FirebaseDatabase.getInstance().getReference().child("CurrentCHAT").child(Receiver_user_id).child(Sender_user_id).child("ischatting").setValue(Sender_user_id);

                String inputmsg = msg_edittext.getText().toString();
                msg_edittext.setText(null);

                if (inputmsg.isEmpty())
                    msg_edittext.setError("input msg first");
                else {
                    SendMessage(inputmsg, "text");
                }


            }
        });
        btn_send_pic.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference().child("CurrentCHAT").child(Sender_user_id).child(Receiver_user_id).child("ischatting").setValue(Receiver_user_id);
            FirebaseDatabase.getInstance().getReference().child("CurrentCHAT").child(Receiver_user_id).child(Sender_user_id).child("ischatting").setValue(Sender_user_id);

            Intent galleryintent = new Intent();
            galleryintent.setAction(Intent.ACTION_GET_CONTENT);
            galleryintent.setType("image/*");
            startActivityForResult(galleryintent, gallery_pick);
        });

        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            rootref.child(CONSTANTS.DATABASE_USER_nodE).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue("online");
    }

    private void setUserOnlineSts(DataSnapshot dataSnapshot) {
        if (dataSnapshot.hasChild("online")) {
            String last_seen = (String) dataSnapshot.child("online").getValue().toString();

            if (last_seen != null && last_seen.equals("online"))
                chat_to_user_lastsen.setText("online");
            else {
                Long last_seen_long;
                try {
                    last_seen_long = Long.parseLong(last_seen);
                    String active_sts = LastSeen.getTimeAgo(last_seen_long).toString();
                    chat_to_user_lastsen.setText(active_sts);

                } catch (Exception e) {
                    chat_to_user_lastsen.setText("NA");
                    return;
                }


            }

        }
    }

    private void FetchMessage() {
        messagesList.clear();
        rootref.child("Messages").child(Sender_user_id).child(Receiver_user_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
             messages = dataSnapshot.getValue(Messages.class);
                messages.setMessageID(dataSnapshot.getKey());
                messagesList.add(messages);


                messageAdapter.notifyDataSetChanged();

                msgrecyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                if (!issendtype)
                {senderplayer.stop();
                    receiverplayer.start();

                }else
                    issendtype=false;

//


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(ChatActivity.this, "Message Deleted ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        setMessageAdapter(messagesList);

    }
private void setMessageAdapter(List<Messages> list)
{
    messageAdapter = new ChatRecyclerAdapter(list, ChatActivity.this);

    msgrecyclerView.setAdapter(messageAdapter);
    messageAdapter.notifyDataSetChanged();


    msgrecyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);

}

    private void SendMessage(String msg, String type) {


        String senderref = CONSTANTS.MESSAGE_NODE + "/" + Sender_user_id + "/" + Receiver_user_id;
        String receiverref = CONSTANTS.MESSAGE_NODE + "/" + Receiver_user_id + "/" + Sender_user_id;


        DatabaseReference user_msg_key = rootref.child(CONSTANTS.MESSAGE_NODE).child(Sender_user_id)
                .child(Receiver_user_id).push();
        String push_id = user_msg_key.getKey();
        Map messageTextBody = new HashMap();
        messageTextBody.put("message", msg);
        messageTextBody.put("seen", false);
        messageTextBody.put("time", ServerValue.TIMESTAMP);
        messageTextBody.put("type", type);
        messageTextBody.put("fromid", Sender_user_id);
        messageTextBody.put("toid", Receiver_user_id);


        Map messageBodyDetails = new HashMap();

        messageBodyDetails.put(senderref + "/" + push_id, messageTextBody);
        messageBodyDetails.put(receiverref + "/" + push_id, messageTextBody);


        rootref.updateChildren(messageBodyDetails, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                issendtype = true;
                receiverplayer.stop();
                senderplayer.start();
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_chatactivity, menu);
        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();

        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Toast.makeText(getApplicationContext(),"onpause chats",0).show();

        rootref.child(CONSTANTS.DATABASE_USER_nodE).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue(ServerValue.TIMESTAMP);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CONSTANTS.DatabaseReferenceToCurrentUser.child("online").setValue("online");
    }

    @Override
    protected void onStop() {
        super.onStop();

         CONSTANTS.DatabaseReferenceToCurrentUser.child("online").setValue(ServerValue.TIMESTAMP);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CONSTANTS.DatabaseReferenceToCurrentUser.child("online").setValue("online");

    }
}
