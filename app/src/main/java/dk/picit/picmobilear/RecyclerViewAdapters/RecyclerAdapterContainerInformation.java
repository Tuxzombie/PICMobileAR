package dk.picit.picmobilear.RecyclerViewAdapters;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import dk.picit.picmobilear.R;

import static android.content.ContentValues.TAG;

public class RecyclerAdapterContainerInformation extends RecyclerView.Adapter<RecyclerAdapterContainerInformation.ViewHolder> {

    private Map<String, String> dataset;
    private Iterator<String> keySetIterable;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ConstraintLayout constraintLayout;

        public ViewHolder(ConstraintLayout view) {
            super(view);
            this.constraintLayout = view;
        }
    }

    public RecyclerAdapterContainerInformation(Map<String, String> dataset) {
        super();
        this.dataset = dataset;
        Set<String> keySet = this.dataset.keySet();
        this.keySetIterable = keySet.iterator();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConstraintLayout layout = (ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_information, parent, false);
        ViewHolder viewHolder = new ViewHolder(layout);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView tvwKey = holder.constraintLayout.findViewById(R.id.tvwKey);
        TextView tvwValue = holder.constraintLayout.findViewById(R.id.tvwValue);
        String key = keySetIterable.next();
        String value = dataset.get(key);
        Log.d(TAG, "onBindViewHolder: " + key);
        tvwKey.setText(key + ":");
        tvwValue.setText(value);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

}
