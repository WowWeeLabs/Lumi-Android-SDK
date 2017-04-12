package com.wowwee.lumi_android_sdk.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.wowwee.bluetoothrobotcontrollib.lumi.LumiRobot;
import com.wowwee.bluetoothrobotcontrollib.lumi.LumiRobotFinder;
import com.wowwee.lumi_android_sdk.R;
import com.wowwee.lumi_android_sdk.utils.FragmentHelper;

/**
 * Created by davidchan on 22/3/2017.
 */

public class SettingFragment extends LumiBaseFragment {
    Handler handler;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null)
            return null;

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        getActivity().getWindow().getDecorView().setSystemUiVisibility(flags);

        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        handler = new Handler();

        ListView listView = (ListView)view.findViewById(R.id.menuTable);
        String[] ledNameArr = {"Back", "Get Firmware", "Get Battery Level", "Set Wall Detection On/Off", "Calibrate", "Reset Calibration"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, ledNameArr);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                if (LumiRobotFinder.getInstance().getmLumiRobotConnectedList().size() > 0) {
                    final LumiRobot robot = (LumiRobot)LumiRobotFinder.getInstance().getmLumiRobotConnectedList().get(0);
                    robot.setCallbackInterface(SettingFragment.this);
                    switch (position) {
                        case 0:
                            FragmentHelper.switchFragment(getActivity().getSupportFragmentManager(), new MenuFragment(), R.id.view_id_content, false);
                            break;
                        case 1:
                            robot.lumiFirmwareVersion();
                            break;
                        case 2:
                            robot.lumiGetBatteryLevel();
                            break;
                        case 3:
                            AlertDialog.Builder builder;
                            builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("Set Wall DetectionSet Wall Detection");
                            builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    robot.lumiSetWallDetectionMode(true);
                                    dialog.cancel();
                                }
                            });
                            builder.setNegativeButton("Disable", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    robot.lumiSetWallDetectionMode(false);
                                    dialog.cancel();
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            Window dialogWindow = alertDialog.getWindow();
                            dialogWindow.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                            dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                            alertDialog.show();

                            dialogWindow.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                            dialogWindow.getDecorView().setSystemUiVisibility(getActivity().getWindow().getDecorView().getSystemUiVisibility());
                            alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                            break;
                        case 4:
                            robot.lumiCalibrate();
                            break;
                        case 5:
                            robot.lumiResetCalibration();
                            break;
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void lumiDidCalibrate(LumiRobot lumiRobot, final boolean b) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Calibration");
                alertDialog.setMessage(b?"Success":"Failed");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                Window dialogWindow = alertDialog.getWindow();
                dialogWindow.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                alertDialog.show();

                dialogWindow.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                dialogWindow.getDecorView().setSystemUiVisibility(getActivity().getWindow().getDecorView().getSystemUiVisibility());
                alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            }
        });
    }

    @Override
    public void lumiDidResetCalibration(LumiRobot var1, final boolean b) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Reset Calibration");
                alertDialog.setMessage(b?"Success":"Failed");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                Window dialogWindow = alertDialog.getWindow();
                dialogWindow.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                alertDialog.show();

                dialogWindow.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                dialogWindow.getDecorView().setSystemUiVisibility(getActivity().getWindow().getDecorView().getSystemUiVisibility());
                alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            }
        });
    }

    @Override
    public void lumiDidReceiveFirmwareVersion(LumiRobot lumiRobot, final int i, final int i1) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Firmware");
                alertDialog.setMessage("" + i + "." + i1);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                Window dialogWindow = alertDialog.getWindow();
                dialogWindow.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                alertDialog.show();

                dialogWindow.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                dialogWindow.getDecorView().setSystemUiVisibility(getActivity().getWindow().getDecorView().getSystemUiVisibility());
                alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            }
        });
    }

    @Override
    public void lumiDidReceiveBatteryInfo(LumiRobot lumiRobot, final float i) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Battery Level");
                alertDialog.setMessage("" + i + "v");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                Window dialogWindow = alertDialog.getWindow();
                dialogWindow.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                alertDialog.show();

                dialogWindow.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                dialogWindow.getDecorView().setSystemUiVisibility(getActivity().getWindow().getDecorView().getSystemUiVisibility());
                alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            }
        });
    }
}
