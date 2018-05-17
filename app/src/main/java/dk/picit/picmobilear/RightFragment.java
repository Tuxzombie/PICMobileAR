package dk.picit.picmobilear;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

import dk.picit.picmobilear.RecyclerViewAdapters.RecyclerAdapterContainerInformation;


public class RightFragment extends Fragment {

    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_right, container, false);

        recyclerView = (RecyclerView)  view.findViewById(R.id.RvwContainerInformation);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        HashMap<String, String> map = new HashMap<>();
        map.put("Navn", "Kasper");
        map.put("Efternavn", "Knudsen");
        map.put("Noget", "Andet");

        RecyclerAdapterContainerInformation recyclerAdapterContainerInformation = new RecyclerAdapterContainerInformation(map);
        recyclerView.setAdapter(recyclerAdapterContainerInformation);

        // Inflate the layout for this fragment
        return view;
    }
}



