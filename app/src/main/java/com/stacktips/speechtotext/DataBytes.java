package com.stacktips.speechtotext;

import android.content.Context;
import android.util.Log;

import static com.stacktips.speechtotext.Constants.chatController;


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
    public void sendTxtMessage(String mssg)
    {
           byte[] data = new byte[400];
            data[0] = 0x6E; //110


            byte[] array = mssg.getBytes();
            //length of Payload
            data[1] = (byte) array.length;  // mssg length
            Log.e(" data[1]=",""+ data[1]);
            Log.e("msg=",""+mssg);
            //Payload
            for (int i=2;i<array.length+2;i++)
            {
                data[i]=array[i-2];

            }

            for (int j=array.length+2;j<397;j++)
            {

                data[j]=(byte) 0xFF;
            }
            data[398] = 0x0A;
            data[399] = 0x0D;

            Log.e("array",""+array);
            Log.e("array len",""+array.length);

            Log.e("payload",""+data);
            chatController.write(data);

//        bt.send(data, false);

    }

}
