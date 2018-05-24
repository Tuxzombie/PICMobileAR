package dk.picit.picmobilear.RecyclerViewAdapters;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class RecyclerAdapterEqId extends RecyclerView.Adapter<RecyclerAdapterEqId.ViewHolder> implements View.OnClickListener{

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ConstraintLayout constraintLayout;

        public ViewHolder(ConstraintLayout view) {
            super(view);
            this.constraintLayout = view;
        }
    }

    @NonNull
    @Override
    public RecyclerAdapterEqId.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterEqId.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public void onClick(View v) {

    }
}
