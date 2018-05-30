package dk.picit.picmobilear.RecyclerViewAdapters;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.List;

import dk.picit.picmobilear.R;

public class RecyclerAdapterCheckList extends
        RecyclerView.Adapter<RecyclerAdapterCheckList.ViewHolder> {

    private List<String> checkList;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ConstraintLayout constraintLayout;

        public ViewHolder(ConstraintLayout view) {
            super(view);
            constraintLayout = view;
        }
    }


    public RecyclerAdapterCheckList(List<String> checkList) {
        super();
        this.checkList = checkList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConstraintLayout layout = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                                                                   .inflate(
                                                                           R.layout.adapter_checklist,
                                                                           parent, false);

        ViewHolder viewHolder = new ViewHolder(layout);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CheckBox checkBox = (CheckBox) holder.constraintLayout.findViewById(R.id.cbCheckList);
        checkBox.setText(checkList.get(position));
    }

    @Override
    public int getItemCount() {
        return checkList.size();
    }
}
