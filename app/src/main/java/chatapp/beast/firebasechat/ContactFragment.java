package chatapp.beast.firebasechat;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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


        View view= inflater.inflate(R.layout.fragment_contact, container, false);

        contact_recyler=(RecyclerView)view.findViewById(R.id.recycler_contacts);
        contact_recyler.setHasFixedSize(true);
        contact_recyler.setLayoutManager(new LinearLayoutManager(getContext()));

databaseReference=FirebaseDatabase.getInstance().getReference();

return  view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query=databaseReference.child("Users");
        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(query, Contacts.class)
                        .build();
        FirebaseRecyclerAdapter<Contacts,ContactsViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>

                (   options) {
            @Override
            protected void onBindViewHolder(@NonNull ContactsViewHolder holder, int position, @NonNull Contacts model) {
holder.setUser_name(model.getUser_name());
holder.setUser_image(model.getUser_thumb_image());
holder.setUser_status(model.getUser_status());
                Log.d("RECYCLERVIEW","ONBINDVIEW CALLED full");
            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.contact_users_layout,viewGroup, false);

                return new ContactsViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        contact_recyler.setAdapter(firebaseRecyclerAdapter);

    }

    public  static  class ContactsViewHolder extends RecyclerView.ViewHolder
    {View mView;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);
        mView=itemView;
        }



        public void setUser_name(String user_name)
        {
            TextView name=mView.findViewById(R.id.contacts_fragment_user_name);
            name.setText(user_name);
        }


        public void setUser_status(String user_status)
        {
TextView status=mView.findViewById(R.id.contacts_fragment_user_status);
        status.setText(user_status);
        }


        public void setUser_image(String user_thumb_image)
        {CircleImageView imageView=mView.findViewById(R.id.contacts_thumb_profile_pic);
           if (!user_thumb_image.equals("default_image"))
            Picasso.get().load(user_thumb_image).into(imageView);
            else
           {Picasso.get().load(R.drawable.default_profile_pic).into(imageView);

           }
        }
    }
}
