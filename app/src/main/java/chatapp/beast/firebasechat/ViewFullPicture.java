package chatapp.beast.firebasechat;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ViewFullPicture extends AppCompatActivity {
ImageView img;
Button btnclose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_full_picture);
        img=findViewById(R.id.chat_message_pic_full);
        btnclose=findViewById(R.id.view_full_pic_closebtn);
        btnclose.setOnClickListener(v->{
            finish();
        });
        String urlImg=getIntent().getStringExtra("imageurl");
//        Picasso.get().setLoggingEnabled(true);
//        Picasso.get().load(urlImg).placeholder(R.drawable.bg).into(img);
        Glide.with(this).load(urlImg).placeholder(R.drawable.bg).into(img);
    }
}
