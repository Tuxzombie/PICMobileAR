package dk.picit.picmobilear.RecyclerViewAdapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import dk.picit.picmobilear.R;

public class RecyclerAdapterEqId extends RecyclerView.Adapter<RecyclerAdapterEqId.ViewHolder>{

    private Context context;
    private Listener listener;
    private ArrayList<String> data;

    interface Listener {
        void onClick(int position);
    }

    public RecyclerAdapterEqId(Context context, ArrayList<String> data) {
        this.context = context;
        this.data = data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
        }
    }

    @Override
    public RecyclerAdapterEqId.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate( R.layout.adapter_eqid, parent, false);
        return new RecyclerAdapterEqId.ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onClick(holder.getAdapterPosition());
                }
            }
        });

        TextView eqId = (TextView) holder.cardView.findViewById(R.id.adapter_eqid_cardview);
        eqId.setText(data.get(holder.getAdapterPosition()));

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
}
