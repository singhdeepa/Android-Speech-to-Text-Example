package com.stacktips.speechtotext.activities;


import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.stacktips.speechtotext.helperClass.ChatController;
import com.stacktips.speechtotext.helperClass.Constants;
import com.stacktips.speechtotext.dataSet.DataBytes;
import com.stacktips.speechtotext.R;

import static com.stacktips.speechtotext.helperClass.Constants.DEVICE_OBJECT;
import static com.stacktips.speechtotext.helperClass.Constants.MESSAGE_READ;
import static com.stacktips.speechtotext.helperClass.Constants.MESSAGE_STATE_CHANGE;
import static com.stacktips.speechtotext.helperClass.Constants.MESSAGE_TOAST;
import static com.stacktips.speechtotext.helperClass.Constants.MESSAGE_WRITE;
import static com.stacktips.speechtotext.helperClass.Constants.REQUEST_ENABLE_BLUETOOTH;
import static com.stacktips.speechtotext.helperClass.Constants.bluetoothAdapter;
import static com.stacktips.speechtotext.helperClass.Constants.chatController;
import java.util.Set;

public class ConnectActivity extends AppCompatActivity implements View.OnClickListener {
    ImageButton btnBluetooth;
    private Dialog dialog;

    private BluetoothDevice connectingDevice;
    private ArrayAdapter<String> discoveredDevicesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_connectivity_type);

        initViews();

        configureBlueTooth();

        initListener();

    }


    private void initViews() {
        btnBluetooth = (ImageButton)findViewById(R.id.btnBluetooth);

    }
    private void initListener() {
        btnBluetooth.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnBluetooth:
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
                } else {
                    chatController = new ChatController(ConnectActivity.this, handler);
                }

                showPrinterPickDialog();
                break;
        }

    }
    private void configureBlueTooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    
    private void showPrinterPickDialog() {
        dialog = new Dialog(ConnectActivity.this);
        dialog.setContentView(R.layout.dialog_bluetooth_list);
        dialog.setTitle(R.string.bluetooth_devices);

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        bluetoothAdapter.startDiscovery();

        //Initializing bluetooth adapters
        ArrayAdapter<String> pairedDevicesAdapter = new ArrayAdapter<>(ConnectActivity.this, android.R.layout.simple_list_item_1);
        discoveredDevicesAdapter = new ArrayAdapter<>(ConnectActivity.this, android.R.layout.simple_list_item_1);

        //locate listviews and attatch the adapters
        ListView listView = (ListView) dialog.findViewById(R.id.pairedDeviceList);
        ListView listView2 = (ListView) dialog.findViewById(R.id.discoveredDeviceList);
        listView.setAdapter(pairedDevicesAdapter);
        listView2.setAdapter(discoveredDevicesAdapter);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(discoveryFinishReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(discoveryFinishReceiver, filter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        final Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            if (pairedDevices.size()>0) {
                pairedDevicesAdapter.clear();
            }
            pairedDevicesAdapter.add(getString(R.string.none_paired));
        }

        //Handling listview item click event
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (pairedDevices.size() > 0) {
                    bluetoothAdapter.cancelDiscovery();
                    String info = ((TextView) view).getText().toString();

                    String address = info.substring(info.length() - 17);
//                    showAlert(getString(R.string.enter_password),address);
                    connectToDevice(address);
                    dialog.dismiss();


                }
            }

        });

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (discoveredDevicesAdapter!=null) {
                    bluetoothAdapter.cancelDiscovery();
                    String info = ((TextView) view).getText().toString();

                    if (info.equals("No devices found") && i==0) {
                        //do nothing
                    }else {
                        String address = info.substring(info.length() - 17);
//                        showAlert(getString(R.string.enter_password),address);
                   connectToDevice(address);
                        dialog.dismiss();
                    }


                }

            }
        });

        dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(true);
        dialog.show();

    }


    private void connectToDevice(String deviceAddress) {
        bluetoothAdapter.cancelDiscovery();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        chatController.connect(device);
    }


    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BLUETOOTH:
                if (resultCode == ConnectActivity.RESULT_OK) {
                    chatController = new ChatController(ConnectActivity.this, handler);
                } else {
                    Toast.makeText(ConnectActivity.this, R.string.bluetooth_disabled, Toast.LENGTH_SHORT).show();

                }
        }
    }

    public Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
