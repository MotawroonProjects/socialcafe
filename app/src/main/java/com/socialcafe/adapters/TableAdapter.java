package com.socialcafe.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.socialcafe.R;
import com.socialcafe.activities_fragments.activity_cart.CartActivity;
import com.socialcafe.databinding.TableRowBinding;
import com.socialcafe.models.TableModel;

import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TableModel> list;
    private Context context;
    private LayoutInflater inflater;
    private int i = -1;

    //private Fragment_Main fragment_main;
    public TableAdapter(List<TableModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        //  this.fragment_main=fragment_main;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        TableRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.table_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;

        myHolder.binding.setModel(list.get(position));

        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = holder.getLayoutPosition();
                notifyDataSetChanged();

            }
        });
        if (i == position) {
            myHolder.binding.ll.setBackground(context.getResources().getDrawable(R.drawable.rounded_stroke_gray));
            if (context instanceof CartActivity) {
                CartActivity cartActivity = (CartActivity) context;
                cartActivity.choose(list.get(position).getId());
            }

        } else {
            myHolder.binding.ll.setBackground(null);

        }
//Log.e("eeee",list.get(position).getOffer_value()+""+(list.get(position).getAmount()%list.get(position).getOffer_min()));
        // Log.e("ssss",((list.get(position).getHave_offer().equals("yes")?(list.get(position).getOffer_type().equals("per")?(list.get(position).getProduct_default_price().getPrice()-((list.get(position).getProduct_default_price().getPrice()*list.get(position).getOffer_value())/100)):list.get(position).getProduct_default_price().getPrice()-list.get(position).getOffer_value()):list.get(position).getProduct_default_price().getPrice())+""));


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public TableRowBinding binding;

        public MyHolder(@NonNull TableRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
