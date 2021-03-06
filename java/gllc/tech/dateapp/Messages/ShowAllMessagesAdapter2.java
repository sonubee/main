package gllc.tech.dateapp.Messages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import gllc.tech.dateapp.Loading.MyApplication;
import gllc.tech.dateapp.Objects.AgreedChats;
import gllc.tech.dateapp.R;

/**
 * Created by bhangoo on 12/22/2016.
 */

public class ShowAllMessagesAdapter2 extends ArrayAdapter<AgreedChats>{

    Context context;
    public static ArrayList<AgreedChats> agreedChatsArrayList = new ArrayList<>();
    public static ChildEventListener childEventListener;
    public static DatabaseReference myRef;

    public ShowAllMessagesAdapter2(Context context, int resource, ArrayList<AgreedChats> agreedChats) {
        super(context, resource, agreedChats);

        this.context = context;
        agreedChatsArrayList = agreedChats;

        agreedChatsArrayList.clear();
        notifyDataSetChanged();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("AgreedChats/" + MyApplication.currentUser.getId());

        myRef.addChildEventListener(childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                AgreedChats chat = dataSnapshot.getValue(AgreedChats.class);
                agreedChatsArrayList.add(chat);
                notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.show_all_messages_adapter, parent, false);

        TextView name = (TextView)view.findViewById(R.id.nameAllMessages);
        TextView title = (TextView)view.findViewById(R.id.titleOfDate);
        CircleImageView image = (CircleImageView)view.findViewById(R.id.profilePicAllMessages);

        if (agreedChatsArrayList.get(position).getPoster().equals(MyApplication.currentUser.getId())) {
            name.setText(MyApplication.userHashMap.get(agreedChatsArrayList.get(position).getRequester()).getName());
            Picasso.with(context).load(MyApplication.userHashMap.get(agreedChatsArrayList.get(position).getRequester()).getProfilePic()).into(image);
        } else {
            name.setText(MyApplication.userHashMap.get(agreedChatsArrayList.get(position).getPoster()).getName());
            Picasso.with(context).load(MyApplication.userHashMap.get(agreedChatsArrayList.get(position).getPoster()).getProfilePic()).into(image);
        }

        if (MyApplication.dateHashMap.containsKey(agreedChatsArrayList.get(position).getDateKey())) {
            title.setText(MyApplication.dateHashMap.get(agreedChatsArrayList.get(position).getDateKey()).getDateTitle());
        }

        if (MyApplication.completedDatesHashMap.containsKey(agreedChatsArrayList.get(position).getDateKey())) {
            title.setText(MyApplication.completedDatesHashMap.get(agreedChatsArrayList.get(position).getDateKey()).getDateTitle());
        }

        /*
        //for (int i=0; i < MyApplication.agreedChats.size(); i++){
        if (agreedChatsArrayList.get(position).getPoster().equals(MyApplication.currentUser.getId())){
            for (int j=0; j < MyApplication.allUsers.size(); j++){
                if (MyApplication.allUsers.get(j).getId().equals(agreedChatsArrayList.get(position).getRequester())){
                    name.setText(MyApplication.allUsers.get(j).getName());
                    Picasso.with(context).load(MyApplication.allUsers.get(j).getProfilePic()).into(image);
                    for (int k=0; k<MyApplication.allDates.size(); k++){
                        if (MyApplication.allDates.get(k).getKey().equals(agreedChatsArrayList.get(position).getDateKey())){
                            title.setText(MyApplication.allDates.get(k).getDateTitle());
                        }
                    }
                }
            }
        }

        else {
            for (int j=0; j < MyApplication.allUsers.size(); j++){
                if (MyApplication.allUsers.get(j).getId().equals(agreedChatsArrayList.get(position).getPoster())){
                    name.setText(MyApplication.allUsers.get(j).getName());
                    Picasso.with(context).load(MyApplication.allUsers.get(j).getProfilePic()).into(image);

                    for (int k=0; k<MyApplication.allDates.size(); k++){
                        if (MyApplication.allDates.get(k).getKey().equals(agreedChatsArrayList.get(position).getDateKey())){
                            title.setText(MyApplication.allDates.get(k).getDateTitle());
                        }
                    }
                }
            }
        }
        //}
*/
        return view;
    }
}
