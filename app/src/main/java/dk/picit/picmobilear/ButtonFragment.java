package dk.picit.picmobilear;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ButtonFragment extends Fragment {
    private static final String TAG = ButtonFragment.class.getSimpleName();


    private View view;
    private int count = 0;
    private Button button;
    MainActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_button, container, false);
        button = (Button) view.findViewById(R.id.button);
        button.setOnClickListener(buttonClickListener);

        button.setOnClickListener(takePictureClickListener);
        activity = (MainActivity) getActivity();
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

    private View.OnClickListener takePictureClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            activity.takePicture();
        }
    };


}
