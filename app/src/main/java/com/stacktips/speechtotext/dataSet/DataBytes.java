package com.stacktips.speechtotext.dataSet;

import android.content.Context;
import android.util.Log;

import static com.stacktips.speechtotext.helperClass.Constants.chatController;


/**
 * Created by atreya on 22/9/17.
 */

//Note : This class only contains user panel data bytes
public class DataBytes {

    Context mContext;

    public DataBytes(Context context) {
        mContext = context;

    }
    //Text message
    public static  void sendTxtMessage(byte b)
    {
           byte[] data = new byte[400];
            data[0] = b; //110


            for (int i=1;i<399;i++)
            {
                data[i]=(byte) 0xFF;

            }

            data[398] = 0x0A;
            data[399] = 0x0D;


            chatController.write(data);

//        bt.send(data, false);

    }

}
