package com.stacktips.speechtotext;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by spoorthi on 30/7/17.
 */

public class GrantPermissions
{

    Activity activity;

    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;

    String[] permissionsRequired = new String[]{
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.WRITE_SETTINGS,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private SharedPreferences permissionStatus;
    private static boolean sentToSettings = false;

    public GrantPermissions(Activity activity) {
        this.activity = activity;
    }

    public void addPermission()
    {
        permissionStatus = activity.getSharedPreferences("permissionStatus",MODE_PRIVATE);

        if(ActivityCompat.checkSelfPermission(activity, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, permissionsRequired[3]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, permissionsRequired[4]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, permissionsRequired[5]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, permissionsRequired[6]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, permissionsRequired[7]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, permissionsRequired[8]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, permissionsRequired[9]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, permissionsRequired[10]) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity,permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(activity,permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(activity,permissionsRequired[2])
                    || ActivityCompat.shouldShowRequestPermissionRationale(activity,permissionsRequired[3])
                    || ActivityCompat.shouldShowRequestPermissionRationale(activity,permissionsRequired[4])
                    || ActivityCompat.shouldShowRequestPermissionRationale(activity,permissionsRequired[5])
                    || ActivityCompat.shouldShowRequestPermissionRationale(activity,permissionsRequired[6])
                    || ActivityCompat.shouldShowRequestPermissionRationale(activity,permissionsRequired[7])
                    || ActivityCompat.shouldShowRequestPermissionRationale(activity,permissionsRequired[8])
                            || ActivityCompat.shouldShowRequestPermissionRationale(activity,permissionsRequired[9])
                            || ActivityCompat.shouldShowRequestPermissionRationale(activity,permissionsRequired[10])

                    )
            {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Bluetooth, Wifi and other permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(activity,permissionsRequired,PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
//            else if (permissionStatus.getBoolean(permissionsRequired[0],false))
//            {
//                //Previously Permission Request was cancelled with 'Dont Ask Again',
//                // Redirect to Settings after showing Information about why you need the permission
//                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//                builder.setTitle("Need Multiple Permissions");
//                builder.setMessage("This app needs Camera and Location permissions.");
//                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                        sentToSettings = true;
//                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                        Uri uri = Uri.fromParts("package", getPackageName(), null);
//                        intent.setData(uri);
//                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
//                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant  Camera and Location", Toast.LENGTH_LONG).show();
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//                builder.show();
//            }
            else {
                //just request the permission
                ActivityCompat.requestPermissions(activity,permissionsRequired,PERMISSION_CALLBACK_CONSTANT);
            }

//            txtPermissions.setText("Permissions Required");

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0],true);
            editor.commit();
        } else {
            //You already have the permission, just go ahead.
            proceedAfterPermission();
        }
    }

    private void proceedAfterPermission()
    {
//        txtPermissions.setText("We've got all permissions");
//        Toast.makeText(activity, "We got All Permissions", Toast.LENGTH_LONG).show();
    }

    public void checkOnPostResume()
    {
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(activity, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }

    public void checkOnActivityResult(int requestCode)
    {
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(activity, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }
}