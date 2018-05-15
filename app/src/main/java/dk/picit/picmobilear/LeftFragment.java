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


    private View view;
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


        return view;
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



}
