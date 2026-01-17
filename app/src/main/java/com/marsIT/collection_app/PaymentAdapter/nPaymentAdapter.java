package com.marsIT.collection_app.PaymentAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.marsIT.collection_app.R;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class nPaymentAdapter extends RecyclerView.Adapter<nPaymentAdapter.ViewHolder> {

    private final ArrayList<nPaymentItem> list;

    public nPaymentAdapter(ArrayList<nPaymentItem> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView type, bank, chk, amount;

        public ViewHolder(View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.textView1);
            bank = itemView.findViewById(R.id.textView2);
            chk = itemView.findViewById(R.id.textView3);
            amount = itemView.findViewById(R.id.textView5);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_payment_n, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        nPaymentItem item = list.get(position);

        holder.type.setText(item.getPaymentType());
        holder.bank.setText(item.getBankInitial());
        holder.chk.setText(item.getChkNumber());

        try {
            double amt = Double.parseDouble(item.getAmount().replace(",", ""));
            holder.amount.setText(new DecimalFormat("#,###.00").format(amt));
        } catch (Exception e) {
            holder.amount.setText(item.getAmount());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
