package dk.picit.picmobilear;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Rect;
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
import android.media.ImageReader;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

public class TopFragment extends Fragment {

    private SurfaceTexture mPreviewSurfaceTexture = null;
    private CameraDevice mCamera = null;
    private CameraCaptureSession mSession = null;
    private Surface previewSurface = null;
    private Surface rawCaptureSurface = null;
    private Surface jpegCaptureSurface = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_top, container, false);

       // onClickOCR(view);

        return view;
    }

    public void onClickOCR(View view) {


        //Lokal variabel af det view camerapreview skal tegnes på
        final TextureView camView = (TextureView) view.findViewById(R.id.textureView);

        //listner der ser på hvornår viewet er klar til at få billede på fra kameraet
        camView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, final int width, int height) {
                mPreviewSurfaceTexture = surface;
//                float scale = height/100f;
//                camView.setScaleY(scale);
                camView.setTranslationX(-((float)width/4));
                camView.setTranslationY(-20f);

                CameraManager manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
                try {
                    String camId = manager.getCameraIdList()[0];
                    CameraCharacteristics cc = manager.getCameraCharacteristics(camId);
                    StreamConfigurationMap streamConfigs = cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
//                    Size[] rawSizes = streamConfigs.getOutputSizes(ImageFormat.RAW_SENSOR);
                    Size[] jpegSizes = streamConfigs.getOutputSizes(ImageFormat.JPEG);

//                    ImageReader rawImageReader = ImageReader.newInstance(rawSizes[0].getWidth(), rawSizes[0].getHeight(), ImageFormat.RAW_SENSOR, 1);
//                    rawImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
//                        @Override
//                        public void onImageAvailable(ImageReader reader) {
//                            //
//                        }
//                    }, null);

                    ImageReader jpegImageReader = ImageReader.newInstance(jpegSizes[jpegSizes.length-1].getWidth(), jpegSizes[jpegSizes.length-1].getHeight(), ImageFormat.JPEG, 1);
                    jpegImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                        @Override
                        public void onImageAvailable(ImageReader reader) {
                            //gemme kode
                        }
                    }, null);

                    previewSurface = new Surface(mPreviewSurfaceTexture);
//                    rawCaptureSurface = rawImageReader.getSurface();
                    jpegCaptureSurface = jpegImageReader.getSurface();

                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    manager.openCamera(camId, new CameraDevice.StateCallback() {
                        @Override
                        public void onOpened(@NonNull CameraDevice camera) {
                            mCamera = camera;
                            List<Surface> surfaces = Arrays.asList(previewSurface, jpegCaptureSurface);
                            try {
                                mCamera.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                                    @Override
                                    public void onConfigured(@NonNull CameraCaptureSession session) {
                                        mSession = session;
                                        CaptureRequest.Builder request = null;
                                        try {
                                            request = mCamera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                                            request.addTarget(previewSurface);
                                              mSession.setRepeatingRequest(request.build(), new CameraCaptureSession.CaptureCallback() {
                                                @Override
                                                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                                                    // updated values can be found here
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
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                camView.setTranslationX(-((float)width/4));
                camView.setTranslationY(-20f);
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });

    }


}
