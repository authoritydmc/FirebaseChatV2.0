package chatapp.beast.firebasechat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private Toolbar chatToolbar;
    private CircleImageView chat_to_pic;
    private TextView chat_to_user_name;
    private TextView chat_to_user_lastsen;
    private ImageButton backbutton;

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
        chat_to_pic = actionbar.getCustomView().findViewById(R.id.chat_activity_profile_pic);
        chat_to_user_name = actionbar.getCustomView().findViewById(R.id.chat_activity_profile_name);
        chat_to_user_name.setText(getIntent().getStringExtra(CONSTANTS.CHAT_TO_USER_NAME));
        chat_to_pic = actionbar.getCustomView().findViewById(R.id.chat_activity_profile_pic);
        backbutton = actionbar.getCustomView().findViewById(R.id.chat_activity_back);
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
