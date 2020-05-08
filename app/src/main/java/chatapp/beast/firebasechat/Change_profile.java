package chatapp.beast.firebasechat;

import android.app.Activity;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Change_profile extends AppCompatActivity {
private EditText user_name,user_sts;
private Button btn_save_change;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);
        user_name=findViewById(R.id.new_user_name);
        user_sts=findViewById(R.id.new_user_status);
        btn_save_change=findViewById(R.id.btn_save_profile_change);
        String old_name=getIntent().getStringExtra(CONSTANTS.OLD_USER_NAME);
        String old_sts=getIntent().getStringExtra(CONSTANTS.OLD_USER_STATUS);
        user_name.setText(old_name);
        user_sts.setText(old_sts);

        btn_save_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String new_username=user_name.getText().toString();
                String new_usersts=user_sts.getText().toString();
                if (new_username.isEmpty())
                {user_name.setError("Enter username");
                user_name.requestFocus();
return;
                }
                if (new_usersts.isEmpty())
                {
                    user_sts.setError("Enter user Status");
                    user_sts.requestFocus();
                    return;
                }
                Intent resultIntent=new Intent();
                new_username=new_username.trim();
               new_username= new_username.replaceAll("\n"," ");
               new_username=new_username.replaceAll("[ ]+"," ");
                new_usersts=new_usersts.trim();
                resultIntent.putExtra(CONSTANTS.NEW_USER_NAME,new_username);
                resultIntent.putExtra(CONSTANTS.NEW_USER_STATUS,new_usersts);
                setResult(Activity.RESULT_OK,resultIntent);
                finish();

            }
        });

    }
}
