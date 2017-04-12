package com.wowwee.lumi_android_sdk.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.wowwee.bluetoothrobotcontrollib.lumi.LumiRobot;
import com.wowwee.bluetoothrobotcontrollib.lumi.LumiRobotFinder;
import com.wowwee.lumi_android_sdk.R;
import com.wowwee.lumi_android_sdk.utils.FragmentHelper;
import com.wowwee.bluetoothrobotcontrollib.lumi.LumiRobotConstant.kLumiStunt;

/**
 * Created by davidchan on 6/4/2017.
 */

public class StuntFragment extends LumiBaseFragment {
    Handler handler;
    String[] stuntNameArr;
    ListView listView;

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

        listView = (ListView)view.findViewById(R.id.menuTable);
        String[] arr = {"Back", "Take Off", "kLumiStuntYawBackAndForth", "kLumiStuntShortYawLeft", "kLumiStuntShortYawRight", "kLumiStuntShortThrustPulse", "kLumiStuntShortNegThrustPulse", "kLumiStuntWobbleRoll", "kLumiStuntWobblePitch", "kLumiStuntRollPitchL", "kLumiStuntRollPitchR", "kLumiStuntPitch", "kLumiStuntRoll", "kLumiStuntMoonWalk", "kLumiStuntSpiralUp", "kLumiStuntLeftFlip", "kLumiStuntSwayFrontBack", "kLumiStuntSwayLeftRight", "kLumiStuntZigZagUp", "kLumiStuntZigZagDown", "kLumiStuntSpiralDown", "kLumiStuntRightFlip", "kLumiStuntBackFlip", "kLumiStuntFrontFlip"};
        stuntNameArr = arr;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, stuntNameArr);
        listView.setAdapter(adapter);

        if (LumiRobotFinder.getInstance().getmLumiRobotConnectedList().size() > 0) {
            LumiRobot robot = (LumiRobot) LumiRobotFinder.getInstance().getmLumiRobotConnectedList().get(0);
            robot.lumiSetBeaconMode(true);
            robot.lumiDeactivateFollowMeMode();
            robot.lumiSetAltitudeMode(true);
            robot.setCallbackInterface(this);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                if (LumiRobotFinder.getInstance().getmLumiRobotConnectedList().size() > 0) {
                    final LumiRobot robot = (LumiRobot)LumiRobotFinder.getInstance().getmLumiRobotConnectedList().get(0);

                    AlertDialog.Builder builder;
                    LinearLayout layout = new LinearLayout(getActivity());
                    final EditText txtV1 = new EditText(getActivity());
                    final EditText txtV2 = new EditText(getActivity());
                    switch (position) {
                        case 0:
                            handler = null;
                            FragmentHelper.switchFragment(getActivity().getSupportFragmentManager(), new MenuFragment(), R.id.view_id_content, false);
                            break;
                        case 1:
                            if (stuntNameArr[1].contentEquals("Take Off"))
                                robot.lumiLandOrTakeOff(false);
                            else if (stuntNameArr[1].contentEquals("Land"))
                                robot.lumiLandOrTakeOff(true);
                            break;
                        case 2:
                            robot.lumiPerformStunt(kLumiStunt.YawBackAndForth, 0, 0);
                            break;
                        case 3:
                            robot.lumiPerformStunt(kLumiStunt.ShortYawLeft, 0, 0);
                            break;
                        case 4:
                            robot.lumiPerformStunt(kLumiStunt.ShortYawRight, 0, 0);
                            break;
                        case 5:
                            builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("ShortThrustPulse");
                            txtV1.setHint("Intensity: (0-255)");
                            txtV2.setHint("Duration: 10ms (0-255)");
                            layout.setOrientation(LinearLayout.VERTICAL);
                            layout.addView(txtV1);
                            layout.addView(txtV2);
                            builder.setView(layout);
                            builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int data1 = 0;
                                    int data2 = 0;
                                    if (txtV1.getText().toString().matches("\\d+")) {
                                        data1 = Integer.parseInt(txtV1.getText().toString());
                                        data1 = Math.max(Math.min(data1, 255), 0);
                                    }
                                    if (txtV2.getText().toString().matches("\\d+")) {
                                        data2 = Integer.parseInt(txtV2.getText().toString());
                                        data2 = Math.max(Math.min(data2, 255), 0);
                                    }
                                    robot.lumiPerformStunt(kLumiStunt.ShortThrustPulse, data1, data2);
                                    dialog.cancel();
                                }
                            });
                            builder.setNegativeButton("Fill Default Value", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    robot.lumiPerformStunt(kLumiStunt.ShortThrustPulse, 50, 50);
                                }
                            });
                            builder.show();
                            break;
                        case 6:
                            builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("ShortNegThrustPulse");
                            txtV1.setHint("Duration: 10ms (0-255)");
                            layout.setOrientation(LinearLayout.VERTICAL);
                            layout.addView(txtV1);
                            builder.setView(layout);
                            builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int data1 = 0;
                                    if (txtV1.getText().toString().matches("\\d+")) {
                                        data1 = Integer.parseInt(txtV1.getText().toString());
                                        data1 = Math.max(Math.min(data1, 255), 0);
                                    }
                                    robot.lumiPerformStunt(kLumiStunt.ShortNegThrustPulse, 0, data1);
                                    dialog.cancel();
                                }
                            });
                            builder.setNegativeButton("Fill Default Value", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    robot.lumiPerformStunt(kLumiStunt.ShortNegThrustPulse, 0, 50);
                                }
                            });
                            builder.show();
                            break;
                        case 7:
                            robot.lumiPerformStunt(kLumiStunt.WobbleRoll, 0, 0);
                            break;
                        case 8:
                            robot.lumiPerformStunt(kLumiStunt.WobblePitch, 0, 0);
                            break;
                        case 9:
                            builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("RollPitchL");
                            txtV1.setHint("Degree: (-180-180)");
                            txtV2.setHint("Duration: 10ms (0-255)");
                            layout.setOrientation(LinearLayout.VERTICAL);
                            layout.addView(txtV1);
                            layout.addView(txtV2);
                            builder.setView(layout);
                            builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int data1 = 0;
                                    int data2 = 0;
                                    if (txtV1.getText().toString().matches("\\d+")) {
                                        data1 = Integer.parseInt(txtV1.getText().toString());
                                        data1 = Math.max(Math.min(data1, 255), 0);
                                    }
                                    if (txtV2.getText().toString().matches("\\d+")) {
                                        data2 = Integer.parseInt(txtV2.getText().toString());
                                        data2 = Math.max(Math.min(data2, 255), 0);
                                    }
                                    robot.lumiPerformStunt(kLumiStunt.RollPitchL, data1, data2);
                                    dialog.cancel();
                                }
                            });
                            builder.setNegativeButton("Fill Default Value", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    robot.lumiPerformStunt(kLumiStunt.RollPitchL, 50, 50);
                                }
                            });
                            builder.show();
                            break;
                        case 10:
                            builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("RollPitchR");
                            txtV1.setHint("Degree: (-180-180)");
                            txtV2.setHint("Duration: 10ms (0-255)");
                            layout.setOrientation(LinearLayout.VERTICAL);
                            layout.addView(txtV1);
                            layout.addView(txtV2);
                            builder.setView(layout);
                            builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int data1 = 0;
                                    int data2 = 0;
                                    if (txtV1.getText().toString().matches("\\d+")) {
                                        data1 = Integer.parseInt(txtV1.getText().toString());
                                        data1 = Math.max(Math.min(data1, 255), 0);
                                    }
                                    if (txtV2.getText().toString().matches("\\d+")) {
                                        data2 = Integer.parseInt(txtV2.getText().toString());
                                        data2 = Math.max(Math.min(data2, 255), 0);
                                    }
                                    robot.lumiPerformStunt(kLumiStunt.RollPitchR, data1, data2);
                                    dialog.cancel();
                                }
                            });
                            builder.setNegativeButton("Fill Default Value", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    robot.lumiPerformStunt(kLumiStunt.RollPitchR, 50, 50);
                                }
                            });
                            builder.show();
                            break;
                        case 11:
                            builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("Pitch");
                            txtV1.setHint("Degree: (-180-180)");
                            txtV2.setHint("Duration: 10ms (0-255)");
                            layout.setOrientation(LinearLayout.VERTICAL);
                            layout.addView(txtV1);
                            layout.addView(txtV2);
                            builder.setView(layout);
                            builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int data1 = 0;
                                    int data2 = 0;
                                    if (txtV1.getText().toString().matches("\\d+")) {
                                        data1 = Integer.parseInt(txtV1.getText().toString());
                                        data1 = Math.max(Math.min(data1, 255), 0);
                                    }
                                    if (txtV2.getText().toString().matches("\\d+")) {
                                        data2 = Integer.parseInt(txtV2.getText().toString());
                                        data2 = Math.max(Math.min(data2, 255), 0);
                                    }
                                    robot.lumiPerformStunt(kLumiStunt.Pitch, data1, data2);
                                    dialog.cancel();
                                }
                            });
                            builder.setNegativeButton("Fill Default Value", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    txtV1.setText("50");
                                    txtV2.setText("50");
                                    robot.lumiPerformStunt(kLumiStunt.Pitch, 50, 50);
                                }
                            });
                            builder.show();
                            break;
                        case 12:
                            builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("Roll");
                            txtV1.setHint("Degree: (-180-180)");
                            txtV2.setHint("Duration: 10ms (0-255)");
                            layout.setOrientation(LinearLayout.VERTICAL);
                            layout.addView(txtV1);
                            layout.addView(txtV2);
                            builder.setView(layout);
                            builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int data1 = 0;
                                    int data2 = 0;
                                    if (txtV1.getText().toString().matches("\\d+")) {
                                        data1 = Integer.parseInt(txtV1.getText().toString());
                                        data1 = Math.max(Math.min(data1, 255), 0);
                                    }
                                    if (txtV2.getText().toString().matches("\\d+")) {
                                        data2 = Integer.parseInt(txtV2.getText().toString());
                                        data2 = Math.max(Math.min(data2, 255), 0);
                                    }
                                    robot.lumiPerformStunt(kLumiStunt.Roll, data1, data2);
                                    dialog.cancel();
                                }
                            });
                            builder.setNegativeButton("Fill Default Value", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    robot.lumiPerformStunt(kLumiStunt.Roll, 50, 50);
                                }
                            });
                            builder.show();
                            break;
                        case 13:
                            robot.lumiPerformStunt(kLumiStunt.MoonWalk, 0, 0);
                            break;
                        case 14:
                            robot.lumiPerformStunt(kLumiStunt.SpiralUp, 0, 0);
                            break;
                        case 15:
                            robot.lumiPerformStunt(kLumiStunt.LeftFlip, 0, 0);
                            break;
                        case 16:
                            robot.lumiPerformStunt(kLumiStunt.SwayFrontBack, 0, 0);
                            break;
                        case 17:
                            robot.lumiPerformStunt(kLumiStunt.SwayLeftRight, 0, 0);
                            break;
                        case 18:
                            robot.lumiPerformStunt(kLumiStunt.ZigZagUp, 0, 0);
                            break;
                        case 19:
                            robot.lumiPerformStunt(kLumiStunt.ZigZagDown, 0, 0);
                            break;
                        case 20:
                            robot.lumiPerformStunt(kLumiStunt.SpiralDown, 0, 0);
                            break;
                        case 21:
                            robot.lumiPerformStunt(kLumiStunt.RightFlip, 0, 0);
                            break;
                        case 22:
                            robot.lumiPerformStunt(kLumiStunt.ZigZagUp, 0, 0);
                            break;
                        case 23:
                            robot.lumiPerformStunt(kLumiStunt.BackFlip, 0, 0);
                            break;
                        case 24:
                            robot.lumiPerformStunt(kLumiStunt.FrontFlip, 0, 0);
                            break;
                    }
                }
            }
        });
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getStatus();
            }
        }, 500);

        return view;
    }

    void getStatus() {
        if (LumiRobotFinder.getInstance().getmLumiRobotConnectedList().size() > 0) {
            LumiRobot robot = (LumiRobot) LumiRobotFinder.getInstance().getmLumiRobotConnectedList().get(0);
            robot.lumiGetQuadcopterStatus();
            if (handler != null) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getStatus();
                    }
                }, 500);
            }
        }
    }

    @Override
    public void lumiDidReceiveQuadcopterStatus(LumiRobot lumiRobot, int i) {
        if (i == 0 || i == 2) {
            stuntNameArr[1] = "Take Off";

        }
        else if (i == 1) {
            stuntNameArr[1] = "Land";

        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                ArrayAdapter<String> adapter = (ArrayAdapter<String>)listView.getAdapter();
                adapter.notifyDataSetChanged();
            }
        });
    }
}
