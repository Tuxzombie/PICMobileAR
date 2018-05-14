package dk.picit.picmobilear;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.augumenta.agapi.AugumentaManager;
import com.augumenta.agapi.CameraFrameProvider;
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
    private ShowPose showPoseListener;
    private HandTransitionListener selectTransitionListener;

    private int count = 0;
    private Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraInfo = new android.hardware.Camera.CameraInfo();
        CameraManager manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        String camId = null;
        try {
            camId = manager.getCameraIdList()[0];
            CameraCharacteristics cc = manager.getCameraCharacteristics(camId);
            int cameraOrientation = cc.get(CameraCharacteristics.SENSOR_ORIENTATION);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_left, container, false);
        button = (Button) view.findViewById(R.id.button);
        button.setOnClickListener(buttonClickListener);
        showPoseListener = new ShowPose(view, getActivity());
        selectTransitionListener = showPoseListener.getSelectTransitionListner();

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

        // add listener for pose 229
        augumentaManager.registerListener(showPoseListener, Poses.P229);

        augumentaManager.registerListener(showPoseListener, Poses.P141);

        augumentaManager.registerListener(showPoseListener, Poses.P016);

        augumentaManager.registerListener(showPoseListener, Poses.P201);

        // add listener for transition from pose 229 to 141
        augumentaManager.registerListener(selectTransitionListener, Poses.P229, Poses.P141);

        augumentaManager.registerListener(backTransitionListener, Poses.P201, Poses.P016);

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
        updateDisplayOrientation();
    }

    private void requestCameraPermission() {
        // Request CAMERA permission from user
        Log.d(TAG, "Requesting CAMERA permission");
        ActivityCompat.requestPermissions(this.getActivity(), new String[] { Manifest.permission.CAMERA }, PERMISSION_REQUEST_CAMERA);
    }

    private HandTransitionListener backTransitionListener = new HandTransitionListener() {
        @Override
        public void onTransition(HandTransitionEvent handTransitionEvent) {
            // Close app
            getActivity().finish();
        }
    };


    private android.hardware.Camera.CameraInfo cameraInfo;

    private void updateDisplayOrientation() {
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        CameraFrameProvider cameraFrameProvider = ((CameraFrameProvider) augumentaManager.getFrameProvider());
        if(cameraFrameProvider!=null) {
            cameraFrameProvider.setDisplayOrientation(degrees);
            int result;
            android.hardware.Camera.getCameraInfo(cameraFrameProvider.getCameraId(), cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (cameraInfo.orientation + degrees) % 360;
                result = (360 - result) % 360;  // compensate the mirror
            } else {  // back-facing
                result = (cameraInfo.orientation - degrees + 360) % 360;
            }
            cameraFrameProvider.getCamera().setDisplayOrientation(result);
        }
    }

}