//            Log.e("message",""+msg);
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case ChatController.STATE_CONNECTED:
                            reInitializeValues();
                            makeTransition();

                            break;
                        case ChatController.STATE_CONNECTING:
//                            Toast.makeText(ConnectActivityType.this,"Connecting...", Toast.LENGTH_SHORT).show();
                            break;
                        case ChatController.STATE_LISTEN:
                            break;
                        case ChatController.STATE_NONE:
                            Log.e("not conn","STATE_NONE"+122);
//                            connectedStatus.setText("Not Connected");
//                            reInitializeValues();
                            Toast.makeText(ConnectActivity.this, R.string.not_connected, Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);
                    Log.e("msg",""+writeMessage);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
//                    Log.e("len",""+readBuf.length);
//                    String hexVal= bytesToHex(readBuf);
//                    new HandleReceivedData(ConnectActivityType.this).checkforValues(hexVal,readBuf);

                    break;
                case Constants.MESSAGE_DEVICE_OBJECT:
                    connectingDevice = msg.getData().getParcelable(DEVICE_OBJECT);
//                    Toast.makeText(ConnectActivityType.this, "Connected to " + connectingDevice.getName(),
//                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
//                    Toast.makeText(getActivity(), msg.getData().getString("toast"),
//                            Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });

    private void reInitializeValues() {


    }

    @Override
    public void onStart() {
        super.onStart();
//        if (!bluetoothAdapter.isEnabled()) {
//            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
//        } else {
//            chatController = new ChatController(ConnectActivityType.this, handler);
//        }
    }

    private final BroadcastReceiver discoveryFinishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    discoveredDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (discoveredDevicesAdapter.getCount() == 0) {
                    discoveredDevicesAdapter.add(getString(R.string.none_found));
                }
            }
        }
    };


//    private void showAlert(String title, final String address)
//    {
//        LayoutInflater layoutInflater = LayoutInflater.from(this);
//        View promptView = layoutInflater.inflate(R.layout.prompt, null);
//
//        final AlertDialog alertD = new AlertDialog.Builder(this).create();
//
//        TextView titleTxt = (TextView) promptView.findViewById(R.id.title);
//        titleTxt.setText(title);
//
//        final EditText msgTxt = (EditText) promptView.findViewById(R.id.message);
//        msgTxt.setText("1234");
//        msgTxt.setHint(R.string.password);
//        msgTxt.setVisibility(View.VISIBLE);
//
//        final RadioButton btnAdd1 = (RadioButton) promptView.findViewById(R.id.radioYes);
//        btnAdd1.setText(R.string.yes);
//
//        final RadioButton btnAdd2 = (RadioButton) promptView.findViewById(R.id.radioNO);
//        btnAdd2.setText(R.string.cancel);
//
//        btnAdd1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //remove this after testing
//                if(msgTxt.getText().toString().equals("1234")) {
//                    connectToDevice(address);
//
//                }else {
//                    Toast.makeText(ConnectActivity.this, R.string.invalid_password, Toast.LENGTH_SHORT).show();
//
//                }
//                alertD.dismiss();
//
//
//            }
//        });
//        btnAdd2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                alertD.dismiss();
//
//            }
//        });
//
//        alertD.setView(promptView);
//
//        alertD.show();
//
//    }

    private  void makeTransition(){
        new DataBytes(ConnectActivity.this).sendTxtMessage("msg");
//        handler.postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//        isWifi=false;
        Toast.makeText(ConnectActivity.this, getString(R.string.connected_to) + connectingDevice.getName(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ConnectActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);

//            }
//        },3000);


    }



    @Override
    public void onResume() {
        super.onResume();
        // ... your own onResume implementation
      }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatController != null)
            chatController.stop();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(ConnectActivity.this,MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);

        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_right);
        finish();
    }
}
