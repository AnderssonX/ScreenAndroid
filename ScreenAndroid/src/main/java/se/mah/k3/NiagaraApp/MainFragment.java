package se.mah.k3.NiagaraApp;

import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Display;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Random;

/**
 * Created by K3LARA on 28/03/2015.
 */
public class MainFragment extends Fragment implements View.OnClickListener, View.OnTouchListener, ValueEventListener, View.OnLongClickListener, View.OnDragListener {
    long lastTimeStamp = System.currentTimeMillis();
    long timeLastRound;
    public int startX;
    public int startY;
    public float OffsetX;
    public float OffsetY;
    int width;
    int height;
    int randomNo = 99;
    public String TAG="TagZ";
    int n;
    int wordListSize;
    private long roundTrip = 0;
    String randomWord;
    View rootView;
    Button wordBtn;
    TextView wordArea;
    private Firebase myFirebaseRef;
    RelativeLayout layout;
    AbsoluteLayout wordTray;
    // DD related Declaration
    Button dragImg;
    ImageView leftImage;

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


        // DD stuff
        //Attach a longclick listener to an imageview
        dragImg = (Button) rootView.findViewById(R.id.wordBtn);


        dragImg.setOnLongClickListener(this);
        leftImage = (ImageView) rootView.findViewById(R.id.dragImg2);
        //Attach a draglistener to the layout
        layout = (RelativeLayout) rootView.findViewById(R.id.layout);
        layout.setOnDragListener(this);

        //Add listeners for the touch events onTouch will be called when screen is touched.
        rootView.setOnTouchListener(this);

        //Add listeners to initiate a measure of roundtrip time onClick will be called.
//        View v = rootView.findViewById(R.id.iv_refresh);
//        v.setOnClickListener(this);



        wordBtn = (Button) rootView.findViewById(R.id.wordBtn);
        startX = (int) wordBtn.getX();
        startX = (int) wordBtn.getX();

        startX = Math.round(wordBtn.getX());
        startY = Math.round(wordBtn.getY());

        Log.i("StartX is :" + startX, " startY is: " + startY);
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

