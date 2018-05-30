package dk.picit.picmobilear;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ButtonFragment extends Fragment {

    private View view;
    private Button buttonTakePicture, buttonReselectEqId;
    private BroadcastReceiver receiver;
    private MainActivity activity;
    private RecyclerView rvwContainerInformation, rvwCheckList, rvwEqIdList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_button, container, false);
        buttonTakePicture = (Button) view.findViewById(R.id.buttonTakePicture);
        buttonTakePicture.setOnClickListener(takePictureClickListener);

        buttonReselectEqId = (Button) view.findViewById(R.id.buttonReselectEqId);
        buttonReselectEqId.setOnClickListener(reselectEqId);

        activity = (MainActivity) getActivity();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                buttonReselectEqId.setVisibility(View.VISIBLE);
            }
        };
        return view;
    }

    private View.OnClickListener takePictureClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            activity.takeScreenshot(view);
            activity.takePicture(true);
        }
    };

    private View.OnClickListener reselectEqId = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            activity.takeScreenshot(view);
            toggleVisibilityForLists(false);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getContext().registerReceiver(receiver, new IntentFilter("showEqidButton"));

        View fragmentView =
                getActivity().getSupportFragmentManager().findFragmentById(R.id.rightfragment)
                             .getView();
        rvwContainerInformation = fragmentView.findViewById(R.id.RvwContainerInformation);
        rvwCheckList = fragmentView.findViewById(R.id.RvwChecklist);
        rvwEqIdList = fragmentView.findViewById(R.id.RvwEqIdList);
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(receiver);
    }

    /**
     * toggle list and button visibility
     * @param b
     */
    private void toggleVisibilityForLists(boolean b) {
        if (b) {
            buttonReselectEqId.setVisibility(View.GONE);
            rvwContainerInformation.setVisibility(View.VISIBLE);
            rvwCheckList.setVisibility(View.VISIBLE);
            rvwEqIdList.setVisibility(View.GONE);
        } else {
            buttonReselectEqId.setVisibility(View.VISIBLE);
            rvwContainerInformation.setVisibility(View.GONE);
            rvwCheckList.setVisibility(View.GONE);
            rvwEqIdList.setVisibility(View.VISIBLE);
        }
    }
}
