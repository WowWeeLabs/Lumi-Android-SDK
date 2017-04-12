package com.wowwee.lumi_android_sdk.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.wowwee.bluetoothrobotcontrollib.lumi.LumiCommandValues;
import com.wowwee.bluetoothrobotcontrollib.lumi.LumiRobot;
import com.wowwee.bluetoothrobotcontrollib.lumi.LumiRobotConstant;
import com.wowwee.bluetoothrobotcontrollib.lumi.LumiRobotFinder;
import com.wowwee.lumi_android_sdk.R;
import com.wowwee.lumi_android_sdk.utils.FragmentHelper;
import com.wowwee.lumi_android_sdk.utils.JoystickData;
import com.wowwee.lumi_android_sdk.utils.JoystickDrawer;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by davidchan on 10/4/2017.
 */

public class DriveFragment extends LumiBaseFragment implements View.OnTouchListener {
    Handler handler;
    Button takeOffBtn;
    SurfaceView touchArea;
    protected JoystickData joystickData;
    protected JoystickDrawer joystickDrawer;
    protected Bitmap outerRingBitmap;
    protected int singleJoystickDrawableId;
    protected int outerRingDrawableId;
    protected Bitmap leftBitmap;
    protected Rect viewRect;
    protected boolean moveMip;
    protected float[] movementVector = new float[]{0, 0};

    protected boolean disableAllCommand = false;
    protected int stuntX, stuntY, stuntZ, lumiYAW;

    int getStatusTime;
    Timer joystickTimer;

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

        viewRect = new Rect();
        getActivity().getWindowManager().getDefaultDisplay().getRectSize(viewRect);

        singleJoystickDrawableId = R.drawable.joystick_right_centre;
        outerRingDrawableId = R.drawable.joystick_bg;

        View view = inflater.inflate(R.layout.fragment_drive, container, false);

        //handle the touch area
        touchArea = (SurfaceView)view.findViewById(R.id.view_id_touch_area);
        touchArea.setZOrderOnTop(true);
        touchArea.getHolder().setFormat(PixelFormat.TRANSLUCENT);

        joystickData = new JoystickData(JoystickData.TYPE.RIGHT);

        //create the bitmps for joystick
        BitmapFactory.Options bitmapFactoryOption = new BitmapFactory.Options();
        bitmapFactoryOption.inScaled = false;
        outerRingBitmap = BitmapFactory.decodeResource(getResources(), outerRingDrawableId, bitmapFactoryOption);
        leftBitmap = BitmapFactory.decodeResource(getResources(), singleJoystickDrawableId, bitmapFactoryOption);

        joystickTimer = new Timer();
        joystickTimer.scheduleAtFixedRate(new JoystickTimerTask(), 0, 100);

        //create the joystick drawer
        joystickDrawer = new JoystickDrawer(outerRingBitmap, leftBitmap);

        //compute the draw ratio for joystick
        float drawRatio = (viewRect.width() / 2.0f < outerRingBitmap.getWidth())?0.5f:1.0f;

        joystickDrawer.setDrawRatio(drawRatio);
        joystickData.setMaxJoystickValue(joystickDrawer.getMaxJoystickValue());

        if (LumiRobotFinder.getInstance().getmLumiRobotConnectedList().size() > 0) {
            LumiRobot robot = (LumiRobot) LumiRobotFinder.getInstance().getmLumiRobotConnectedList().get(0);
            robot.setCallbackInterface(this);
        }
        Button btn = (Button)view.findViewById(R.id.back_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentHelper.switchFragment(getActivity().getSupportFragmentManager(), new MenuFragment(), R.id.view_id_content, false);
            }
        });

        takeOffBtn = (Button)view.findViewById(R.id.takeoff_btn);
        takeOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LumiRobotFinder.getInstance().getmLumiRobotConnectedList().size() > 0) {
                    if (takeOffBtn.getText().equals("Take Off")) {
                        LumiRobot robot = (LumiRobot) LumiRobotFinder.getInstance().getmLumiRobotConnectedList().get(0);
                        robot.setLEDRGB(LumiCommandValues.kLumiColor.White);
                        robot.lumiLandOrTakeOff(false);
                        lumiYAW = 0;
                        disableAllCommand = true;
                        stuntZ = 60;
                    }
                    else if (takeOffBtn.getText().equals("Land")) {
                        LumiRobot robot = (LumiRobot) LumiRobotFinder.getInstance().getmLumiRobotConnectedList().get(0);
                        robot.lumiLandOrTakeOff(true);
                        disableAllCommand = false;
                    }
                }
            }
        });

        touchArea.setOnTouchListener(this);

        getStatusTime = 9;
