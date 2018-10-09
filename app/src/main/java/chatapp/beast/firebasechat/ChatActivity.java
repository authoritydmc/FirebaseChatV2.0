package chatapp.beast.firebasechat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toast.makeText(this, getIntent().getStringExtra(CONSTANTS.CHAT_TO_USER_ID), Toast.LENGTH_LONG).show();
    }
}
