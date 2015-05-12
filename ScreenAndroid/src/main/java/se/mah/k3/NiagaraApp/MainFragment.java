package se.mah.k3.NiagaraApp;

import android.app.Fragment;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Random;

/**
 * Created by K3LARA on 28/03/2015.
 */
public class MainFragment extends Fragment implements View.OnClickListener, View.OnTouchListener, ValueEventListener {
    long lastTimeStamp = System.currentTimeMillis();
    long timeLastRound;
    int width;
    int height;
    int randomNo = 99;
    int n;
    int wordListSize;
    private long roundTrip = 0;
    String randomWord;
    View rootView;
    Button wordBtn;
    TextView wordArea;
    private Firebase myFirebaseRef;


    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        addRandomWord();


        //Add listeners for the touch events onTouch will be called when screen is touched.
        rootView.setOnTouchListener(this);

        //Add listeners to initiate a measure of roundtrip time onClick will be called.
//        View v = rootView.findViewById(R.id.iv_refresh);
//        v.setOnClickListener(this);


        wordBtn = (Button) rootView.findViewById(R.id.wordBtn);
        wordBtn.setOnClickListener(this);




        //Create listeners for response time back so know when the token returns
        String userName = Constants.userName;
        Firebase fireBaseEntryForMyID = Constants.myFirebaseRef.child(Constants.userName); //My part of the firebase

        Firebase fireBaseEntryForRoundBack = fireBaseEntryForMyID.child("RoundTripBack"); //My roundtrip (Check firebase)
        //Listen for changes on "RoundTripBack" entry onDataChange will be called when "RoundTripBack" is changed
        fireBaseEntryForRoundBack.addValueEventListener(this);
        return rootView;
    }

    //Start a new time measure of roundtrip time
    @Override
    public void onClick(View v) {
//        if (v.getId() == R.id.iv_refresh) {
//            roundTrip = roundTrip + 1; //Assuming that we are the only one using our ID
//            lastTimeStamp = System.currentTimeMillis();  //remember when we sent the token
//            Constants.myFirebaseRef.child(Constants.userName).child("RoundTripTo").setValue(roundTrip);
//
//        }


        if (v.getId() == R.id.wordBtn) {

            //wordArea.setVisibility(View.VISIBLE);

            wordArea = (TextView) rootView.findViewById(R.id.wordDisplayArea);
            wordArea.setText(randomWord);

            myFirebaseRef = new Firebase("https://scorching-fire-1846.firebaseio.com/").child("Regular Words/word" + randomNo);
            myFirebaseRef.child("Active").setValue("True");
            Log.i("Buttonclick", myFirebaseRef.child("Active").getRef().toString());
            getNewWord();

            Log.i("wordBtn", "TestButton");

        }
    }

    //called if we move on the screen send the coordinates to fireBase
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:  // If it is the motionEvent move.
                float xRel = event.getX() / width;
                float yRel = event.getRawY() / height;//Compensate for menubar can probably be solved more beautiful test with getY to see the difference
                Constants.myFirebaseRef.child(Constants.userName).child("xRel").setValue(xRel);  //Set the x Value
                Constants.myFirebaseRef.child(Constants.userName).child("yRel").setValue(yRel);  //Set the y value
        }
        return true; //Ok we consumed the event and no-one can use it it is ours!
    }

    //This is called when the roundtrip is completed so show the time
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
//        if (roundTrip > 0 && dataSnapshot != null) {
//            roundTrip = (long) dataSnapshot.getValue();
//            timeLastRound = System.currentTimeMillis() - lastTimeStamp;
//            TextView timeLastTV = (TextView) getActivity().findViewById(R.id.timelast);
//            timeLastTV.setText("" + timeLastRound);
//        }
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }

    private void addRandomWord() {

        Firebase fbNumWords = Constants.myFirebaseRef.child("Regular Words Size");

        // Adds a "SINGLE" event listener to fetch value from child on firebase.

        fbNumWords.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // adds value of firebase child to textview "randomWord"
                String size = dataSnapshot.getValue().toString();
                wordListSize = Integer.parseInt(size);
                Log.i("SIze", String.valueOf(wordListSize));
                n = wordListSize;
                makeRandom();
                getWord();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void getWord() {
        // Creating a ref to a random child in the Regular Words tree on firebase
        final Firebase fireBaseWords = Constants.myFirebaseRef.child("Regular Words/word" + randomNo);

        // Adds a "SINGLE" event listener to fetch value from child on firebase.

        fireBaseWords.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // adds value of firebase child to textview "randomWord"
//                TextView wordTW = (TextView) getActivity().findViewById(R.id.wordDisplayArea);
//                wordTW.setText(dataSnapshot.child("text").getValue().toString() + " " + n);

                randomWord = dataSnapshot.child("text").getValue().toString();
                wordBtn.setText(randomWord);
                //fireBaseWords.child("Active").setValue(true);


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    public void makeRandom()

    {

        Random rand = new Random();
        // First we grab a random number (0-6)
        //  int n = rand.nextInt((int)(wordListSize+1));
        int n = rand.nextInt(wordListSize);
        randomNo = n;
    }
    public void getNewWord(){
        addRandomWord();
        getWord();
    }
}

