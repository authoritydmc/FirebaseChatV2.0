package chatapp.beast.firebasechat;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.manolovn.trianglify.TrianglifyView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private Toolbar chatToolbar;
    private CircleImageView chat_to_pic;
    private TextView chat_to_user_name;
    private TextView chat_to_user_lastsen;
    private ImageButton backbutton;
    private EditText msg_edittext;
    private ImageButton btn_send_msg, btn_send_pic;
    private DatabaseReference rootref;
    private String Sender_user_id;
    private String Receiver_user_id;
    private RecyclerView msgrecyclerView;
    private   List<Messages> messagesList=new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private  MessageAdapter messageAdapter;
    private Messages messages;

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
        chat_to_user_lastsen=findViewById(R.id.chat_activity_profile_lastseen);
        chat_to_pic = actionbar.getCustomView().findViewById(R.id.chat_activity_profile_pic);
        chat_to_user_name = actionbar.getCustomView().findViewById(R.id.chat_activity_profile_name);
        chat_to_user_name.setText(getIntent().getStringExtra(CONSTANTS.CHAT_TO_USER_NAME));
        chat_to_pic = actionbar.getCustomView().findViewById(R.id.chat_activity_profile_pic);
        backbutton = actionbar.getCustomView().findViewById(R.id.chat_activity_back);
        rootref = FirebaseDatabase.getInstance().getReference();
          messageAdapter=new MessageAdapter(messagesList);
        msgrecyclerView=findViewById(R.id.Recycler_chat_msg);
linearLayoutManager=new LinearLayoutManager(this);
msgrecyclerView.setHasFixedSize(true);
msgrecyclerView.setLayoutManager(linearLayoutManager);
msgrecyclerView.setAdapter(messageAdapter);

FetchMessage();

        String last_seen=getIntent().getStringExtra("last_seen");
        if (last_seen!=null && last_seen.equals("online"))
            chat_to_user_lastsen.setText("online");
        else
        {

            Long last_seen_long=Long.parseLong(last_seen);
            String active_sts=LastSeen.getTimeAgo(last_seen_long,getApplicationContext()).toString();

            chat_to_user_lastsen.setText(active_sts);

        }

        Picasso.get().load(getIntent().getStringExtra(CONSTANTS.CHAT_TO_USER_DP)).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_profile_pic).into(chat_to_pic, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(getIntent().getStringExtra(CONSTANTS.CHAT_TO_USER_DP)).placeholder(R.drawable.default_profile_pic).into(chat_to_pic);
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
                SendMessage();
        AudioManager        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

                audioManager.playSoundEffect(SoundEffectConstants.NAVIGATION_LEFT);

            }
        });

        if (FirebaseAuth.getInstance().getCurrentUser()!=null)
            rootref.child(CONSTANTS.DATABASE_USER_nodE).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue("online");
    }

    private void FetchMessage() {
        rootref.child("Messages").child(Sender_user_id).child(Receiver_user_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                 messages=dataSnapshot.getValue(Messages.class);
                messagesList.add(messages);
               Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                }else {
                    vibrator.vibrate(200);
                }
                messageAdapter.notifyDataSetChanged();

            msgrecyclerView.scrollToPosition(messageAdapter.getItemCount()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendMessage() {
        String inputmsg = msg_edittext.getText().toString();
        msg_edittext.setText(null);

        if (inputmsg.isEmpty())
            msg_edittext.setError("input msg first");


        else {
            String senderref = CONSTANTS.MESSAGE_NODE + "/" + Sender_user_id + "/" + Receiver_user_id;
            String receiverref = CONSTANTS.MESSAGE_NODE + "/" + Receiver_user_id + "/" + Sender_user_id;



            DatabaseReference user_msg_key = rootref.child(CONSTANTS.MESSAGE_NODE).child(Sender_user_id)
                    .child(Receiver_user_id).push();
            String push_id = user_msg_key.getKey();
            Map messageTextBody = new HashMap();
            messageTextBody.put("message", inputmsg);
            messageTextBody.put("seen", false);
            messageTextBody.put("time", ServerValue.TIMESTAMP);
            messageTextBody.put("type", "text");
            messageTextBody.put("fromid",Sender_user_id);

            Map messageBodyDetails = new HashMap();

            messageBodyDetails.put(senderref + "/" + push_id,messageTextBody);
            messageBodyDetails.put(receiverref + "/" + push_id, messageTextBody);



            rootref.updateChildren(messageBodyDetails, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                }
            });


        }
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

       // CONSTANTS.DatabaseReferenceToCurrentUser.child("online").setValue(ServerValue.TIMESTAMP);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CONSTANTS.DatabaseReferenceToCurrentUser.child("online").setValue("online");

    }
}
