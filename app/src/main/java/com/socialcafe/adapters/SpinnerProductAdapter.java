package com.socialcafe.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;


import com.socialcafe.R;
import com.socialcafe.databinding.SpinnerProductBinding;
import com.socialcafe.models.ProductModel;

import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class SpinnerProductAdapter extends BaseAdapter {
    private List<ProductModel> dataList;
    private Context context;
    private String lang;
    public SpinnerProductAdapter(List<ProductModel> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
        Paper.init(context);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        @SuppressLint("ViewHolder") SpinnerProductBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.spinner_product,viewGroup,false);
        binding.setLang(lang);
        binding.setModel(dataList.get(i));
        return binding.getRoot();
    }


}
