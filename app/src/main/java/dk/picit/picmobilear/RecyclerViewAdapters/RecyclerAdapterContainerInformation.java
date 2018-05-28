package dk.picit.picmobilear.RecyclerViewAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import dk.picit.picmobilear.R;

import static android.content.ContentValues.TAG;

public class RecyclerAdapterContainerInformation extends RecyclerView.Adapter<RecyclerAdapterContainerInformation.ViewHolder> {

    private Map<String, String> dataset;
    private Iterator<String> keySetIterable;
    private Context context;
    private boolean isCollapsed;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ConstraintLayout constraintLayout;

        public ViewHolder(ConstraintLayout view) {
            super(view);
            this.constraintLayout = view;
        }
    }

    public RecyclerAdapterContainerInformation(Context context, Map<String, String> dataset) {
        super();
        this.dataset = dataset;
        this.context = context;
        isCollapsed = true;
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        TextView tvwKey = holder.constraintLayout.findViewById(R.id.tvwKey);
        TextView tvwValue = holder.constraintLayout.findViewById(R.id.tvwValue);
        if(keySetIterable.hasNext()) {
            String key = keySetIterable.next();
            String value = dataset.get(key);
            Log.d(TAG, "onBindViewHolder: " + key);
            if (key.equalsIgnoreCase("EquipmentID")) {
                tvwKey.setText(key + ":");
                tvwValue.setText(value);
                tvwKey.setVisibility(View.VISIBLE);
                tvwValue.setVisibility(View.VISIBLE);
            } else if (!isCollapsed) {
                tvwKey.setText(key + ":");
                tvwValue.setText(value);
                tvwKey.setVisibility(View.VISIBLE);
                tvwValue.setVisibility(View.VISIBLE);
            } else {
                    tvwKey.setVisibility(View.GONE);
                    tvwValue.setVisibility(View.GONE);
            }
                holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        setCollapsed(!isCollapsed);

                    }
                });
        }
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void setCollapsed(boolean b)
    {
        this.isCollapsed = b;
        Set<String> keySet = this.dataset.keySet();
        this.keySetIterable = keySet.iterator();
        notifyDataSetChanged();
    }

}