            // wordArea = (TextView) rootView.findViewById(R.id.wordDisplayArea);
            wordArea.setVisibility(View.VISIBLE);
            wordArea.setText(randomWord);
            wordArea.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left));
            myFirebaseRef = new Firebase("https://scorching-fire-1846.firebaseio.com/").child("Regular Words/word" + randomNo);

            myFirebaseRef.child("Active").setValue(true);
            Log.i("buttoncl", myFirebaseRef.child("Active").getRef().toString());
            getNewWord();


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
                if (dataSnapshot.child("Active").getValue().toString() == "true") {
                    fireBaseWords.child("Active").setValue(false);
                    Log.i("getWord", "Set Value");
                }
                wordBtn.setText(randomWord);
                dragImg.setText(randomWord);

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

    public void getNewWord() {

        //         myFirebaseRef = new Firebase("https://scorching-fire-1846.firebaseio.com/").child("Regular Words/word" + randomNo);

        // myFirebaseRef.child("Active").setValue(true);
        // Log.i("getNewWord_setValue", myFirebaseRef.child("Active").getRef().toString());

        wordTray = (AbsoluteLayout) rootView.findViewById(R.id.wordTrayLayout);
        wordTray.removeAllViews();
        Button newButton = new Button(rootView.getContext());
        dragImg = newButton;
        dragImg.setId(R.id.wordBtn);
        dragImg.setText("new button!");
        wordTray.addView(dragImg);
        // getNewWord();

        int x = wordTray.getWidth() / 2;
        int y = wordTray.getHeight() / 2;

        dragImg.setX(400);
        dragImg.setY(100);

        dragImg.setOnLongClickListener(this);
        addRandomWord();
        // getWord();
    }

    public void resetWord() {

        wordTray = (AbsoluteLayout) rootView.findViewById(R.id.wordTrayLayout);
        wordTray.removeAllViews();
        Button newButton = new Button(rootView.getContext());
        dragImg = newButton;
        dragImg.setId(R.id.wordBtn);
        dragImg.setText(" ");
        wordTray.addView(dragImg);
        //addRandomWord();
        int x = wordTray.getWidth() / 2;
        int y = wordTray.getHeight() / 2;

        dragImg.setX(400);
        dragImg.setY(100);
        dragImg.setText(randomWord);
        dragImg.setOnLongClickListener(this);

    }


    @Override
    public boolean onLongClick(View v) {
        // User long-clicks, let's start the drag operation!
        // Create clip data holding information about what we're dragging, currently just empty until we need it for something
        ClipData clipData = ClipData.newPlainText("", "");
        // DragShadowBuilder creates what we want to drag, currently just a copy of the image.
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(dragImg);
        // startDrag starts the drag and drop operation. System sends drag events to all visible views, passing 4 parameters.
        dragImg.startDrag(clipData, shadowBuilder, dragImg, 0);
        // From here we go to the onDrag method
        return false;
         }

    @Override
    public boolean onDrag(View v, DragEvent dragEvent) {
        View draggedImageView = (View) dragEvent.getLocalState();

        // Handles each of the expected events
        switch (dragEvent.getAction()) {

            case DragEvent.ACTION_DRAG_STARTED:
                String TAG="dragLog";
                Log.i("TAGz", "drag action started");

                // Determines if this View can accept the dragged data
                if (dragEvent.getClipDescription()
                        .hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    Log.i("TAGz", "Can accept this data");

                    // returns true to indicate that the View can accept the dragged data.
                    return true;

                } else {
                    Log.i("TAGz", "Can not accept this data");

                }

                // Returns false. During the current drag and drop operation, this View will
                // not receive events again until ACTION_DRAG_ENDED is sent.
                return false;

            case DragEvent.ACTION_DRAG_ENTERED:
                Log.i("TAGz", "drag action entered");
                OffsetX=dragEvent.getX();
                OffsetY=dragEvent.getY();
                draggedImageView.setVisibility(View.INVISIBLE);
//                the drag point has entered the bounding box
                return true;

            case DragEvent.ACTION_DRAG_LOCATION:
                Log.i("TAGz", "dragging location");
    if(dragEvent.getX()>leftImage.getX()&&(dragEvent.getX()<leftImage.getX()+leftImage.getWidth()&&(dragEvent.getY()>leftImage.getY()&&(dragEvent.getY()<leftImage.getY()+leftImage.getHeight())))){
Log.i("DraggedItem", "Dragged item is over right image!");
            }
                /*triggered after ACTION_DRAG_ENTERED
                stops after ACTION_DRAG_EXITED*/
                return true;

            case DragEvent.ACTION_DRAG_EXITED:
                Log.i("TAGz", "drag action exited");
//                the drag shadow has left the bounding box
                return true;

            case DragEvent.ACTION_DROP:
                  /* the listener receives this action type when
                  drag shadow released over the target view
            the action only sent here if ACTION_DRAG_STARTED returned true
            return true if successfully handled the drop else false*/
                switch (draggedImageView.getId()) {
                    case R.id.dragImg2:
                        Log.i("TAGz", "Soccer ball");
                        return false;
                    case R.id.wordBtn:
                        Log.i("TAGz", "Dragging word");
                        if (dragEvent.getX() > leftImage.getX() && (dragEvent.getX() < leftImage.getX() + leftImage.getWidth() && (dragEvent.getY() > leftImage.getY() && (dragEvent.getY() < leftImage.getY() + leftImage.getHeight())))) {

                            myFirebaseRef = new Firebase("https://scorching-fire-1846.firebaseio.com/").child("Regular Words/word" + randomNo);
                            myFirebaseRef.child("Active").setValue(true);
                            myFirebaseRef.child("Active").setValue(false);

                            // Try to send position
                            //float x = dragEvent.getX() / width;
                            //float y = dragEvent.getY() / height;//Compensate for menubar can probably be solved more beautiful test with getY to see the difference
                            float x = dragEvent.getX();
                            float y = dragEvent.getY();
                            myFirebaseRef.child("x").setValue(x);  //Set the x Value
                            myFirebaseRef.child("y").setValue(y);  //Set the y value
                            //

                            Log.i("Setting ", myFirebaseRef + " Active to: true");

                        ViewGroup draggedImageViewParentLayout
                                = (ViewGroup) draggedImageView.getParent();
                        draggedImageViewParentLayout.removeView(draggedImageView);
                            RelativeLayout bottomLinearLayout = (RelativeLayout) v;
                        bottomLinearLayout.addView(draggedImageView);
                        draggedImageView.setVisibility(View.VISIBLE);
                            int xin = (int) dragEvent.getX() + 5 - dragImg.getWidth() / 2;
                            int yIn = (int) dragEvent.getY() + 5 - dragImg.getHeight() / 2;
                            Log.i("X: " + xin, ", Y: " + yIn);
                            Log.i("StartX is :" + startX, " startY is: " + startY);
                            Log.i("X is :" + xin, " Y is: " + yIn);
                            Log.i("dragImg X is :" + dragImg.getX(), " dragImg y is: " + dragImg.getY());
                            // myFirebaseRef = new Firebase("https://scorching-fire-1846.firebaseio.com/").child("Regular Words/word" + randomNo);

                            // myFirebaseRef.child("Active").setValue(true);
                            // Log.i("wordDropped", myFirebaseRef.child("Active").getRef().toString());

                            TextView tw = new TextView(v.getContext());

                            tw.setVisibility(View.VISIBLE);
                            tw.setX(xin);
                            tw.setY(yIn);
                            tw.setTextSize(22);
                            tw.setTextColor(Color.WHITE);
                            tw.setPadding(9, 9, 9, 9);
                            tw.setBackgroundColor(Color.DKGRAY);
                            tw.setTypeface(null, Typeface.BOLD);
                            tw.setText(randomWord);
                            layout.addView(tw);
                            layout.removeView(dragImg);
                            getNewWord();

                            Log.i("draggedWord", "Dropped");
                        } else {
                            Log.i("draggedWord", " Can't be dropped here, resetting!");
                            layout.removeView(dragImg);
                            resetWord();
                            ;
                        }
                        return true;
                    case R.id.dragImage1:
                        Log.i("TAGz", "Rugby ball");
                        return false;
                    default:
                        Log.i("TAGz", "in default");
                        return false;
                }

            case DragEvent.ACTION_DRAG_ENDED:

                Log.i("TAGz", "drag action ended");
                Log.i("TAGz", "getResult: " + dragEvent.getResult());

//                if the drop was not successful, set the ball to visible
                if (!dragEvent.getResult()) {
                    Log.i("TAGz", "setting visible");
                    draggedImageView.setVisibility(View.VISIBLE);
                }

                return true;
            // An unknown action type was received.
            default:
                Log.i("TAGz", "Unknown action type received by OnDragListener.");
                break;
        }

        return false;
    }

    public void dropWord() {

    }


}

