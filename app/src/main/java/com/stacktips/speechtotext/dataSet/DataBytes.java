package com.stacktips.speechtotext.dataSet;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import static com.stacktips.speechtotext.activities.SearchAndDisplayActivity.setupAudioManager;
import static com.stacktips.speechtotext.activities.SearchAndDisplayActivity.showWebView;
import static com.stacktips.speechtotext.helperClass.Constants.chatController;
import static com.stacktips.speechtotext.helperClass.Constants.context;


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
    //Text message
    public String receiveVoiceTxt(String hexVal, byte[] readBuf)
    {

        int unsignedType = unsignedToBytes(readBuf[0]);
        Log.d("unsignedType", "= " + unsignedType);
        switch (unsignedType) {

            case 0x65:
                // mute
                setupAudioManager(101);
                break;

            case 0x66:
                // unMute
                setupAudioManager(102);
            case 0x67:
                //play
            // aajtak live
               showWebView("https://www.youtube.com/watch?v=X7Ktabhd8a4");
                break;
            case 0x68:
                //increase
                setupAudioManager(104);
                break;
            case 0x69:
                //decrease
                setupAudioManager(105);
                break;
            case 0x6A:
                // switch or change
                showWebView("https://www.youtube.com/watch?v=6wTBWneWimU");

                break;
            case 0x6B:
                // actor
                showWebView("https://www.youtube.com/watch?v=XJ4BsLFoNIk");

                break;
            case 0x6C:
                // James bond
//                sendTxtMessage("404");
//                Toast.makeText(context,"Content not available",Toast.LENGTH_LONG).show();
                break;
            case 0x6D:
                    break;
            case 0x6E:
                int unsignedType2 = unsignedToBytes(readBuf[1]);
                if(unsignedType2==0x70){
                    showWebView("https://www.youtube.com/watch?v=XJ4BsLFoNIk");

                }else {
                    showWebView("https://www.youtube.com/watch?v=LDA3PoDGW8Q");

                }break;
            case 0x72:
                showWebView("https://www.youtube.com/watch?v=SDY1N-IJOA8");
                break;


        }
        return "";
    }
    public static int unsignedToBytes(byte b) {
        return b & 0xFF;//255
    }
}
