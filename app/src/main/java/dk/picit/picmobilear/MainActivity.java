package dk.picit.picmobilear;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Size;
import android.view.Surface;
import android.view.View;
import android.widget.Toast;

import com.augumenta.agapi.AugumentaManager;
import com.augumenta.agapi.CameraFrameProvider;
import com.augumenta.agapi.HandTransitionEvent;
import com.augumenta.agapi.HandTransitionListener;
import com.augumenta.agapi.Poses;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import dk.picit.picmobilear.handPose.ShowPose;
import dk.picit.picmobilear.service.VisionService;

public class MainActivity extends AppCompatActivity {

    // Permission request code for CAMERA permission
    private static final int PERMISSION_REQUEST_CAMERA = 0;


    private CameraFrameProvider cameraFrameProvider;
    private AugumentaManager augumentaManager;
    private ShowPose showPoseListener;
    private HandTransitionListener backTransitionListener;
    private HandTransitionListener selectTransitionListener;
    private CameraDevice mCamera = null;
    private CameraCaptureSession mSession = null;
    private Surface previewSurface = null;
    private SurfaceTexture mPreviewSurfaceTexture = null;
    private Surface jpegCaptureSurface = null;
    private String encodedImage;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        backTransitionListener = new HandTransitionListener() {
            @Override
            public void onTransition(HandTransitionEvent handTransitionEvent) {
                // Close app
                finish();
            }
        };
        showPoseListener = new ShowPose(this);
        selectTransitionListener = showPoseListener.getSelectTransitionListner();

        cameraFrameProvider = new CameraFrameProvider();
        cameraFrameProvider.setCameraPreview(null);

        //Get camera hardware orientation and correct the display orentation,
        // this is to correct fixed hardware angels
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

        //Instantiate augumenta manager with licence key
        try {
            augumentaManager = AugumentaManager.getInstance(this, cameraFrameProvider);
        } catch (IllegalStateException e) {
            // Something went wrong while authenticating license
            Toast.makeText(this, "License error: " + e.getMessage(), Toast.LENGTH_LONG)
                 .show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //Register broadcast receiver to look for intent named OCR
        registerReceiver(receiver, new IntentFilter("OCR"));

        // Check if the Camera permission is already available
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
            PackageManager.PERMISSION_GRANTED) {
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
        stopAugumentaManager();
        //unregisterReceiver(receiver);
    }

