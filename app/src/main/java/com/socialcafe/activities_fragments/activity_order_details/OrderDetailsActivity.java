package com.socialcafe.activities_fragments.activity_order_details;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.exoplayer2.C;
import com.socialcafe.R;
import com.socialcafe.adapters.ProductOrderAdapter;
import com.socialcafe.databinding.ActivityOrderDetailsBinding;
import com.socialcafe.models.CreateOrderModel;
import com.socialcafe.models.ItemCartModel;
import com.socialcafe.models.OrderModel;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailsActivity extends AppCompatActivity {
    private ActivityOrderDetailsBinding binding;
    private ProductOrderAdapter adapter;
    private List<OrderModel.Detials> productList;
    private OrderModel orderModel;
    private CreateOrderModel createOrderModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_details);
        getDataFromIntent();
        initView();

    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        orderModel = (OrderModel) intent.getSerializableExtra("data");
    }

    private void initView() {
        createOrderModel = new CreateOrderModel();
        binding.setModel(orderModel);
        productList = new ArrayList<>();
        productList.addAll(orderModel.getDetails());
        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductOrderAdapter(productList, this);
        binding.recView.setAdapter(adapter);
        setData();
    }

    private void setData() {
        createOrderModel.setBiller_id(orderModel.getBiller_id());
        createOrderModel.setBiller_id_hidden(orderModel.getBiller_id());
        createOrderModel.setSale_id(orderModel.getId());
        createOrderModel.setSale_status(orderModel.getSale_status());
        createOrderModel.setCheque_no();
        List<ItemCartModel> itemCartModelList = new ArrayList<>();
        for (int i = 0; i < productList.size(); i++) {
            OrderModel.Detials detials = productList.get(i);
            ItemCartModel itemCartModel = new ItemCartModel();
            itemCartModel.setName(detials.getProduct().getName());
            itemCartModel.setNet_unit_price(Double.parseDouble(detials.getNet_unit_price()));
            itemCartModel.setProduct_id(Integer.parseInt(detials.getProduct_id()));
            itemCartModel.setProduct_batch_id("");
            itemCartModel.setProduct_code(detials.getProduct().getCode());
            itemCartModel.setDiscount(0);
            itemCartModel.setTax(Double.parseDouble(detials.getTax()));
            itemCartModel.setTax_rate(Double.parseDouble(detials.getTax_rate()));
            itemCartModel.setSubtotal(Double.parseDouble(detials.getTotal()));
            itemCartModel.setQty(Integer.parseInt(detials.getQty()));
         //   itemCartModel.setSale_unit(detials.getsa);

            itemCartModelList.add(itemCartModel);
        }
        createOrderModel.setDetails(itemCartModelList);
    }
}