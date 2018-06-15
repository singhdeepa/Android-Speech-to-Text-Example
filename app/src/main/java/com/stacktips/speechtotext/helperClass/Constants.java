package com.stacktips.speechtotext.helperClass;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;


import com.stacktips.speechtotext.helperClass.ChatController;

/**
 * Created by bitjini on 23/10/17.
 */
public class Constants {
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_OBJECT = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final String DEVICE_OBJECT = "device_name";
    public static final int REQUEST_ENABLE_BLUETOOTH = 1;

    public static BluetoothAdapter bluetoothAdapter;

    public static ChatController chatController;
    public static Context context = null;
}