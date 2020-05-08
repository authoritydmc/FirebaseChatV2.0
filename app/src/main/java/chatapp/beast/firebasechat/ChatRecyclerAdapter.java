package chatapp.beast.firebasechat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Messages> Chats;

    private Context mContext;
    private static final int TYPE_IMAGE=1;
    private static FirebaseUser user;
    private static final int TYPE_TEXT=2;

    @Override
    public int getItemViewType(int position) {
        return Chats.get(position).getType().equals("image")?TYPE_IMAGE:TYPE_TEXT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater=LayoutInflater.from(viewGroup.getContext());
        if (i==TYPE_IMAGE)
        {
            Log.d("RAAJCREa", "onCreateViewHolder: "+"loaded Picture fpr "+i);
           View view=inflater.inflate(R.layout.chat_messages_picture,viewGroup,false);
           return new PictureViewHolder(view);
        }
        else
        {
            View view=inflater.inflate(R.layout.chat_messages_floating_buubles,viewGroup,false);
return  new TextViewHolder(view);
        }



    }

    public ChatRecyclerAdapter(List<Messages> chats,Context mContext) {
     this.mContext=mContext;
        this.Chats=chats;
        user=FirebaseAuth.getInstance().getCurrentUser();

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Messages obj=Chats.get(i);
        Log.d("RAAJ", "onBindViewHolder: "+obj.getType()+viewHolder.getItemViewType());
        if (obj.getType().equals("image"))
        {try{
            if (obj.getFromid().equals(user.getUid())) {

                ( (PictureViewHolder)viewHolder).picroot.setGravity(Gravity.RIGHT);
            }
            else
            {                ( (PictureViewHolder)viewHolder).picroot.setGravity(Gravity.LEFT);

            }
            Picasso.get().load(obj.getMessage()).placeholder(R.drawable.logo).into(((PictureViewHolder)viewHolder).imgView);
            ( (PictureViewHolder)viewHolder).picroot.setOnClickListener(v->{
                Intent intent=new Intent(mContext,ViewFullPicture.class);
                intent.putExtra("imageurl",obj.getMessage());
                mContext.startActivity(intent);
            });
            ( (PictureViewHolder)viewHolder).datetime.setText(LastSeen.getTimeStamp(obj.getTime()));








            ( (PictureViewHolder)viewHolder).picroot.setOnLongClickListener(v -> {

                new AlertDialog.Builder(mContext)
                        .setTitle("Delete This message")
                        .setMessage("Are you sure you want to delete this entry?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference root= FirebaseDatabase.getInstance().getReference().child(CONSTANTS.MESSAGE_NODE);
                                root.child(obj.getFromid()).child(obj.getToid()).child(obj.getMessageID()).removeValue();
                                root.child(obj.getToid()).child(obj.getFromid()).child(obj.getMessageID()).removeValue();



                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return true;
            });



        }catch (Exception e)
        {
              Log.d(TAG, "onBindViewHolder: "+e.getMessage());
        }


        }    else
        {
 //text
            if (obj.getFromid().equals(user.getUid())) {

                ( (ChatRecyclerAdapter.TextViewHolder)viewHolder).msgrootLayout.setGravity(Gravity.RIGHT);
            }
            else ( (ChatRecyclerAdapter.TextViewHolder)viewHolder).msgrootLayout.setGravity(Gravity.LEFT);

            ( (ChatRecyclerAdapter.TextViewHolder)viewHolder).messageText.setText(obj.getMessage());
            ( (TextViewHolder)viewHolder).datetime.setText(LastSeen.getTimeStamp(obj.getTime()));

            ( (TextViewHolder)viewHolder).msgrootLayout.setOnLongClickListener(v -> {

                new AlertDialog.Builder(mContext)
                        .setTitle("Delete This message")
                        .setMessage("Are you sure you want to delete this entry?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                               DatabaseReference root= FirebaseDatabase.getInstance().getReference().child(CONSTANTS.MESSAGE_NODE);
                               root.child(obj.getFromid()).child(obj.getToid()).child(obj.getMessageID()).removeValue();
                               root.child(obj.getToid()).child(obj.getFromid()).child(obj.getMessageID()).removeValue();



                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

return true;
            });



        }



    }

    @Override
    public int getItemCount() {
        return Chats.size();

    }
    private static class TextViewHolder extends  RecyclerView.ViewHolder{
        private TextView messageText;
        private LinearLayout msgrootLayout;
        private TextView datetime;

        public TextViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText=itemView.findViewById(R.id.message_text);
            msgrootLayout=itemView.findViewById(R.id.message_buuble_root);
            datetime=itemView.findViewById(R.id.chat_message_dateTime);

        }
    }
    private static  class PictureViewHolder extends  RecyclerView.ViewHolder{
        private ImageView imgView;
        private LinearLayout picroot;
        private TextView datetime;
        public PictureViewHolder(@NonNull View itemView) {
            super(itemView);
            imgView=itemView.findViewById(R.id.chat_message_pic);

            picroot=itemView.findViewById(R.id.chat_message_pic_root);
            datetime=itemView.findViewById(R.id.chat_pic_dateTime);
        }
    }
}
