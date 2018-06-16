package com.stacktips.speechtotext.activities;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stacktips.speechtotext.R;
import com.stacktips.speechtotext.dataSet.DataBytes;

public class SearchAndDisplayActivity extends AppCompatActivity {

    private static final String TAG="DisplayActivity";
    FirebaseDatabase database;
    DatabaseReference myRef;
    String matchingString="";


    static Context context;
    private MyWebChromeClient mWebChromeClient = null;
    private View mCustomView;
    private RelativeLayout mContentView;
    private FrameLayout mCustomViewContainer;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;

    private static WebView myWebView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_display);

        context = SearchAndDisplayActivity.this;
//        // Write a message to the database
//         database = FirebaseDatabase.getInstance();
//         myRef = database.getReference("message");

//         setupAudioManager(matchingString);

         setWebView();

    }

    private void setWebView() {
         myWebView = (WebView) findViewById(R.id.webView);
        mWebChromeClient = new MyWebChromeClient();
        myWebView.setWebChromeClient(mWebChromeClient);
        myWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
//        myWebView.loadUrl(new DataBytes(SearchAndDisplayActivity.this).receiveVoiceTxt("101", ));

    }

    public static void  showWebView(String url){
        myWebView.loadUrl(url);

    }

    public static void setupAudioManager(int matchingString) {
        final AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
         if(matchingString == 104) {
             audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 40, 0);
         }
         else if(matchingString == 105){
             audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);

         } else if(matchingString == 101){
             audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);

         }else if(matchingString == 102){
             audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
         }


//        audioManager.setMicrophoneMute(true);

    }
    @Override
    protected void onResume() {
        super.onResume();
        // Read from the database
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                String value = dataSnapshot.getValue(String.class);
//                Log.d(TAG, "Value is: " + value);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
//            }
//        });
    }
    public class MyWebChromeClient extends WebChromeClient {

        FrameLayout.LayoutParams LayoutParameters = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            // if a view already exists then immediately terminate the new one
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            mContentView = (RelativeLayout) findViewById(R.id.activity_main);
            mContentView.setVisibility(View.GONE);
            mCustomViewContainer = new FrameLayout(SearchAndDisplayActivity.this);
            mCustomViewContainer.setLayoutParams(LayoutParameters);
            mCustomViewContainer.setBackgroundResource(android.R.color.black);
            view.setLayoutParams(LayoutParameters);
            mCustomViewContainer.addView(view);
            mCustomView = view;
            mCustomViewCallback = callback;
            mCustomViewContainer.setVisibility(View.VISIBLE);
            setContentView(mCustomViewContainer);
        }

        @Override
        public void onHideCustomView() {
            if (mCustomView == null) {
                return;
            } else {
                // Hide the custom view.
                mCustomView.setVisibility(View.GONE);
                // Remove the custom view from its container.
                mCustomViewContainer.removeView(mCustomView);
                mCustomView = null;
                mCustomViewContainer.setVisibility(View.GONE);
                mCustomViewCallback.onCustomViewHidden();
                // Show the content view.
                mContentView.setVisibility(View.VISIBLE);
                setContentView(mContentView);
            }
        }
    }
    @Override
    public void onBackPressed() {
        if (mCustomViewContainer != null)
            mWebChromeClient.onHideCustomView();
        else if (myWebView.canGoBack())
            myWebView.goBack();
        else
            super.onBackPressed();
    }
}


