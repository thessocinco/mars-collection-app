package com.marsIT.collection_app.InvoiceAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.marsIT.collection_app.R;
import java.util.ArrayList;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(InvoiceItem item);
    }

    private final ArrayList<InvoiceItem> invoiceList;
    private final OnItemClickListener listener;

    public InvoiceAdapter(ArrayList<InvoiceItem> invoiceList, OnItemClickListener listener) {
        this.invoiceList = invoiceList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public InvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_invoice, parent, false);
        return new InvoiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceViewHolder holder, int position) {
        InvoiceItem item = invoiceList.get(position);

        holder.rvInvoiceNo.setText(item.getInvoiceNo());
//        holder.rvAmount.setText(item.getAmount());
        try {
            double amt = Double.parseDouble(item.getAmount());
            java.text.DecimalFormat df = new java.text.DecimalFormat("#,###.00");
            holder.rvAmount.setText(df.format(amt));
        } catch (Exception e) {
            holder.rvAmount.setText(item.getAmount());
        }
        holder.rvDepartment.setText(item.getDepartment());

        // Checkbox selection for delete
        holder.cbSelect.setChecked(item.isSelected());
        holder.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> item.setSelected(isChecked));

        // Bind click listener
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return invoiceList.size();
    }

    /** DELETE selected items */
    public void deleteSelected() {
        for (int i = invoiceList.size() - 1; i >= 0; i--) {
            if (invoiceList.get(i).isSelected()) {
                invoiceList.remove(i);
            }
        }
        notifyDataSetChanged();
    }

    public static class InvoiceViewHolder extends RecyclerView.ViewHolder {
        TextView rvInvoiceNo, rvAmount, rvDepartment;
        CheckBox cbSelect;

        public InvoiceViewHolder(@NonNull View itemView) {
            super(itemView);
            rvInvoiceNo = itemView.findViewById(R.id.rInvoiceNo);
            rvAmount = itemView.findViewById(R.id.rAmount);
            rvDepartment = itemView.findViewById(R.id.rDepartment);
            cbSelect = itemView.findViewById(R.id.checkDelete);
        }
    }
}
