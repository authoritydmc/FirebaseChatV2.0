package chatapp.beast.firebasechat;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ((TextView)findViewById(R.id.splash_txtView)).setText("Version:"+BuildConfig.VERSION_NAME);

        Intent intent=new Intent(SplashActivity.this,MainActivity.class);
              intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
              startActivity(intent);
              finish();


    }
}
