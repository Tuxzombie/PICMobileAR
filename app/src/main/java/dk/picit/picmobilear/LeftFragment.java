package dk.picit.picmobilear;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.augumenta.agapi.AugumentaManager;
import com.augumenta.agapi.CameraFrameProvider;
import com.augumenta.agapi.HandPoseEvent;
import com.augumenta.agapi.HandPoseListener;
import com.augumenta.agapi.HandTransitionEvent;
import com.augumenta.agapi.HandTransitionListener;
import com.augumenta.agapi.Poses;

import dk.picit.picmobilear.handPose.ShowPose;

public class LeftFragment extends Fragment {
    private static final String TAG = LeftFragment.class.getSimpleName();

    // Permission request code for CAMERA permission
    private static final int PERMISSION_REQUEST_CAMERA = 0;

    private View view;
    private CameraFrameProvider cameraFrameProvider;
    private AugumentaManager augumentaManager;
    private HandPoseListener showPoseListener;

    private int count = 0;
    private Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_left, container, false);
        button = (Button) view.findViewById(R.id.button);
        button.setOnClickListener(buttonClickListener);
        showPoseListener = new ShowPose(view, getActivity());

        cameraFrameProvider = new CameraFrameProvider();
        cameraFrameProvider.setCameraPreview(null);

        try{
            augumentaManager = AugumentaManager.getInstance(this.getContext(), cameraFrameProvider);
        } catch (IllegalStateException e) {
            // Something went wrong while authenticating license
            Toast.makeText(this.getContext(), "License error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "License error: " + e.getMessage());
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        augumentaManager.registerListener(showPoseListener, Poses.P001);

//        augumentaManager.registerListener(transitionListner, Poses.P001, Poses.P032);

        // Check if the Camera permission is already available
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted
            requestCameraPermission();
        } else {
            // Camera permission is already available
            // Start detection when activity is resumed
            startAugumentaManager();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        augumentaManager.unregisterAllListeners();
        augumentaManager.stop();
    }

    private void pushButton(){
        count++;
        TextView pushCount = view.findViewById(R.id.textViewCount);
        pushCount.setText("Push count: " + count);
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            pushButton();
        }
    };

    private void startAugumentaManager(){
        if (!augumentaManager.start()) {
            Toast.makeText(this.getContext(), "Failed to open camera!", Toast.LENGTH_LONG).show();
        }
    }

    private void requestCameraPermission() {
        // Request CAMERA permission from user
        Log.d(TAG, "Requesting CAMERA permission");
        ActivityCompat.requestPermissions(this.getActivity(), new String[] { Manifest.permission.CAMERA }, PERMISSION_REQUEST_CAMERA);
    }

//    private HandTransitionListener transitionListner = new HandTransitionListener() {
//        @Override
//        public void onTransition(HandTransitionEvent handTransitionEvent) {
//            Log.d(TAG, "onTransition: " + handTransitionEvent);
//            pushButton();
//        }
//    };

}
