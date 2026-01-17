package com.marsIT.collection_app.InvoiceAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.marsIT.collection_app.R;
import java.util.ArrayList;

public class nInvoiceAdapter extends RecyclerView.Adapter<nInvoiceAdapter.InvoiceViewHolder> {

    /** Interface for row click */
    public interface OnItemClickListener {
        void onItemClick(nInvoiceItem item);
    }

    private final ArrayList<nInvoiceItem> invoiceList;
    private final OnItemClickListener listener;

    public nInvoiceAdapter(ArrayList<nInvoiceItem> invoiceList, OnItemClickListener listener) {
        this.invoiceList = invoiceList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public InvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_invoice_n, parent, false);
        return new InvoiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceViewHolder holder, int position) {
        nInvoiceItem item = invoiceList.get(position);

        holder.rvInvoiceNo.setText(item.getInvoiceNo());

        try {
            double amt = Double.parseDouble(item.getAmount());
            java.text.DecimalFormat df = new java.text.DecimalFormat("#,###.00");
            holder.rvAmount.setText(df.format(amt));
        } catch (Exception e) {
            holder.rvAmount.setText(item.getAmount());
        }

        holder.rvDepartment.setText(item.getDepartment());

        // Row click listener
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return invoiceList.size();
    }

    public static class InvoiceViewHolder extends RecyclerView.ViewHolder {
        TextView rvInvoiceNo, rvAmount, rvDepartment;

        public InvoiceViewHolder(@NonNull View itemView) {
            super(itemView);
            rvInvoiceNo = itemView.findViewById(R.id.rInvoiceNo);
            rvAmount = itemView.findViewById(R.id.rAmount);
            rvDepartment = itemView.findViewById(R.id.rDepartment);
        }
    }
}
