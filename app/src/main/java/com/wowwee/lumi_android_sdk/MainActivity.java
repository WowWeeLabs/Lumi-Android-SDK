package com.wowwee.lumi_android_sdk;

import android.bluetooth.BluetoothAdapter;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.wowwee.bluetoothrobotcontrollib.BluetoothRobot;
import com.wowwee.bluetoothrobotcontrollib.lumi.LumiRobot;
import com.wowwee.bluetoothrobotcontrollib.lumi.LumiRobotFinder;
import com.wowwee.lumi_android_sdk.fragment.ConnectFragment;
import com.wowwee.lumi_android_sdk.utils.FragmentHelper;

import java.util.List;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BluetoothAdapter.getDefaultAdapter();
        FragmentHelper.switchFragment(getSupportFragmentManager(), new ConnectFragment(), R.id.view_id_content, false);
    }

    @Override
    public void onStop() {
        super.onStop();
        for (LumiRobot robot : (List<LumiRobot>)LumiRobotFinder.getInstance().getmLumiRobotConnectedList()){
            robot.disconnect();
        }
        System.exit(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // disable idle timer
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        for (LumiRobot robot : (List<LumiRobot>)LumiRobotFinder.getInstance().getmLumiRobotConnectedList()){
            robot.disconnect();
        }

        BluetoothRobot.unbindBluetoothLeService(MainActivity.this);

        System.exit(0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
