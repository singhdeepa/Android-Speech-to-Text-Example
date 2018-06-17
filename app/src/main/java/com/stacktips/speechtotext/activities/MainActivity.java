package com.stacktips.speechtotext.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stacktips.speechtotext.R;
import com.stacktips.speechtotext.dataSet.ChannelModel;
import com.stacktips.speechtotext.dataSet.DataBytes;
import com.stacktips.speechtotext.dataSet.PushToFirebase;

import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;

import static com.stacktips.speechtotext.activities.ConnectActivity.isFromnotification;
import static com.stacktips.speechtotext.dataSet.Constants.MyPREFERENCES;

public class MainActivity extends AppCompatActivity implements  TextToSpeech.OnInitListener {

    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView mVoiceInputTv;
    private ImageButton mSpeakBtn;
    ChannelModel channelModel;
    ArrayList<ChannelModel> channelList;

     DataBytes dataBytes;
    //TTS object
    private TextToSpeech myTTS;
    //status check code
    private static final int MY_DATA_CHECK_CODE = 101;
    boolean isInterestSet=false,fromNotification=false;

    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String restoredText = sharedpreferences.getString("fromNotify", "no");

        Log.e("restoredText","==="+restoredText);

        initViews();
        initListners();
        pushChannelsToFirebase();   //For pushing channels to Firebase


        //has Internet? fetch from Firebase
        //getChannelsFromFirebase();
        onReceiveText();
        //has no internet? fetch from local
        getChannels();

//        processVoiceInput("Switch to channel number 14");
//         speakWords("Switch to channel number 14");

