package dk.picit.picmobilear;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Map;

import dk.picit.picmobilear.RecyclerViewAdapters.RecyclerAdapterCheckList;
import dk.picit.picmobilear.RecyclerViewAdapters.RecyclerAdapterContainerInformation;
import dk.picit.picmobilear.RecyclerViewAdapters.RecyclerAdapterEqId;
import dk.picit.picmobilear.service.CheckListService;

import static android.content.ContentValues.TAG;


public class RightFragment extends Fragment {

    private RecyclerView rvwInformation;
    private RecyclerView rvwCheckList;
    private RecyclerView rvwEqId;
    private BroadcastReceiver checkListReceiver, visionReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_right, container, false);

        // view with container information
        rvwInformation = (RecyclerView) view.findViewById(R.id.RvwContainerInformation);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        rvwInformation.setLayoutManager(layoutManager);

        // view with checklist
        rvwCheckList = (RecyclerView) view.findViewById(R.id.RvwChecklist);
        layoutManager = new LinearLayoutManager(getContext());
        rvwCheckList.setLayoutManager(layoutManager);

        // view with similar container numbers
        rvwEqId = (RecyclerView) view.findViewById(R.id.RvwEqIdList);
        rvwEqId.setLayoutManager(new LinearLayoutManager(getContext()));


        final CheckListService checkListService = new CheckListService(getContext());

        checkListReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // set adapter for container information
                Map<String, String> informationMap = checkListService.getInformation();
                RecyclerAdapterContainerInformation recyclerAdapterContainerInformation =
                        new RecyclerAdapterContainerInformation(context, informationMap);
                rvwInformation.setAdapter(recyclerAdapterContainerInformation);

                Log.d(TAG, "onReceive: " + checkListService.getInformation().toString());

                // set adapter for checklist
                RecyclerAdapterCheckList recyclerAdapterCheckList =
                        new RecyclerAdapterCheckList(checkListService.getService());
                rvwCheckList.setAdapter(recyclerAdapterCheckList);

                // if similar container numbers exist, make adapter for similar container numbers.
                String eqpList = informationMap.get("EqpList");
                if (eqpList != null) {
                    eqpList = eqpList.replaceAll("cont:", "");
                    String[] eqpArray = eqpList.split("\\|");

                    RecyclerAdapterEqId recyclerAdapterEqId =
                            new RecyclerAdapterEqId(getContext(), Arrays.asList(eqpArray));
                    rvwEqId.setAdapter(recyclerAdapterEqId);
                    context.sendBroadcast(new Intent("showEqidButton"));
                }

                View fragmentView = getActivity().getSupportFragmentManager()
                                                 .findFragmentById(R.id.rightfragment).getView();
                fragmentView.findViewById(R.id.RvwContainerInformation).setVisibility(View.VISIBLE);
                fragmentView.findViewById(R.id.RvwChecklist).setVisibility(View.VISIBLE);
                fragmentView.findViewById(R.id.RvwEqIdList).setVisibility(View.GONE);
            }
        };


        visionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!intent.getStringExtra("ocrResult").equals("No Text Found")) {
                    //set username, password and container number
                    checkListService.setUsername("kGHikLiikljcnknd");
                    checkListService.setPassword("RbiubbLchRkbQaih");
                    checkListService.setContainerNr(intent.getStringExtra("ocrResult"));

                    // check internet connection 
                    ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
                            .getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo == null || !activeNetworkInfo.isConnectedOrConnecting()) {
                        Toast toast = Toast.makeText(getContext(), "No internet connection",
                                                     Toast.LENGTH_LONG);
                        toast.show();
                    } else {
                        checkListService.sendRequest();
                    }
                }

            }
        };


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getContext().registerReceiver(checkListReceiver, new IntentFilter("CheckListReady"));
        getContext().registerReceiver(visionReceiver, new IntentFilter("OCR"));
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(checkListReceiver);
        getContext().unregisterReceiver(visionReceiver);
    }
}



