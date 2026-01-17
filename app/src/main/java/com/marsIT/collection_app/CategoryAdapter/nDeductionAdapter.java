package com.marsIT.collection_app.CategoryAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.marsIT.collection_app.R;
import java.util.ArrayList;

public class nDeductionAdapter extends RecyclerView.Adapter<nDeductionAdapter.ViewHolder> {

    private final ArrayList<nCategoryItem> deductionList;

    public nDeductionAdapter(ArrayList<nCategoryItem> deductionList) {
        this.deductionList = deductionList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_deduction_n, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        nCategoryItem item = deductionList.get(position);

        holder.category.setText(item.getCategory());

        try {
            double amt = Double.parseDouble(item.getAmount());
            java.text.DecimalFormat df = new java.text.DecimalFormat("#,###.00");
            holder.amount.setText(df.format(amt));
        } catch (Exception e) {
            holder.amount.setText(item.getAmount());
        }

        // CheckBox removed
    }

    @Override
    public int getItemCount() {
        return deductionList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView category, amount;

        ViewHolder(View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.category);
            amount = itemView.findViewById(R.id.deductionAmount);
        }
    }
}
