package chatapp.beast.firebasechat;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private Toolbar chatToolbar;
    private CircleImageView chat_to_pic;
    private TextView chat_to_user_name;
    private TextView chat_to_user_lastsen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toast.makeText(this, getIntent().getStringExtra(CONSTANTS.CHAT_TO_USER_NAME), Toast.LENGTH_LONG).show();
        chatToolbar = findViewById(R.id.chat_bar);

        setSupportActionBar(chatToolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionbarview = layoutInflater.inflate(R.layout.chatbar_layout, null);
        actionbar.setCustomView(actionbarview);
        chat_to_pic = actionbar.getCustomView().findViewById(R.id.chat_activity_profile_pic);
        chat_to_user_name = actionbar.getCustomView().findViewById(R.id.chat_activity_profile_name);
        chat_to_user_name.setText(getIntent().getStringExtra(CONSTANTS.CHAT_TO_USER_NAME));
        chat_to_pic = actionbar.getCustomView().findViewById(R.id.chat_activity_profile_pic);
        Picasso.get().load(getIntent().getStringExtra(CONSTANTS.CHAT_TO_USER_DP)).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_profile_pic).into(chat_to_pic, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(getIntent().getStringExtra(CONSTANTS.CHAT_TO_USER_DP)).placeholder(R.drawable.default_profile_pic).into(chat_to_pic);
            }
        });
    }
}
