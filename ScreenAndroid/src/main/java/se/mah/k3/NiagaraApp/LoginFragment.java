package se.mah.k3.NiagaraApp;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements ValueEventListener
{
    private Firebase myFirebaseRef;
    public static Word w = new Word(true,"test");
    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.fragment_login, container, false);
        View v = returnView.findViewById(R.id.btnLogon);
        v.setOnClickListener(new View.OnClickListener() {
            //Click on loginButton
            @Override
            public void onClick(View v) {
                //In firebase you read a value by adding a listener, then it will trigger once connected and on all changes.
                //There is no readvalue as one could expect only listeners.
                //Get the ScreenNbr child
                Firebase  fireBaseEntryForScreenNbr = Constants.myFirebaseRef.child("ScreenNbr");
                //Ok listen the changes will sho up in the method onDataChange
                fireBaseEntryForScreenNbr.addValueEventListener(LoginFragment.this);
            }
        });


        Log.i("börja!!!!!!!!!!!", w.getText());

        myFirebaseRef = new Firebase("https://scorching-fire-1846.firebaseio.com/");  // alrik
        myFirebaseRef.child("Word1").setValue("ehhehsjhdhdjfdssf");
        //  myFirebaseRef.removeValue(); //Cleans out everything
        // use method getText from the word class to set text to "word1" in the firebase db.

        //myFirebaseRef.addChildEventListener(new ChildEventListener() {
        //myFirebaseRef.child("word1").addChildEventListener(new ChildEventListener() {

        //myFirebaseRef.child("Word1").addListenerForSingleValueEvent(new ValueEventListener() {
            myFirebaseRef.child("Word1").addValueEventListener(new ValueEventListener() {
         //   @Override
            public void onChildRemoved(DataSnapshot arg0) {
            }

           // @Override
            public void onChildMoved(DataSnapshot arg0, String arg1) {
            }

            //A user changed some value so update

            public void onChildChanged(DataSnapshot arg0, String arg1) {
                Iterable<DataSnapshot> dsList = arg0.getChildren();
                Log.i("börja!!!", "funkacasdfasdfsadfadsffasdfasdfasdf");
                // int place=1;

                if (arg0.getKey().equals("text")) {
                    w.setText(String.valueOf(arg0.getValue()));
                }
                if (arg0.getKey().equals("active")) {
                    w.setActive(Boolean.valueOf(String.valueOf(arg0.getValue())));
                }

                Log.i("börja!!!", "word1:" + w.getText() + "    firebase" + String.valueOf(arg0.getValue()));

            }

            //We got a new user

            public void onChildAdded(DataSnapshot arg0, String arg1) {
                if (arg0.hasChildren()) {

                }
            }

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("börja!!!", "    firebase: " + dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(FirebaseError arg0) {

            }
        });


        return returnView;



    }


    @Override
    public void onDataChange(DataSnapshot snapshot) {
        if (snapshot.getValue()!=null) {
            long val = (long) snapshot.getValue();
            String screenNbrFromFirebase = String.valueOf(val);
            Log.i("LoginFragment", "Screen nbr entered: " + val + " Value from firebase: "+screenNbrFromFirebase);
            EditText screenNumber = (EditText) getActivity().findViewById(R.id.screenNumber);
            EditText name = (EditText) getActivity().findViewById(R.id.name);
            // Constants.userName = name.getText().toString();
            //Are we on the right screen
            if (screenNbrFromFirebase.equals(screenNumber.getText().toString())){
                Log.i("LoginFragment", "Logged in");
                FragmentManager fm;
                fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.container, new MainFragment());
                ft.commit();
            }else{
                Toast.makeText(getActivity(),"Not the correct Screen",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }
}