        if (isFromnotification)
        {
//            myTTS = new TextToSpeech(this, this);
            isFromnotification=false;
            Log.e("restoredText","===yes");
            fromNotification=true;
            DataBytes.sendTxtMessage((byte) 0x72);
//            speakWords("Do you want to watch FIFA");
        }
    }

    private void getChannels() {
        ChannelModel c1 = new ChannelModel("setmax","1","tzp",
                "amir khan","xyz");
        ChannelModel c2 = new ChannelModel("sony","2","CID",
                "salman khan","ccc");


        channelList.add(c1);
        channelList.add(c2);

    }

    private void pushChannelsToFirebase() {
        PushToFirebase.pushToFB();

    }

    private void getChannelsFromFirebase() {
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("channels");

        Log.e("db ref","===="+mDatabase);
        channelList.clear();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    // TODO: handle the post
                    channelModel = postSnapshot.getValue(ChannelModel.class);
                    Log.e("list", "==" + channelModel.getChannelName());
                    channelList.add(channelModel);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("db error", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDatabase.addValueEventListener(postListener);
    }

    private void initListners() {
        mSpeakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceInput();
//                String words = "Switched to your favorite channel";
//                speakWords(words);
            }
        });

    }

    private void onReceiveText() {
        //check for TTS Text to speech data
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
    }

    private void initViews() {
        mVoiceInputTv = (TextView) findViewById(R.id.voiceInput);
        mSpeakBtn = (ImageButton) findViewById(R.id.btnSpeak);
        channelList = new ArrayList<>();

    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT:
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mVoiceInputTv.setText(result.get(0));
                    Log.e("result","======="+result.toString());
                    splitInoutString(result.get(0).toLowerCase());
//                    processVoiceInput(result.get(0).toLowerCase());
                }
                break;
           //act on result of TTS data check
            case MY_DATA_CHECK_CODE: {
                    if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                        //the user has the necessary data - create the TTS
                        myTTS = new TextToSpeech(this, this);
                    } else {
                        //no data - install it now
                        Intent installTTSIntent = new Intent();
                        installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                        startActivity(installTTSIntent);
                    }
                }
                break;
            }

        }

    private void splitInoutString(String s) {
        String[] parts = s.split(" ");
        for (int i=0;i<parts.length;i++)
        {
            parts[i]=parts[i].toLowerCase();
        }

        if (parts[0].contains("hey") && parts[1].contains("man"))
        {
           processArray(parts);
        }
        else if (parts[0].contains("kick") || parts[0].contains("wanted"))
        {
            if (isInterestSet)
            {
                isInterestSet=false;
                if (parts[0].contains("kick"))
                {
                    DataBytes.sendTxtMessages((byte) 0x6E, (byte) 0x70);
                    speakWords("Playing kick");
                }
               else {
                    DataBytes.sendTxtMessages((byte) 0x6E, (byte) 0x71);
                    speakWords("Playing wanted");
                }
            }
            else
            {
                notAvalidCmd();
            }

        }
        else if (parts[0].contains("yes") || parts[0].contains("no") )
        {
            if (fromNotification)
            {
                if (parts[0].contains("yes"))
                {
                    DataBytes.sendTxtMessage((byte) 0x72);
                    speakWords("Playing fifa");
                }
            }else
            {
                notAvalidCmd();
            }

        }
        else
        {
            notAvalidCmd();
        }

    }

    private void notAvalidCmd() {
        DataBytes.sendTxtMessage((byte) 0x6D);
        speakWords("Please say something which i can understand");
    }

    private void processArray(String[] parts) {


        if (parts[2].contains("mute"))
        {
            DataBytes.sendTxtMessage((byte) 0x65);
            speakWords("Audio muted");
        }

        else if(parts[2].contains("unmute")) {
            DataBytes.sendTxtMessage((byte) 0x66);
            speakWords("Audio unmuted");
        }


        else if (parts[2].contains("play"))
        {
          if (parts.length>=2)
          {
              String channel="";
              for (int i=3;i<parts.length;i++)
              {
                  channel=channel+" "+parts[i];
                  if (parts[i].contains("movie"))
                  {
//                      DataBytes.sendTxtMessage((byte) 0x6E);
                      isInterestSet=true;
                      speakWords("There are 2 movies based on your interest. Kick and Wanted ");
                  }else
                  {
                      speakWords("Playing "+channel);
                      DataBytes.sendTxtMessage((byte) 0x67);
                  }

              }

          }
          else
          {
              notAvalidCmd();
          }
        }



        else if (parts[2].equals("increase") || parts[2].equals("decrease") )
        {
            if (parts.length>=3)
            {
                if (parts[3].equals("volume")){

                    if (parts[2].equals("increase") )
                    {
                        DataBytes.sendTxtMessage((byte) 0x68);
                        speakWords("Volume increased");
                    }
                    else {
                        DataBytes.sendTxtMessage((byte) 0x69);
                        speakWords("Volume decreased");
                    }
                }
            }
        }

        else if (parts[2].contains("switch") || parts[2].contains("change") ){
            if (parts.length>5){
                if (parts[3].contains("to") && parts[4].contains("channel") && parts[5].contains("number") )
                {
                    String channel="";
                    for (int i=6;i<parts.length;i++)
                    {
                        channel=channel+" "+parts[i];
                    }
                    DataBytes.sendTxtMessage((byte) 0x6A);
                    speakWords("switched to channel number "+channel);
                }else
                {
                    notAvalidCmd();
                }

            }else {
                notAvalidCmd();
            }
        }


        else if (parts[2].contains("check") && parts[3].contains("for") ){
            if (parts[4].contains("Salman") && parts[5].contains("Khan")  || (parts[4].contains("salman") && parts[5].contains("khan")))
            {
                DataBytes.sendTxtMessage((byte) 0x6B);
                speakWords("Salman khan movie Kick is playing in Star plus Enjoy");
            }
            else if (parts[4].contains("james") && parts[5].contains("bond") || (parts[4].contains("James") && parts[5].contains("Bond")))
            {
                DataBytes.sendTxtMessage((byte) 0x6C);
                speakWords("There are no james bond movies playing");
            }else {
                notAvalidCmd();
            }

        }

    }


    //speak the user text
    private void speakWords(String speech) {

        Log.e("speaking===","===="+speech);
        //speak straight away
        myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
        myTTS.setPitch((float) 0.5);

    }


    //setup TTS
    public void onInit(int initStatus) {

        //check for successful instantiation
        if (initStatus == TextToSpeech.SUCCESS) {
            if(myTTS.isLanguageAvailable(Locale.US)==TextToSpeech.LANG_AVAILABLE)
                myTTS.setLanguage(Locale.US);
        }
        else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }
    }

    private void processVoiceInput(String voiceText) {

       if (voiceText.contains("channel number"))//switching to channel number
        {
            Log.e("enter channel num found","==="+222);
            String found  = fetchNumber(voiceText);
          if (found.equals("notFound"))
            {
                Log.e("Speech",""+false);
                speakWords("Please say valid channel number");
            }
            else
            {
                Log.e("Speech",""+true);
                String words = "Switched to channel "+found;
                speakWords(words);
            }

        }
//        else if (voiceText.contains("actor") || voiceText.contains("actress"))// searching for actor/actress programms
//        {
//            searchActor(" ");
//
//        }else if (voiceText.contains("movie"))//Searching for movie
//        {
//            Log.e("enter movie found","==="+222);
//        }
        else
       {
           speakWords("Please say valid channel number");
       }

    }

    private void searchActor(String actor) {

    }

    private String searchSubString(String input, String subString) {
        StringTokenizer tokens = new StringTokenizer(input, subString);
        String first = tokens.nextToken();
        String second = tokens.nextToken();


        Log.e("second","===="+second);
        Log.e("first","===="+first);
        return second;

    }

    private String fetchNumber(String channelNumber) {
        String toReturn="";
        boolean isFound=false;
        StringBuilder sb = new StringBuilder();

        char[] arr = new char[10];
        for (char ch : channelNumber.toCharArray()) {
            //5
            if (Character.isDigit(ch)) {
                isFound=true;
                Log.e("ch====","==="+ch);
                sb.append(ch);
                toReturn = sb.toString();
            }
        }
        if (isFound)
        {
            return  toReturn;
//           return arr.toString();
        }
        else
        {
            return  "notFound";
        }

    }

    private void switchToChannelNumber(int channelNumber) {
        Log.e("channel number=","======"+channelNumber);
    }

}

