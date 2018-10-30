package chatapp.beast.firebasechat;

import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import static android.content.Context.VIBRATOR_SERVICE;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{
    private List<Messages> userMessageList;

    public MessageAdapter(List<Messages> userMessageList)
    {
        this.userMessageList=userMessageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
     View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_messages_floating_buubles,viewGroup,false);
  return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i) {
Messages messages=userMessageList.get(i);

if (messages.getFromid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

    messageViewHolder.msgrootLayout.setGravity(Gravity.RIGHT);
}
else messageViewHolder.msgrootLayout.setGravity(Gravity.LEFT);

messageViewHolder.messageText.setText(messages.getMessage());
    }

    @Override
    public int getItemCount() {
        return userMessageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
public TextView messageText;
public LinearLayout msgrootLayout;
        public MessageViewHolder( View itemView) {
            super(itemView);
            messageText=itemView.findViewById(R.id.message_text);
msgrootLayout=itemView.findViewById(R.id.message_buuble_root);
        }
    }





}
