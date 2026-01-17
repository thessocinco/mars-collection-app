package com.marsIT.collection_app.PaymentAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.marsIT.collection_app.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {

    private ArrayList<PaymentItem> list;

    public PaymentAdapter(ArrayList<PaymentItem> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView type, bank, chk, amount;

        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.pCheck);
            type = itemView.findViewById(R.id.textView1);
            bank = itemView.findViewById(R.id.textView2);
            chk = itemView.findViewById(R.id.textView3);
            amount = itemView.findViewById(R.id.textView5);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_payment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PaymentItem item = list.get(position);
        holder.checkBox.setChecked(item.isSelected());
        holder.type.setText(item.getPaymentType());
        holder.bank.setText(item.getBankInitial());
        holder.chk.setText(item.getChkNumber());
        holder.amount.setText(new DecimalFormat("#,###.00").format(
                Double.parseDouble(item.getAmount().replace(",", ""))
        ));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> item.setSelected(isChecked));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void deleteSelected() {
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).isSelected()) list.remove(i);
        }
        notifyDataSetChanged();
    }
}
