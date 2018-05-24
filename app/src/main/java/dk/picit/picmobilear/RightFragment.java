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

import dk.picit.picmobilear.RecyclerViewAdapters.RecyclerAdapterCheckList;
import dk.picit.picmobilear.RecyclerViewAdapters.RecyclerAdapterContainerInformation;
import dk.picit.picmobilear.service.CheckListService;

import static android.content.ContentValues.TAG;


public class RightFragment extends Fragment {

    private RecyclerView rvwInformation;
    private RecyclerView rvwCheckList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_right, container, false);

        rvwInformation = (RecyclerView) view.findViewById(R.id.RvwContainerInformation);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext()){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        rvwInformation.setLayoutManager(layoutManager);


        rvwCheckList = (RecyclerView) view.findViewById(R.id.RvwChecklist);
        layoutManager = new LinearLayoutManager(getContext());
        rvwCheckList.setLayoutManager(layoutManager);

        final CheckListService checkListService = new CheckListService(getContext());

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                RecyclerAdapterContainerInformation recyclerAdapterContainerInformation = new RecyclerAdapterContainerInformation(checkListService.getInformation());
                rvwInformation.setAdapter(recyclerAdapterContainerInformation);

                Log.d(TAG, "onReceive: " + checkListService.getInformation().toString());

                RecyclerAdapterCheckList recyclerAdapterCheckList = new RecyclerAdapterCheckList(checkListService.getService());
                rvwCheckList.setAdapter(recyclerAdapterCheckList);
            }
        };

        getContext().registerReceiver(receiver, new IntentFilter("CheckListReady"));

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!intent.getStringExtra("ocrResult").equals("No Text Found")) {
                    checkListService.setUsername("kGHikLiikljcnknd");
                    checkListService.setPassword("RbiubbLchRkbQaih");
                    checkListService.setContainerNr(intent.getStringExtra("ocrResult"));

                    ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo == null || !activeNetworkInfo.isConnectedOrConnecting()) {
                        Toast toast = Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_LONG);
                        toast.show();
                    } else {
                        checkListService.sendRequest();
                    }
                }
                Toast.makeText(context, "Response Received :" + intent.getStringExtra("ocrResult"), Toast.LENGTH_LONG).show();
            }
        };

        getContext().registerReceiver(receiver, new IntentFilter("OCR"));


        return view;
    }
}