//        handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                cycle();
//            }
//        }, 100);

        Button forceLandBtn = (Button)view.findViewById(R.id.forceland_btn);
        forceLandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LumiRobotFinder.getInstance().getmLumiRobotConnectedList().size() > 0) {
                    LumiRobot robot = (LumiRobot) LumiRobotFinder.getInstance().getmLumiRobotConnectedList().get(0);
                    robot.lumiForceLand();
                }
            }
        });

        Button upBtn = (Button)view.findViewById(R.id.up_btn);
        upBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LumiRobotFinder.getInstance().getmLumiRobotConnectedList().size() > 0)
                {
                    int increment = 5;
                    stuntZ += increment;
                    int maxHeight = 130;
                    if (stuntZ > maxHeight) {
                        stuntZ = maxHeight;
                    }
                }
            }
        });

        Button downBtn = (Button)view.findViewById(R.id.down_btn);
        downBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int increment = 5;
                stuntZ -= increment;
                int minHeight = 60;
                if (stuntZ < minHeight) {
                    stuntZ = minHeight;
                }
            }
        });

        Button leftBtn = (Button)view.findViewById(R.id.left_btn);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lumiYAW = 2000;
            }
        });

        Button rightBtn = (Button)view.findViewById(R.id.right_btn);
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lumiYAW = -2000;
            }
        });

        if (LumiRobotFinder.getInstance().getmLumiRobotConnectedList().size() > 0) {
            LumiRobot robot = (LumiRobot) LumiRobotFinder.getInstance().getmLumiRobotConnectedList().get(0);
            robot.lumiSetWallDetectionMode(true);
            robot.lumiDeactivateFollowMeMode();
            robot.lumiSetBeaconMode(false);
            robot.lumiSetAltitudeMode(true);
        }
        return view;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        Canvas canvas = touchArea.getHolder().lockCanvas();

        if (v == touchArea)
        {
            int pointerIndex = event.getActionIndex();
            if(canvas != null) {
                canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            }

            switch(event.getActionMasked())
            {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                {
                    //set start point
                    JoystickData.TYPE joystickType = JoystickData.TYPE.LEFT;

                    JoystickData joystickData = this.joystickData;
                    if (joystickData.pointerId == JoystickData.INVALID_POINTER_ID)
                    {
                        joystickData.pointerId = event.getPointerId(pointerIndex);
                        joystickData.setStartPoint((int)event.getX(pointerIndex), (int)event.getY(pointerIndex));
                    }

                }
                break;
                case MotionEvent.ACTION_MOVE:
                {
                    //update the drag points
                    for (int i=0; i<event.getPointerCount(); i++)
                    {
                        JoystickData joystickData = null;

                        if(event.getPointerId(i) == this.joystickData.pointerId) {
                            joystickData = this.joystickData;
                        }
                        if(joystickData == null) {
                            continue;
                        }
                        joystickData.setDragPoint((int)event.getX(i), (int)event.getY(i));

                        float[] moveVector = joystickData.getMoveVector();

                        moveMip = true;

                        movementVector[0] = moveVector[0];
                        movementVector[1] = moveVector[1] * -1;
                        Log.i("location",""+String.valueOf(movementVector[0])+","+String.valueOf(movementVector[1]));
                    }
                }
                break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                case MotionEvent.ACTION_CANCEL:
                {
                    //cancel the touch
                    JoystickData joystickData = this.joystickData;
                    movementVector[0] = 0;
                    movementVector[1] = 0;
                    joystickData.reset();
                }
                break;
            }
        }

        if (canvas != null) {
            if (joystickDrawer != null) {
                joystickDrawer.drawJoystick(canvas, joystickData);
            }
        }
        touchArea.getHolder().unlockCanvasAndPost(canvas);

        return true;
    }

    public class JoystickTimerTask extends TimerTask {
        public void run() {
            cycle();
        }
    }

    void cycle() {
        if (LumiRobotFinder.getInstance().getmLumiRobotConnectedList().size() > 0) {
            LumiRobot robot = (LumiRobot) LumiRobotFinder.getInstance().getmLumiRobotConnectedList().get(0);
            if (getStatusTime++ >= 5) {
                robot.lumiGetQuadcopterStatus();
                getStatusTime = 0;
            }

            stuntX = (int)(movementVector[0]);
            stuntY = (int)(movementVector[1]);
            int pitch  = stuntY;
            int roll  = stuntX;
            float[] vec = {roll, pitch, 0};
            if (!disableAllCommand)
                robot.lumiFreeflightCommands(vec, 0, lumiYAW);

            lumiYAW = 0;
            if (!disableAllCommand)
                robot.lumiSetPosition(0, 0, stuntZ);

//            if (handler != null) {
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        cycle();
//                    }
//                }, 100);
//            }
        }
    }

    @Override
    public void lumiDidReceiveQuadcopterStatus(LumiRobot lumiRobot, int i) {
        if (i == 0 || i == 2) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    takeOffBtn.setText("Take Off");
                }
            });
        }
        else if (i == 1) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    takeOffBtn.setText("Land");
                }
            });
        }
    }

    @Override
    public void lumiDidNotifyFirstSonar(LumiRobot lumiRobot) {
        if (LumiRobotFinder.getInstance().getmLumiRobotConnectedList().size() > 0) {
            LumiRobot robot = (LumiRobot) LumiRobotFinder.getInstance().getmLumiRobotConnectedList().get(0);
            robot.setLEDRGB(LumiCommandValues.kLumiColor.Purple);
            robot.lumiSetPosition(0, 0, stuntZ);
            disableAllCommand = false;
        }
    }

    @Override
    public void lumiDidReceiveNotifyError(LumiRobot lumiRobot, LumiRobotConstant.kLumiNotifyError error) {
        String errorString = "";
        if (error == LumiRobotConstant.kLumiNotifyError.kLumiCrash)
            errorString = "kLumiCrash";
        else if (error == LumiRobotConstant.kLumiNotifyError.kLumiStall)
            errorString = "kLumiStall";
        else if (error == LumiRobotConstant.kLumiNotifyError.kLumiBeaconTimeout)
            errorString = "kLumiBeaconTimeout";
        else if (error == LumiRobotConstant.kLumiNotifyError.kLumiBLETimeout)
            errorString = "kLumiBLETimeout";
        else if (error == LumiRobotConstant.kLumiNotifyError.kLumiSonarTimeout)
            errorString = "kLumiSonarTimeout";
        else if (error == LumiRobotConstant.kLumiNotifyError.kLumiSonarStep)
            errorString = "kLumiSonarStep";
        else if (error == LumiRobotConstant.kLumiNotifyError.kLumiTakeoffOnBadFloor)
            errorString = "kLumiTakeoffOnBadFloor";
        final String str = errorString;
        handler.post(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Error");
                builder.setMessage(str);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
            }
        });
    }
}
