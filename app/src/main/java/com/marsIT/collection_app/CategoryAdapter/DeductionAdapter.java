package com.marsIT.collection_app.CategoryAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.marsIT.collection_app.R;
import java.util.ArrayList;

public class DeductionAdapter extends RecyclerView.Adapter<DeductionAdapter.ViewHolder> {

    private final ArrayList<CategoryItem> deductionList;

    public DeductionAdapter(ArrayList<CategoryItem> deductionList) {
        this.deductionList = deductionList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_deduction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CategoryItem item = deductionList.get(position);
        holder.category.setText(item.getCategory());
//        holder.amount.setText(item.getAmount());
        try {
            double amt = Double.parseDouble(item.getAmount());
            java.text.DecimalFormat df = new java.text.DecimalFormat("#,###.00");
            holder.amount.setText(df.format(amt));
        } catch (Exception e) {
            holder.amount.setText(item.getAmount());
        }
        holder.checkBox.setChecked(item.isSelected());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> item.setSelected(isChecked));
    }

    @Override
    public int getItemCount() {
        return deductionList.size();
    }

    public void deleteSelected() {
        for (int i = deductionList.size() - 1; i >= 0; i--) {
            if (deductionList.get(i).isSelected()) {
                deductionList.remove(i);
            }
        }
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView category, amount;
        CheckBox checkBox;

        ViewHolder(View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.category);
            amount = itemView.findViewById(R.id.deductionAmount);
            checkBox = itemView.findViewById(R.id.checkDeleteDeduction);
        }
    }
}
