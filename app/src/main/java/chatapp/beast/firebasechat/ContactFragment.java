package chatapp.beast.firebasechat;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ContactFragment extends Fragment {

    private RecyclerView contact_recyler;
    private DatabaseReference databaseReference;

    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        contact_recyler = view.findViewById(R.id.recycler_contacts);
        contact_recyler.setHasFixedSize(true);
        contact_recyler.setLayoutManager(new LinearLayoutManager(getContext()));

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = databaseReference.child("Users");
        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(query, Contacts.class)
                        .build();
        FirebaseRecyclerAdapter<Contacts, ContactsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>

                (options) {

            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, final int position, @NonNull final Contacts model) {
                String user_id = getRef(position).getKey();

                if (!user_id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()))
                { databaseReference.child(CONSTANTS.DATABASE_USER_nodE).child(user_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                           try {
                               String username = dataSnapshot.child(CONSTANTS.DATABASE_USER_name).getValue().toString();
                               holder.setUser_name(username);
                               holder.setUser_image(model.getUser_thumb_image());
                               holder.setUser_status(model.getUser_status());
                           }catch (Exception e)
                           {  holder.mView.findViewById(R.id.contacts_fragment_card_view).setVisibility(View.GONE);
                               return;
                           }
                            if (dataSnapshot.hasChild("online")) {
                                String online_sts = (String) dataSnapshot.child("online").getValue().toString();
                                holder.setuserOnLine(online_sts);
                            }
                            holder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String chat_to_user_id = getRef(position).getKey();
                                    String chat_to_user_name = model.getUser_name();
                                    Intent chat_intent = new Intent(getContext(), ChatActivity.class);
                                    chat_intent.putExtra("last_seen", dataSnapshot.child("online").getValue().toString());
                                    chat_intent.putExtra(CONSTANTS.CHAT_TO_USER_ID, chat_to_user_id);
                                    chat_intent.putExtra(CONSTANTS.CHAT_TO_USER_NAME, chat_to_user_name);
                                    chat_intent.putExtra(CONSTANTS.CHAT_TO_USER_DP, model.getUser_thumb_image());
                                    startActivity(chat_intent);


                                }
                            });

                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
            }
else
                {
                    holder.mView.findViewById(R.id.contacts_fragment_card_view).setVisibility(View.GONE);
                }
            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.contact_users_layout, viewGroup, false);

                return new ContactsViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        contact_recyler.setAdapter(firebaseRecyclerAdapter);
       // CONSTANTS.DatabaseReferenceToCurrentUser.child("online").setValue("onlineR");
    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }


        public void setUser_name(String user_name) {
            TextView name = mView.findViewById(R.id.contacts_fragment_user_name);
            name.setText(user_name);
        }


        public void setUser_status(String user_status) {
            TextView status = mView.findViewById(R.id.contacts_fragment_user_status);
            status.setText(user_status);
        }


        public void setUser_image(String user_thumb_image) {
            final CircleImageView imageView = mView.findViewById(R.id.contacts_thumb_profile_pic);
            if (!user_thumb_image.equals("default_image"))
                Picasso.get().load(user_thumb_image).into(imageView);
            else {
                Picasso.get().load(R.drawable.default_profile_pic).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_profile_pic).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(R.drawable.default_profile_pic).placeholder(R.drawable.default_profile_pic).into(imageView);
                    }
                });



            }
        }

        public void setuserOnLine(String online_sts) {
            ImageView imageView=mView.findViewById(R.id.online_iimageview);
            if (online_sts.equals("online"))
                imageView.setVisibility(View.VISIBLE);
            else
                imageView.setVisibility(View.INVISIBLE);
        }
    }



}
