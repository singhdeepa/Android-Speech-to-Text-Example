package com.stacktips.speechtotext.dataSet;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PushToFirebase {

    public static void pushToFB()
    {
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        ChannelModel c1 = new ChannelModel("setmax","1","tzp",
                           "amir khan","xyz");
        ChannelModel c2 = new ChannelModel("sony","2","CID",
                "salman khan","ccc");


        mDatabase.child("channels").child(c1.getChannelNumber()).setValue(c1);
        mDatabase.child("channels").child(c2.getChannelNumber()).setValue(c2);


    }
}
