package se.mah.k3.NiagaraApp;

import com.firebase.client.Firebase;
// test jagasdfsdfsfdsfds
/**
 * Created by K3LARA on 29/03/2015.
 */
public class Constants {
    public static String userName = "Joakim";
    public static int xPos = 101;
    public static int yPos = 100;
    //public static Firebase myFirebaseRef = new Firebase("https://klara.firebaseio.com/");
    //Since this is static it will be instansiated  at startup of the App
    //Use static for variables that you want to reach from anywhere in the app...
    //public static Firebase myFirebaseRef = new Firebase("https://blinding-inferno-6351.firebaseio.com");
    public static Firebase myFirebaseRef = new Firebase("https://scorching-fire-1846.firebaseio.com/");
    public static Firebase wordsRef = new Firebase("https://scorching-fire-1846.firebaseio.com/Regular Words/");
}
