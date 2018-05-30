package dk.picit.picmobilear.RecyclerViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import dk.picit.picmobilear.MainActivity;
import dk.picit.picmobilear.R;

public class RecyclerAdapterEqId extends RecyclerView.Adapter<RecyclerAdapterEqId.ViewHolder> {

    private Context context;
    private List<String> data;

    public RecyclerAdapterEqId(Context context, List<String> data) {
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
                                               .inflate(R.layout.adapter_eqid, parent, false);
        return new RecyclerAdapterEqId.ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity a = (Activity) context;
                ((MainActivity) a).takeScreenshot(view);

                String ocrResult = data.get(holder.getAdapterPosition());
                // removes illegal characters from container number.
                ocrResult = ocrResult.replaceAll(" ", "").replaceAll("\\?", "").replaceAll("-", "");
                Intent in = new Intent("OCR");
                in.putExtra("ocrResult", ocrResult);
                // broadcast to lookup new container number
                context.sendBroadcast(in);
            }
        });

        TextView eqId = (TextView) holder.cardView.findViewById(R.id.textView_eqid_id);
        eqId.setText(data.get(holder.getAdapterPosition()));

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
