package com.stacktips.speechtotext.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stacktips.speechtotext.R;
import com.stacktips.speechtotext.dataSet.ChannelModel;
import com.stacktips.speechtotext.dataSet.PushToFirebase;

import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView mVoiceInputTv;
    private ImageButton mSpeakBtn;
    ChannelModel channelModel;
    ArrayList<ChannelModel> channelList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initListners();
        //  pushChannelsToFirebase();
        getChannels();

        //for debugging
        processVoiceInput("change to channel number 15");

    }

    private void pushChannelsToFirebase() {
        PushToFirebase.pushToFB();

    }

    private void getChannels() {
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("channels");

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
            }
        });
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
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mVoiceInputTv.setText(result.get(0));
                    Log.e("result","======="+result.toString());
//                    processVoiceInput(result.get(0).toLowerCase());
                }
                break;
            }

        }
    }

    private void processVoiceInput(String voiceText) {
//        switch (voiceText)
//        {
//            case "":{
//
//            }
        // }

        if (voiceText.equals("swtich on tv"))//on TV
        {

        }
        else if  (voiceText.equals("switch off tv")) //off tv
        {

        }
        else if (voiceText.contains("channel number"))//switching to channel number
        {

            String subString= searchSubString(voiceText,"channel number");
            int cNumber  = fetchNumber(voiceText);
            switchToChannelNumber(cNumber);
        }
        else if (voiceText.contains("actor") || voiceText.contains("actress"))// searching for actor/actress programms
        {
            searchActor(" ");

        }else if (voiceText.contains("movie"))//Searching for movie
        {

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

    private int fetchNumber(String channelNumber) {

        String x="";
        for (char ch : channelNumber.toCharArray()) {
            //5
            if (Character.isDigit(ch)) {
                System.out.print(ch);
                x.concat(String.valueOf(ch));
            }
        }

        String cNumber  = channelNumber.replaceAll("[^0-9]", "");

//        String cNumber=  CharMatcher.DIGIT.retainFrom("abc12 3def");
        Log.e("channel number=","======"+x);
        return 0;

    }

    private void switchToChannelNumber(int channelNumber) {
        Log.e("channel number=","======"+channelNumber);
    }
}
