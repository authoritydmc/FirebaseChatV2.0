package chatapp.beast.firebasechat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        chat_to_pic = actionbar.getCustomView().findViewById(R.id.chat_activity_profile_pic);
        chat_to_user_name = actionbar.getCustomView().findViewById(R.id.chat_activity_profile_name);
        chat_to_user_name.setText(getIntent().getStringExtra(CONSTANTS.CHAT_TO_USER_NAME));
        chat_to_pic = actionbar.getCustomView().findViewById(R.id.chat_activity_profile_pic);
        backbutton = actionbar.getCustomView().findViewById(R.id.chat_activity_back);
        rootref = FirebaseDatabase.getInstance().getReference();
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
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        });
        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });


    }

    private void SendMessage() {
        String inputmsg = msg_edittext.getText().toString();
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
            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(senderref + "/" + push_id, messageTextBody);
            messageBodyDetails.put(receiverref + "/" + push_id, messageTextBody);
            rootref.updateChildren(messageBodyDetails, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                    msg_edittext.setText(null);
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


        }
        return true;
    }
}
