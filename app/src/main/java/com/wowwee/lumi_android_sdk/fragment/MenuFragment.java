package com.wowwee.lumi_android_sdk.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class MenuFragment extends Fragment {
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

        ListView listView = (ListView)view.findViewById(R.id.menuTable);
        String[] robotNameArr = {"Change LED color", "Setting Features", "Free Flight", "Stunt (Beacon Mode)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, robotNameArr);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                if (LumiRobotFinder.getInstance().getmLumiRobotConnectedList().size() > 0) {
                    LumiRobot robot = (LumiRobot)LumiRobotFinder.getInstance().getmLumiRobotConnectedList().get(0);
                    switch (position) {
                        case 0:
                            FragmentHelper.switchFragment(getActivity().getSupportFragmentManager(), new ChangeLEDFragment(), R.id.view_id_content, false);
                            break;
                        case 1:
                            FragmentHelper.switchFragment(getActivity().getSupportFragmentManager(), new SettingFragment(), R.id.view_id_content, false);
                            break;
                        case 2:
                            FragmentHelper.switchFragment(getActivity().getSupportFragmentManager(), new DriveFragment(), R.id.view_id_content, false);
                            break;
                        case 3:
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Stunt (Beacon Mode)");
                            builder.setMessage("Please turn on LUMI Pod to play this mode.");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FragmentHelper.switchFragment(getActivity().getSupportFragmentManager(), new StuntFragment(), R.id.view_id_content, false);
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
                    }
                }
            }
        });

        return view;
    }
}
