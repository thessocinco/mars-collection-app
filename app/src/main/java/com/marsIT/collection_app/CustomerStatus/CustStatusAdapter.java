package com.marsIT.collection_app.CustomerStatus;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.marsIT.collection_app.R;

import java.util.List;

public class CustStatusAdapter extends RecyclerView.Adapter<CustStatusAdapter.ViewHolder> {

    private final Context context;
    private List<CustStatus> list;

    public CustStatusAdapter(Context context, List<CustStatus> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.customer_status_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CustStatus customer = list.get(position);
        holder.tvCustID.setText(customer.getCustomerID());
        holder.tvCustName.setText(customer.getCustomerName());

        // Set indicator color: green if collected, red if not
        int color = customer.isCollected() ? Color.parseColor("#2E7D32") // green
                : Color.parseColor("#C62828"); // red
        holder.statusIndicator.getBackground().setTint(color);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(List<CustStatus> filteredList) {
        this.list = filteredList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustID, tvCustName;
        View statusIndicator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustID = itemView.findViewById(R.id.cdCustID);
            tvCustName = itemView.findViewById(R.id.cdCustName);
            statusIndicator = itemView.findViewById(R.id.statusIndicator);
        }
    }
}
