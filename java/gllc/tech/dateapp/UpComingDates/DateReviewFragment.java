package gllc.tech.dateapp.UpComingDates;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import gllc.tech.dateapp.Loading.MainActivity;
import gllc.tech.dateapp.Automation.MapsActivity;
import gllc.tech.dateapp.Messages.MessageFragment;
import gllc.tech.dateapp.Loading.MyApplication;
import gllc.tech.dateapp.PostDate.EventAdapter;
import gllc.tech.dateapp.R;

/**
 * Created by bhangoo on 12/5/2016.
 */

public class DateReviewFragment extends Fragment{

    ListView listView;
    EventAdapter adapter;
    TextView textView, requestsOrMatch;
    public static LinearLayout layout;
    CircleImageView matchImage;
    HorizontalScrollView horizontalScrollView;
    ChildEventListener childEventListener;
    DatabaseReference populateRequestsReference;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    ArrayList<String> allRequests = new ArrayList<>();

    //public static String requestSelectedToReview;
    public static ArrayList<String> profileUrl = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.date_review, container, false);

        textView = (TextView)view.findViewById(R.id.showDate);
        requestsOrMatch = (TextView)view.findViewById(R.id.requests);
        matchImage = (CircleImageView)view.findViewById(R.id.matchPhoto);
        layout = (LinearLayout) view.findViewById(R.id.requestsLinear);
        horizontalScrollView = (HorizontalScrollView)view.findViewById(R.id.requestsScroll);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        textView.setText(MyApplication.dateSelected.getDateOfDate());

        adapter = new EventAdapter(getContext(), MyApplication.dateSelected.getEvents());

        listView = (ListView) getActivity().findViewById(R.id.dateReviewListView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra("cameFrom", "DateReview");
                startActivity(intent);
            }
        });

        decideDate();
    }

    public void decideDate() {
        if (MyApplication.dateSelected.getTheDate().equals("NA")){populateRequests();}
        else {setupDate();}
    }

    public void setupDate() {
        requestsOrMatch.setText("Your Date!");

        if (!MyApplication.userHashMap.get(MyApplication.dateSelected.getTheDate()).getId().equals(MyApplication.currentUser.getId())) {
            MyApplication.otherPerson = MyApplication.userHashMap.get(MyApplication.dateSelected.getTheDate());
        } else {MyApplication.otherPerson = MyApplication.userHashMap.get(MyApplication.dateSelected.getPoster());}

        Picasso.with(getContext()).load(MyApplication.otherPerson.getProfilePic()).into(matchImage);
        horizontalScrollView.setVisibility(View.INVISIBLE);
        matchImage.setVisibility(View.VISIBLE);

        //((MainActivity)getActivity()).saveUser(MyApplication.otherPerson);
        matchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MyApplication.otherPerson = ((MainActivity)getActivity()).geteUser();
                Bundle bundle = new Bundle();
                bundle.putString("cameFrom", "DateReview");
                bundle.putString("otherPerson", MyApplication.otherPerson.getId());
                ((MainActivity)getActivity()).addFragments(MessageFragment.class, R.id.container, "MessageFragment", bundle);
            }
        });
    }

    public void populateRequests(){

        populateRequestsReference = firebaseDatabase.getReference("Requests/" + MyApplication.dateSelectedKey);

        populateRequestsReference.addChildEventListener(childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                allRequests.add(dataSnapshot.getKey());

                if (!dataSnapshot.getValue().equals("Rejected")) {

                    CircleImageView imageView = new CircleImageView(getContext());
                    profileUrl.add(dataSnapshot.getValue().toString());
                    Picasso.with(getContext()).load(dataSnapshot.getValue().toString()).into(imageView);

                    imageView.setTag(dataSnapshot.getKey());

                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            MyApplication.otherPerson = MyApplication.userHashMap.get(v.getTag().toString());

                            Bundle bundle = new Bundle();
                            bundle.putString("cameFrom", "DateReview");
                            bundle.putString("otherPerson", MyApplication.otherPerson.getId());

                            populateRequestsReference.removeEventListener(childEventListener);
                            profileUrl = new ArrayList<String>();
                            ((MainActivity)getActivity()).addFragments(ReviewProfileFragment.class, R.id.container, "ReviewProfileFragment", bundle);
                        }
                    });

                    layout.addView(imageView);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
       /*
                for (int i=0; i<profileUrl.size(); i++){

                    Log.i("--All", "FIIIIIIIIIIIIIIIIIINDMEEEE" + dataSnapshot.getValue().toString());

                    Log.i("--All", "FIIIIIIIIIIIIIIIIIINDMEEEE" + profileUrl.get(i));
                    if (profileUrl.get(i).equals(dataSnapshot.getValue().toString())){
                        profileUrl.remove(i);
                        layout.removeViewAt(i);
                    }
                    else {


                        ImageView imageView = new ImageView(getContext());
                        Picasso.with(getContext()).load(profileUrl.get(i)).into(imageView);
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

                        imageView.setTag(dataSnapshot.getKey());

                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestSelectedToReview = v.getTag().toString();
                                ((MainActivity)getActivity()).addFragments(ReviewProfileFragment.class, R.id.dateReview);
                            }
                        });

                        layout.addView(imageView);
                    }
                }
                */
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();

        if (MyApplication.dateSelected.getPoster().equals(MyApplication.currentUser.getId()) && !MyApplication.dateSelected.getTheDate().equals("NA")) {
            inflater.inflate(R.menu.date_review, menu);
        } else if (MyApplication.dateSelected.getTheDate().equals("NA") && MyApplication.currentUser.getId().equals(MyApplication.dateSelected.getPoster())) {
            inflater.inflate(R.menu.date_review_cancel_only, menu);
        } else {
            inflater.inflate(R.menu.date_review_as_date, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.unmatchReviewDate:

                databaseReference = firebaseDatabase.getReference("Dates/" + MyApplication.dateSelectedKey + "/" + "theDate");
                databaseReference.setValue("NA");

                databaseReference = firebaseDatabase.getReference("Requests/" + MyApplication.dateSelectedKey + "/" + MyApplication.otherPerson.getId());
                databaseReference.removeValue();

                databaseReference = firebaseDatabase.getReference("AgreedChats/"+MyApplication.currentUser.getId()+"/"+MyApplication.dateSelectedKey);
                databaseReference.removeValue();

                databaseReference = firebaseDatabase.getReference("AgreedChats/"+MyApplication.otherPerson.getId()+"/"+MyApplication.dateSelectedKey);
                databaseReference.removeValue();

                if (MyApplication.dateSelected.getPoster().equals(MyApplication.currentUser.getId())) {
                    requestsOrMatch.setText("Your Requests");
                    horizontalScrollView.setVisibility(View.VISIBLE);
                    matchImage.setVisibility(View.INVISIBLE);

                    MyApplication.dateSelected.setTheDate("NA");

                    for (int i=0; i<MyApplication.allDates.size(); i++){
                        if (MyApplication.allDates.get(i).getKey().equals(MyApplication.dateSelectedKey)){
                            MyApplication.allDates.set(i,MyApplication.dateSelected);
                            MyApplication.dateHashMap.put(MyApplication.dateSelectedKey, MyApplication.dateSelected);
                        }
                    }

                    for (int i=0; i<MyApplication.fullMatchesAsCreator.size(); i++){
                        if (MyApplication.fullMatchesAsCreator.get(i).getKey().equals(MyApplication.dateSelectedKey)){
                            MyApplication.fullMatchesAsCreator.remove(i);
                            MyApplication.fullMatchesAsCreatorHashMap.remove(MyApplication.dateSelectedKey);
                            MyApplication.pendingDates.add(MyApplication.dateSelected);
                            MyApplication.pendingDatesHashMap.put(MyApplication.dateSelectedKey, MyApplication.dateSelected);
                        }
                    }

                    for (int i=0; i<MyApplication.combinedDates.size(); i++){
                        if (MyApplication.combinedDates.get(i).getKey().equals(MyApplication.dateSelectedKey)){
                            MyApplication.combinedDates.set(i,MyApplication.dateSelected);
                            MyApplication.combinesDatesHashMap.put(MyApplication.dateSelectedKey, MyApplication.dateSelected);
                        }
                    }

                    MyApplication.otherPerson = null;
                    populateRequests();
                }

                else{((MainActivity)getActivity()).popBackStack();}


                break;
        }

        switch (item.getItemId()) {
            case R.id.cancelDate:

                String dateId = MyApplication.dateHashMap.get(MyApplication.dateSelectedKey).getTheDate();

                for (int i=0; i <allRequests.size(); i++) {
                    databaseReference = firebaseDatabase.getReference("RequestedDate/"+allRequests.get(i)+"/"+MyApplication.dateSelectedKey);
                    databaseReference.removeValue();
                }

                if (!dateId.equals("NA")) {

                    MyApplication.otherPerson = MyApplication.userHashMap.get(dateId);

                    databaseReference = firebaseDatabase.getReference("AgreedChats/" + MyApplication.currentUser.getId() + "/" + MyApplication.dateSelectedKey);
                    databaseReference.removeValue();

                    databaseReference = firebaseDatabase.getReference("AgreedChats/" + MyApplication.otherPerson.getId() + "/" + MyApplication.dateSelectedKey);
                    databaseReference.removeValue();
                } else {
                    for (int i=0; i <allRequests.size(); i++) {
                        databaseReference = firebaseDatabase.getReference("AgreedChats/"+allRequests.get(i)+"/"+MyApplication.dateSelectedKey);
                        databaseReference.removeValue();
                    }

                    databaseReference = firebaseDatabase.getReference("AgreedChats/" + MyApplication.currentUser.getId() + "/" + MyApplication.dateSelectedKey);
                    databaseReference.removeValue();
                }

                if (!dateId.equals("NA")) {

                    MyApplication.otherPerson = MyApplication.userHashMap.get(dateId);

                    databaseReference = firebaseDatabase.getReference("AgreedChats/" + MyApplication.currentUser.getId() + "/" + MyApplication.dateSelectedKey);
                    databaseReference.removeValue();

                    databaseReference = firebaseDatabase.getReference("AgreedChats/" + MyApplication.otherPerson.getId() + "/" + MyApplication.dateSelectedKey);
                    databaseReference.removeValue();
                } else {
                    for (int i=0; i <allRequests.size(); i++) {
                        databaseReference = firebaseDatabase.getReference("AgreedChats/"+allRequests.get(i)+"/"+MyApplication.dateSelectedKey);
                        databaseReference.removeValue();
                    }

                    databaseReference = firebaseDatabase.getReference("AgreedChats/" + MyApplication.currentUser.getId() + "/" + MyApplication.dateSelectedKey);
                    databaseReference.removeValue();
                }

                databaseReference = firebaseDatabase.getReference("Requests/"+MyApplication.dateSelectedKey);
                databaseReference.removeValue();

                databaseReference = firebaseDatabase.getReference("Messages/"+MyApplication.dateSelectedKey);
                databaseReference.removeValue();

                for (int i = 0; i < MyApplication.combinedDates.size(); i++){
                    if (MyApplication.combinedDates.get(i).getKey().equals(MyApplication.dateSelectedKey)){
                        MyApplication.combinedDates.remove(i);
                        MyApplication.combinesDatesHashMap.remove(MyApplication.dateSelectedKey);
                        YourDatesFragment.adapter.notifyDataSetChanged();
                        ((MainActivity)getActivity()).popBackStack();

                        databaseReference = firebaseDatabase.getReference("Dates/" + MyApplication.dateSelectedKey);
                        databaseReference.removeValue();
                    }
                }
                break;
        }
        return true;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (MyApplication.dateSelected != null) {
            if (MyApplication.dateSelected.getTheDate().equals("NA") && MyApplication.dateSelected.getPoster().equals(MyApplication.currentUser.getId())) {
                populateRequestsReference.removeEventListener(childEventListener);
            }
        }

        MyApplication.dateSelected = null;
        MyApplication.dateSelectedKey="";
        allRequests.clear();
        Log.i("--All", "Detach");

    }

}
