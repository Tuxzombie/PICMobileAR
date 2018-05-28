package dk.picit.picmobilear;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import dk.picit.picmobilear.RecyclerViewAdapters.RecyclerAdapterCheckList;
import dk.picit.picmobilear.RecyclerViewAdapters.RecyclerAdapterContainerInformation;

import static android.content.ContentValues.TAG;

public class ButtonFragment extends Fragment {
    private static final String TAG = ButtonFragment.class.getSimpleName();


    private View view;
    private int count = 0;
    private Button buttonTakePicture, buttonReselectEqId;
    private BroadcastReceiver receiver;
    private MainActivity activity;
    private RecyclerView rvwContainerInformaion, rvwCheckList, rvwEqIdList;

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

//            toggleVisiblityForLists(true);


        }
    };

    private View.OnClickListener reselectEqId = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            activity.takeScreenshot(view);

            toggleVisiblityForLists(false);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getContext().registerReceiver(receiver, new IntentFilter("CheckListReady"));

        View fragmentView = getActivity().getSupportFragmentManager().findFragmentById(R.id.rightfragment).getView();
        rvwContainerInformaion = fragmentView.findViewById(R.id.RvwContainerInformation);
        rvwCheckList = fragmentView.findViewById(R.id.RvwChecklist);
        rvwEqIdList = fragmentView.findViewById(R.id.RvwEqIdList);
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(receiver);
    }

    private void toggleVisiblityForLists(boolean b)
    {
        if(b) {
            buttonReselectEqId.setVisibility(View.GONE);
            rvwContainerInformaion.setVisibility(View.VISIBLE);
            rvwCheckList.setVisibility(View.VISIBLE);
            rvwEqIdList.setVisibility(View.GONE);
        }
        else
        {
            buttonReselectEqId.setVisibility(View.VISIBLE);
            rvwContainerInformaion.setVisibility(View.GONE);
            rvwCheckList.setVisibility(View.GONE);
            rvwEqIdList.setVisibility(View.VISIBLE);
        }
    }
}
