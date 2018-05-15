package dk.picit.picmobilear;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.augumenta.agapi.AugumentaManager;
import com.augumenta.agapi.CameraFrameProvider;
import com.augumenta.agapi.HandTransitionEvent;
import com.augumenta.agapi.HandTransitionListener;
import com.augumenta.agapi.Poses;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import dk.picit.picmobilear.handPose.ShowPose;

public class MainActivity extends AppCompatActivity {

    // Permission request code for CAMERA permission
    private static final int PERMISSION_REQUEST_CAMERA = 0;


    private CameraFrameProvider cameraFrameProvider;
    private AugumentaManager augumentaManager;
    private ShowPose showPoseListener;
    private HandTransitionListener selectTransitionListener;
    private SurfaceTexture mPreviewSurfaceTexture = null;
    private CameraDevice mCamera = null;
    private CameraCaptureSession mSession = null;
    private Surface previewSurface = null;
    private Surface jpegCaptureSurface = null;

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

        try {
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


    private void startAugumentaManager() {
        if (!augumentaManager.start()) {
            Toast.makeText(this, "Failed to open camera!", Toast.LENGTH_LONG).show();
        }
//        updateDisplayOrientation();
    }

    private void requestCameraPermission() {
        // Request CAMERA permission from user
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
    }

    private HandTransitionListener backTransitionListener = new HandTransitionListener() {
        @Override
        public void onTransition(HandTransitionEvent handTransitionEvent) {
            // Close app
            finish();
        }
    };

    private void stopAugumentaManager() {

    }

    private void takePicture() {

                stopAugumentaManager();

                final CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                try {
                    String camId = manager.getCameraIdList()[0];
                    CameraCharacteristics cc = manager.getCameraCharacteristics(camId);
                    StreamConfigurationMap streamConfigs = cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    Size[] jpegSizes = streamConfigs.getOutputSizes(ImageFormat.JPEG);

                    ImageReader jpegImageReader = ImageReader.newInstance(jpegSizes[jpegSizes.length - 1].getWidth(), jpegSizes[jpegSizes.length - 1].getHeight(), ImageFormat.JPEG, 1);
                    jpegImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                        @Override
                        public void onImageAvailable(ImageReader reader) {
                            Image image = reader.acquireLatestImage();
                            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                            byte[] bytes = new byte[buffer.remaining()];
                            buffer.get(bytes);
                            FileOutputStream outImage = null;
                            try {
                                outImage = new FileOutputStream(File.createTempFile("pic"+System.currentTimeMillis(), ".jpg"));
                                outImage.write(bytes);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }, null);

                    jpegCaptureSurface = jpegImageReader.getSurface();

                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        //TODO: insert code for requesting permission if none avaliable
                        return;
                    }
                    //Opens the camera
                    manager.openCamera(camId, new CameraDevice.StateCallback() {
                        //when opened ready the surfaces
                        @Override
                        public void onOpened(@NonNull CameraDevice camera) {
                            mCamera = camera;
                            List<Surface> surfaces = Arrays.asList(jpegCaptureSurface);
                            try {
                                //Creates session witch keeps requesting a picture for the preview service
                                mCamera.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                                    @Override
                                    public void onConfigured(@NonNull CameraCaptureSession session) {
                                        mSession = session;
                                        CaptureRequest.Builder request = null;
                                        try {
                                            request = mCamera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                                            request.addTarget(jpegCaptureSurface);
                                            mSession.setRepeatingRequest(request.build(), new CameraCaptureSession.CaptureCallback() {
                                                @Override
                                                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                                                    mCamera.close();
                                                }
                                            }, null);
                                        } catch (CameraAccessException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                                    }
                                }, null);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onDisconnected(@NonNull CameraDevice camera) {

                        }

                        @Override
                        public void onError(@NonNull CameraDevice camera, int error) {

                        }
                    }, null);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }

                startAugumentaManager();
            }
}
