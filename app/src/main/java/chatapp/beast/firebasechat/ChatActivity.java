package chatapp.beast.firebasechat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toast.makeText(this,getIntent().getStringExtra("chat_to_user_id").toString(),Toast.LENGTH_LONG).show();
    }
}
