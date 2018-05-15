package dk.picit.picmobilear;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.augumenta.agapi.AugumentaManager;
import com.augumenta.agapi.CameraFrameProvider;
import com.augumenta.agapi.HandTransitionEvent;
import com.augumenta.agapi.HandTransitionListener;
import com.augumenta.agapi.Poses;

import dk.picit.picmobilear.handPose.ShowPose;

public class MainActivity extends AppCompatActivity {

    // Permission request code for CAMERA permission
    private static final int PERMISSION_REQUEST_CAMERA = 0;


    private CameraFrameProvider cameraFrameProvider;
    private AugumentaManager augumentaManager;
    private ShowPose showPoseListener;
    private HandTransitionListener selectTransitionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showPoseListener = new ShowPose(this);
        selectTransitionListener = showPoseListener.getSelectTransitionListner();

        cameraFrameProvider = new CameraFrameProvider();
        cameraFrameProvider.setCameraPreview(null);



        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String camId = null;
        int cameraOrientation = 0;
        try {
            camId = manager.getCameraIdList()[0];
            CameraCharacteristics cc = manager.getCameraCharacteristics(camId);
            cameraOrientation = cc.get(CameraCharacteristics.SENSOR_ORIENTATION);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        cameraFrameProvider.setDisplayOrientation(cameraOrientation);

        try{
            augumentaManager = AugumentaManager.getInstance(this, cameraFrameProvider);
        } catch (IllegalStateException e) {
            // Something went wrong while authenticating license
            Toast.makeText(this, "License error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
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


    private void startAugumentaManager(){
        if (!augumentaManager.start()) {
            Toast.makeText(this, "Failed to open camera!", Toast.LENGTH_LONG).show();
        }
//        updateDisplayOrientation();
    }

    private void requestCameraPermission() {
        // Request CAMERA permission from user
        ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA }, PERMISSION_REQUEST_CAMERA);
    }

    private HandTransitionListener backTransitionListener = new HandTransitionListener() {
        @Override
        public void onTransition(HandTransitionEvent handTransitionEvent) {
            // Close app
            finish();
        }
    };

    private void stopAugumentaManager()
    {

    }

    private void takePicture()
    {

    }
}
