package se.mah.k3.NiagaraApp;

import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Random;

/**
 * Created by Mattias Andersson
 */
public class MainFragment extends Fragment implements View.OnClickListener, View.OnTouchListener, ValueEventListener, View.OnLongClickListener, View.OnDragListener {
    long lastTimeStamp = System.currentTimeMillis();
    long timeLastRound;

    public float OffsetX;
    public float OffsetY;
    int width;
    int height;
    int randomNo = 99;
    int n;
    int wordListSize;
    String randomWord;
    View rootView;
    Button wordBtn;
    TextView wordArea;
    private Firebase myFirebaseRef;
    RelativeLayout layout;
    AbsoluteLayout wordTray;
    Button dragButton;
    ImageView dropArea;

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


        //Attach a longclick listener to an button for drag and drop function
        dragButton = (Button) rootView.findViewById(R.id.dragButtonBtn);
        dragButton.setOnLongClickListener(this);

        //Declare imageView to use as reference for valid drop positions in drag and drop event.
        dropArea = (ImageView) rootView.findViewById(R.id.dropArea);

        //Attach a draglistener to the layout
        layout = (RelativeLayout) rootView.findViewById(R.id.layout);
        layout.setOnDragListener(this);

        //Add listeners for the touch events onTouch will be called when screen is touched.
        rootView.setOnTouchListener(this);

