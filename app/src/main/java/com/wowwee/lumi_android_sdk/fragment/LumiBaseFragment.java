package com.wowwee.lumi_android_sdk.fragment;

import android.support.v4.app.Fragment;

import com.wowwee.bluetoothrobotcontrollib.lumi.LumiRobot;
import com.wowwee.bluetoothrobotcontrollib.lumi.LumiRobotConstant;


/**
 * Created by davidchan on 24/3/2017.
 */

public class LumiBaseFragment extends Fragment implements LumiRobot.LUMIRobotInterface {

    @Override
    public void lumiDeviceReady(LumiRobot lumiRobot) {

    }

    @Override
    public void lumiDeviceDisconnected(LumiRobot lumiRobot) {

    }

    @Override
    public void lumiDidReceiveQuadcopterStatus(LumiRobot lumiRobot, int i) {

    }

    @Override
    public void lumiDidReceiveNotifyError(LumiRobot lumiRobot, LumiRobotConstant.kLumiNotifyError error) {

    }

    @Override
    public void lumiDidCalibrate(LumiRobot lumiRobot, boolean b) {

    }

    @Override
    public void lumiDidReceiveBeaconMode(LumiRobot lumiRobot, boolean b) {

    }

    @Override
    public void lumiDidReceiveAltitudeMode(LumiRobot lumiRobot, boolean b) {

    }

    @Override
    public void lumiDidReceiveSignalStrength(LumiRobot lumiRobot, int i) {

    }

    @Override
    public void lumiDidReceivePosition(LumiRobot lumiRobot, int i, int i1, int i2) {

    }

    @Override
    public void lumiDidReceiveFirmwareVersion(LumiRobot lumiRobot, int i, int i1) {

    }

    @Override
    public void lumiDidReceiveWallDetected(LumiRobot lumiRobot, int i) {

    }

    @Override
    public void lumiDidReceiveWallDetectionModeResponse(LumiRobot lumiRobot, boolean b) {

    }

    @Override
    public void lumiDidReceiveCrashDetectionModeResponse(LumiRobot lumiRobot, boolean b) {

    }

    @Override
    public void lumiDidReceiveStallDetectionModeResponse(LumiRobot lumiRobot, boolean b) {

    }

    @Override
    public void lumiDidResetCalibration(LumiRobot lumiRobot, boolean b) {

    }

    @Override
    public void didNotifyModifiedZ(LumiRobot lumiRobot, int i) {

    }

    @Override
    public void lumiDidNotifyFirstSonar(LumiRobot lumiRobot) {

    }

    @Override
    public void lumiDidReceiveBatteryInfo(LumiRobot lumiRobot, float i) {

    }
}