    private void startAugumentaManager() {
        cameraFrameProvider.start();
        // add listener for used hand poses
        augumentaManager.registerListener(showPoseListener, Poses.P229);
        augumentaManager.registerListener(showPoseListener, Poses.P141);
        augumentaManager.registerListener(showPoseListener, Poses.P016);
        augumentaManager.registerListener(showPoseListener, Poses.P201);

        // add listener for transition from pose 229 to 141
        augumentaManager.registerListener(selectTransitionListener, Poses.P229, Poses.P141);
        // add listener for transition from pose 201 to 016
        augumentaManager.registerListener(backTransitionListener, Poses.P201, Poses.P016);

        //check if camera is available by starting augumenta manager
        if (!augumentaManager.start()) {
            Toast.makeText(this, "Failed to open camera!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Request user for permission to use camera
     */
    private void requestCameraPermission() {
        // Request CAMERA permission from user
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                                          PERMISSION_REQUEST_CAMERA);
    }

    /**
     * Unregister all hand pose listeners, stop camera and manager
     */
    private void stopAugumentaManager() {
        augumentaManager.unregisterAllListeners();
        cameraFrameProvider.stop();
        augumentaManager.stop();
    }

    /**
     * Take Picture with camera
     * @param sendToVision send picture to vision
     */
    public void takePicture(final boolean sendToVision) {
        //Change last boolean to save picture to SD card
        takePicture(sendToVision, false);
    }

    /**
     * Take picture with camera
     * @param sendToVision send picture to vision
     * @param savePicture save picture on SD card
     */
    private void takePicture(final boolean sendToVision, final boolean savePicture) {
        stopAugumentaManager();
        final CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            //Selects camera 0 in id list, there is only one camera in the AR hardware,
            // Use CameraCharacteristics.LENS_FACING for other hardware
            String camId = manager.getCameraIdList()[0];
            CameraCharacteristics cc = manager.getCameraCharacteristics(camId);
            //fetches the surface configuration data specific to the camera,
            // so the surfaces can be configure correctly
            StreamConfigurationMap streamConfigs =
                    cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            //fetches the jpeg sizes available for the camera
            Size[] jpegSizes = streamConfigs.getOutputSizes(ImageFormat.JPEG);

            //find max resulution width
            int currentMax = jpegSizes[0].getWidth();
            int maxPlacement = 0;
            for (int i = 0; i < jpegSizes.length; i++) {
                if (currentMax < jpegSizes[i].getWidth()) {
                    currentMax = jpegSizes[i].getWidth();
                    maxPlacement = i;
                }
            }

            ImageReader jpegImageReader = ImageReader
                    .newInstance(jpegSizes[maxPlacement].getWidth(),
                                 jpegSizes[maxPlacement].getHeight(), ImageFormat.JPEG,
                                 1);
            //when the camera takes an image, and sends it to this surface,
            // this listenser begins the vision service process
            jpegImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = reader.acquireNextImage();
                    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    FileOutputStream outImage = null;

                    if(savePicture)
                    {
                        try {
                            Date now = new Date();
                            android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss",
                                                                  now);
                            File file =
                                    new File(Environment.getExternalStorageDirectory() +
                                             "/DCIM/Camera",
                                             "pic" + now + ".jpg");
                            outImage = new FileOutputStream(file);
                            outImage.write(bytes);
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                                     Uri.fromFile(file)));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    //converts the jpeg image into a base64 string for google vision
                    encodedImage = Base64.encodeToString(bytes, Base64.DEFAULT);

                    //checks if there is an internet connection available
                    // before creating the vision service
                    ConnectivityManager connectivityManager =
                            (ConnectivityManager) MainActivity.this
                                    .getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo == null || !activeNetworkInfo.isConnectedOrConnecting()) {
                        Toast toast = Toast.makeText(MainActivity.this,
                                                     "No internet connection",
                                                     Toast.LENGTH_LONG);
                        toast.show();
                    } else if (sendToVision) {
                        VisionService visionService = new VisionService(getApplicationContext());
                        visionService.execute(encodedImage);
                    }

                }
            }, null);

            mPreviewSurfaceTexture = new SurfaceTexture(1);
            previewSurface = new Surface(mPreviewSurfaceTexture);
            jpegCaptureSurface = jpegImageReader.getSurface();

            if (ActivityCompat
                        .checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
                requestCameraPermission();
                return;
            }
            //Opens the camera
            manager.openCamera(camId, new CameraDevice.StateCallback() {
                //when opened ready the surfaces
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    mCamera = camera;
                    List<Surface> surfaces = Arrays.asList(previewSurface, jpegCaptureSurface);
                    try {
                        //Creates session that runs camera to a non visible surface for 30 frames,
                        // to auto adjust white balance, color and focus, then takes a picture
                        CameraCaptureSession.StateCallback session =
                                new CameraCaptureSession.StateCallback() {
                                    @Override
                                    public void onConfigured(CameraCaptureSession session) {
                                        mSession = session;
                                        CaptureRequest.Builder request = null;
                                        try {
                                            request = mCamera.createCaptureRequest(
                                                    CameraDevice.TEMPLATE_PREVIEW);
                                            request.set(CaptureRequest.CONTROL_MODE,
                                                        CaptureRequest.CONTROL_MODE_AUTO);
                                            request.addTarget(previewSurface);
                                            CameraCaptureSession.CaptureCallback ccFocus =
                                                    new CameraCaptureSession.CaptureCallback() {
                                                        @Override
                                                        public void onCaptureCompleted(
                                                                CameraCaptureSession session,
                                                                CaptureRequest request,
                                                                TotalCaptureResult result) {
                                                            if (result.getFrameNumber() > 30) {
                                                                try {
                                                                    mSession.abortCaptures();
                                                                } catch (CameraAccessException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        }
                                                    };
                                            mSession.setRepeatingRequest(request.build(), ccFocus,
                                                                         null);
                                            request = mCamera.createCaptureRequest(
                                                    CameraDevice.TEMPLATE_STILL_CAPTURE);
                                            request.addTarget(jpegCaptureSurface);
                                            //take a picture, and if the capture is successful,
                                            // then close camera to
                                            // release it, and start augumenta
                                            CameraCaptureSession.CaptureCallback ccTakePicture =
                                                    new CameraCaptureSession.CaptureCallback() {
                                                        @Override
                                                        public void onCaptureCompleted(
                                                                CameraCaptureSession session,
                                                                CaptureRequest request,
                                                                TotalCaptureResult result) {
                                                            super.onCaptureCompleted(session,
                                                                                     request,
                                                                                     result);
                                                            mCamera.close();
                                                            startAugumentaManager();
                                                        }
                                                    };
                                            mSession.capture(request.build(), ccTakePicture,
                                                             null);
                                        } catch (CameraAccessException e) {
                                            //if something goes wrong with the capture,
                                            // then close camera to release it, and start augumenta
                                            e.printStackTrace();
                                            mCamera.close();
                                            startAugumentaManager();
                                        }
                                    }

                                    @Override
                                    public void onConfigureFailed(
                                            @NonNull CameraCaptureSession session) {

                                    }
                                };
                        mCamera.createCaptureSession(surfaces, session, null);
                    } catch (CameraAccessException e) {
                        //if accessing the camera fails , then close camera to release it,
                        // and start augumenta
                        e.printStackTrace();
                        mCamera.close();
                        startAugumentaManager();
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
    }

    // remove, and remove int i onthe next function, to take screenshots.
    public void takeScreenshot(View view) {}

    /**
     * Takes screenshots
     * @param view  from listener
     * @param i     remove to take screenshots
     */
    public void takeScreenshot(View view, int i) {
        View rootView = view.getRootView();
        rootView.setDrawingCacheEnabled(true);
        // take screenshot
        Bitmap bitmap = Bitmap.createBitmap(rootView.getDrawingCache());
        rootView.setDrawingCacheEnabled(false);
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        String path = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera/" + now +
                      ".jpeg";
        File imageFile = new File(path);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
            // save file as jpeg
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            //update media scanner
            sendBroadcast(
                    new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        takePicture(false);
    }
}