        // wordBtn = (Button) rootView.findViewById(R.id.dragButtonBtn);
        // wordBtn.setOnClickListener(this);

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
//        }

// Currently unused onclick item
        if (v.getId() == R.id.dragButtonBtn) {

            //wordArea.setVisibility(View.VISIBLE);

            // wordArea = (TextView) rootView.findViewById(R.id.wordDisplayArea);
       /*      wordArea.setVisibility(View.VISIBLE);
            wordArea.setText(randomWord);
            wordArea.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left));
            myFirebaseRef = new Firebase("https://scorching-fire-1846.firebaseio.com/").child("Regular Words/word" + randomNo);

            myFirebaseRef.child("Active").setValue(true);
            Log.i("buttoncl", myFirebaseRef.child("Active").getRef().toString());
            getNewWord();
*/

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

        // Adds a "SINGLE" event listener to fetch size of word list on firebase

        fbNumWords.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // adds value of firebase child to textview "randomWord"
                String size = dataSnapshot.getValue().toString();
                wordListSize = Integer.parseInt(size);
                Log.i("addRandomWord", "wordListSize: " + String.valueOf(wordListSize));
                n = wordListSize;
                // Create random number based on wordlist size and get a word using that number
                makeRandom();
                Log.i("addRandomWord", "makeRandom: got no. " + randomNo);
                getWord();
                Log.i("addRandomWord", "getWord: word" + n);
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

                randomWord = dataSnapshot.child("text").getValue().toString();
                if (dataSnapshot.child("Active").getValue().toString() == "true") {
                    getNewWord();
                    //  fireBaseWords.child("Active").setValue(false);
                    Log.i("getWord", "Word already active, getting a new one.");
                }
                Log.i("getWord", "got word: " + randomWord);
                dragButton.setText(randomWord);
                Log.i("getWord", "sat word on dragButton!");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void makeRandom()

    {

        Random rand = new Random();
        int n = rand.nextInt(wordListSize);
        randomNo = n;
    }

    public void getNewWord() {

        /*
        This Method runs when a word is dropped in the drop area. It removes all current views in
        the wordTray layout. It then re-creates the dragButton and adds it as a view in the layout.
        Then it adds a new onLongClickListener on it, runs the addRandomWord() method to fetch a new
        random word from firebase and sets that new word onto the new button.
         */

        wordTray = (AbsoluteLayout) rootView.findViewById(R.id.wordTrayLayout);
        wordTray.removeAllViews();
        Button newButton = new Button(rootView.getContext());
        dragButton = newButton;
        dragButton.setId(R.id.dragButtonBtn);
        dragButton.setText("new button!");
        wordTray.addView(dragButton);

        int x = wordTray.getWidth() / 2;
        int y = wordTray.getHeight() / 2;

        dragButton.setX(400);
        dragButton.setY(100);

        dragButton.setOnLongClickListener(this);
        addRandomWord();
    }

    public void resetWord() {

        wordTray = (AbsoluteLayout) rootView.findViewById(R.id.wordTrayLayout);
        wordTray.removeAllViews();
        Button newButton = new Button(rootView.getContext());
        dragButton = newButton;
        dragButton.setId(R.id.dragButtonBtn);
        dragButton.setText(" ");
        wordTray.addView(dragButton);

        int x = wordTray.getWidth() / 2;
        int y = wordTray.getHeight() / 2;

        dragButton.setX(400);
        dragButton.setY(100);
        dragButton.setText(randomWord);
        dragButton.setOnLongClickListener(this);

    }


    @Override
    public boolean onLongClick(View v) {
        // User long-clicks, let's start the drag operation!
        // Create clip data holding information about what we're dragging, currently just empty
        // until/if we need it for something
        ClipData clipData = ClipData.newPlainText("", "");
        // DragShadowBuilder creates what we want to drag, currently just a copy of the image.
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(dragButton);
        // startDrag starts the drag and drop operation. System sends drag events to all visible
        // views, passing 4 parameters.
        dragButton.startDrag(clipData, shadowBuilder, dragButton, 0);
        // From here we go to the onDrag method
        return false;
    }

    @Override
    public boolean onDrag(View v, DragEvent dragEvent) {
        View draggedButton = (View) dragEvent.getLocalState();

        // Handles each of the expected events of drag and drop
        switch (dragEvent.getAction()) {

            case DragEvent.ACTION_DRAG_STARTED:

                Log.i("dragEvent", "drag action started");

                // Determines if this View can accept the dragged data
                if (dragEvent.getClipDescription()
                        .hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    Log.i("dragEvent", "Dragged data is accepted by this view");

                    // returns true to indicate that the View can accept the dragged data.
                    return true;

                } else {
                    Log.i("dragEvent", "View does not accepted the dragged data");

                }

                return false;

            case DragEvent.ACTION_DRAG_ENTERED:
                Log.i("dragEvent", "drag action entered");
                OffsetX = dragEvent.getX();
                OffsetY = dragEvent.getY();
                draggedButton.setVisibility(View.INVISIBLE);
                // the drag point has entered the bounding box
                return true;

            case DragEvent.ACTION_DRAG_LOCATION:
                Log.i("dragEvent", "X=" + dragEvent.getX() + " Y=" + dragEvent.getY());
                if (dragEvent.getX() > dropArea.getX() && (dragEvent.getX() < dropArea.getX() + dropArea.getWidth() && (dragEvent.getY() > dropArea.getY() && (dragEvent.getY() < dropArea.getY() + dropArea.getHeight())))) {
                    Log.i("dragEvent", "Dragged data is inside drop area!");
                }
                /*triggered after ACTION_DRAG_ENTERED
                stops after ACTION_DRAG_EXITED*/
                return true;

            case DragEvent.ACTION_DRAG_EXITED:
                Log.i("dragEvent", "drag action exited");
//                the drag shadow has left the bounding box
                return true;

            case DragEvent.ACTION_DROP:
                  /* the listener receives this action type when
                  drag shadow released over the target view
            the action only sent here if ACTION_DRAG_STARTED returned true
            return true if successfully handled the drop else false*/


                switch (draggedButton.getId()) {

                    /* Set behavior depending on what item is being dragged using switch case.
                        Currently only the "draggButtonBtn" id has a set behaviour
                     */

                    case R.id.dragButtonBtn:
                        Log.i("dragEvent", "dragging dragButtonBtn ");
                        /* Check if item is dropped inside these coordinates, then do stuff.
                         If dropped elsewhere, reset button to wordtray again using if/else.
                          */
                        if (dragEvent.getX() > dropArea.getX() && (dragEvent.getX() < dropArea.getX() + dropArea.getWidth() && (dragEvent.getY() > dropArea.getY() && (dragEvent.getY() < dropArea.getY() + dropArea.getHeight())))) {

                            myFirebaseRef = new Firebase("https://scorching-fire-1846.firebaseio.com/").child("Regular Words/word" + randomNo);
                            myFirebaseRef.child("Active").setValue(true);
                            //myFirebaseRef.child("Active").setValue(false);

                            /* Send drop position to firebase by setting value x and y
                            so eclipse can calculate where on the public screen to display our
                            dropped word */

                            float xRel = dragEvent.getX() / width;
                            float yRel = dragEvent.getY() / height;
                            myFirebaseRef.child("x").setValue(xRel);
                            myFirebaseRef.child("y").setValue(yRel);

                            // This handles which layout we're dropping dragged item onto.
                            ViewGroup draggedImageViewParentLayout
                                    = (ViewGroup) draggedButton.getParent();
                            draggedImageViewParentLayout.removeView(draggedButton);
                            RelativeLayout mainRelativeLayout = (RelativeLayout) v;
                            mainRelativeLayout.addView(draggedButton);
                            draggedButton.setVisibility(View.VISIBLE);

                            /* Set xIn and yIn using dragEvent coordinates and size of button.
                           Then use these numbers when setting text from button on screen after
                           drop.
                             */

                            int xIn = (int) dragEvent.getX() + 5 - dragButton.getWidth() / 2;
                            int yIn = (int) dragEvent.getY() + 5 - dragButton.getHeight() / 2;

                            /*
                            When button is dropped, create a new textview, set text to value of
                            the randomword, set cordinates to drop position, set styles etc.
                            add the view to the layout and remove the dragged item and run
                            method getNewWord() to create a new button with a new random word.
                            Et voila! :)
                             */
                            TextView tw = new TextView(v.getContext());

                            tw.setVisibility(View.VISIBLE);
                            tw.setX(xIn);
                            tw.setY(yIn);
                            tw.setTextSize(22);
                            tw.setTextColor(Color.WHITE);
                            tw.setPadding(9, 9, 9, 9);
                            tw.setBackgroundColor(Color.DKGRAY);
                            tw.setTypeface(null, Typeface.BOLD);
                            tw.setText(randomWord);
                            layout.addView(tw);
                            layout.removeView(dragButton);
                            getNewWord();

                            Log.i("dragEvent", "item dropped at X: " + xIn + "Y: " + yIn);
                        } else {
                            Log.i("dragEvent", "Can't be dropped here, resetting!");
                            layout.removeView(dragButton);
                            resetWord();
                            ;
                        }
                        return true;

                    /*
                    Behavior for a second item (currently not used in the app).
                     */
                    case R.id.dragImage1:
                        Log.i("dragEvent", "Dragging dragImage1");
                        return false;
                    default:
                        Log.i("dragEvent", "in default");
                        return false;
                }

            case DragEvent.ACTION_DRAG_ENDED:

                Log.i("dragEvent", "drag action ended");
                Log.i("dragEvent", "getResult: " + dragEvent.getResult());

                // if the drop was not successful, do this.
                if (!dragEvent.getResult()) {
                    Log.i("dragEvent", "setting visible");
                    draggedButton.setVisibility(View.VISIBLE);
                }

                return true;
            // An unknown action type was received.
            default:
                Log.i("dragEvent", "Unknown action type received by OnDragListener.");
                break;
        }

        return false;
    }

}

