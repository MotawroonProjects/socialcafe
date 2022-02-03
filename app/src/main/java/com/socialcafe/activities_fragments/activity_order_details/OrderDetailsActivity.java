package com.socialcafe.activities_fragments.activity_order_details;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.exoplayer2.C;
import com.google.gson.Gson;
import com.socialcafe.R;
import com.socialcafe.activities_fragments.activity_currentorder.CurrentOrderActivity;
import com.socialcafe.activities_fragments.activity_invoice.InvoiceActivity;
import com.socialcafe.activities_fragments.activity_products.ProductsActivity;
import com.socialcafe.activities_fragments.activity_products.TablesActivity;
import com.socialcafe.adapters.ProductOrder2Adapter;
import com.socialcafe.adapters.ProductOrderAdapter;
import com.socialcafe.databinding.ActivityOrderDetailsBinding;
import com.socialcafe.language.Language;
import com.socialcafe.models.CreateOrderModel;
import com.socialcafe.models.ItemCartModel;
import com.socialcafe.models.OrderModel;
import com.socialcafe.models.SingleOrderDataModel;
import com.socialcafe.models.StatusResponse;
import com.socialcafe.models.UserModel;
import com.socialcafe.preferences.Preferences;
import com.socialcafe.remote.Api;
import com.socialcafe.share.Common;
import com.socialcafe.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailsActivity extends AppCompatActivity {
    private ActivityOrderDetailsBinding binding;
    private ProductOrder2Adapter adapter;
    private List<ItemCartModel> productList;
    private OrderModel orderModel;
    private CreateOrderModel createOrderModel;
    private ActivityResultLauncher<Intent> launcher;
    private String lang;
    private UserModel userModel;
    private Preferences preferences;

    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

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
        preferences=Preferences.getInstance();
        userModel=preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        createOrderModel = new CreateOrderModel();
        binding.setModel(createOrderModel);
        productList = new ArrayList<>();
        // productList.addAll(orderModel.getDetails());
        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductOrder2Adapter(productList, this);
        binding.recView.setAdapter(adapter);
        setData();
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderDetailsActivity.this, ProductsActivity.class);
                intent.putExtra("data", createOrderModel);
                launcher.launch(intent);
            }
        });
        binding.btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderDetailsActivity.this, TablesActivity.class);
                intent.putExtra("data", createOrderModel);
                launcher.launch(intent);
            }
        });
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                createOrderModel = (CreateOrderModel) result.getData().getSerializableExtra("data");
                binding.setModel(createOrderModel);
                productList.clear();
                productList.addAll(createOrderModel.getDetails());
                adapter.notifyDataSetChanged();
                //setData();
            }
        });
        binding.btnendorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createOrderModel.setPaid_by_id("1");
                createOrderModel.setSale_status("1");
                createOrderModel.setDraft("1");
                createOrderModel.setUser_id(userModel.getUser().getId());
                createOrderModel.setPaying_amount(createOrderModel.getGrand_total());
                createOrderModel.setPaid_amount(createOrderModel.getGrand_total());
                createOrder();
            }
        });
    }

    private void setData() {
        createOrderModel.setBiller_id(orderModel.getBiller_id());
        createOrderModel.setBiller_id_hidden(orderModel.getBiller_id());
        createOrderModel.setSale_id(orderModel.getId());
        createOrderModel.setSale_status(orderModel.getSale_status());
        createOrderModel.setCheque_no("");
        createOrderModel.setSale_id(orderModel.getId());
        createOrderModel.setPaid_by_id("3");
        createOrderModel.setOrder_tax(Double.parseDouble(orderModel.getOrder_tax()));
        createOrderModel.setGrand_total(Double.parseDouble(orderModel.getGrand_total()));
        createOrderModel.setOrder_discount(Double.parseDouble(orderModel.getOrder_discount()));
        createOrderModel.setCoupon_active("");
        createOrderModel.setCoupon_id(orderModel.getCoupon_id());
        createOrderModel.setCustomer_id(orderModel.getCustomer_id());
        createOrderModel.setCustomer_id_hidden(orderModel.getCustomer_id());
        createOrderModel.setOrder_tax_rate(Double.parseDouble(orderModel.getOrder_tax_rate()));
        createOrderModel.setCoupon_discount(orderModel.getCoupon_discount());
        createOrderModel.setDelivery_companies_id("");
        createOrderModel.setDraft("1");
        createOrderModel.setGift_card_id("");
        createOrderModel.setPaid_amount(Double.parseDouble(orderModel.getPaid_amount()));
        createOrderModel.setPaying_amount(Double.parseDouble(orderModel.getGrand_total()));
        createOrderModel.setPayment_note("");
        createOrderModel.setItem(orderModel.getItem());
        createOrderModel.setPos("1");
        createOrderModel.setReference_no("");
        createOrderModel.setSale_note(orderModel.getSale_note());
        createOrderModel.setShipping_cost(orderModel.getShipping_cost());
        createOrderModel.setStaff_note(orderModel.getStaff_note());
        createOrderModel.setTable_id(orderModel.getTable_id());
        createOrderModel.setTotal_discount(orderModel.getTotal_discount());
        createOrderModel.setTotal_tax(Double.parseDouble(orderModel.getTotal_tax()));
        createOrderModel.setTotal_price(Double.parseDouble(orderModel.getTotal_price()));
        createOrderModel.setTotal_qty(orderModel.getTotal_qty());
        createOrderModel.setWarehouse_id(orderModel.getWarehouse_id());
        createOrderModel.setWarehouse_id_hidden(orderModel.getWarehouse_id());
        createOrderModel.setUser_id(userModel.getUser().getId());
        List<ItemCartModel> itemCartModelList = new ArrayList<>();
        for (int i = 0; i < orderModel.getDetails().size(); i++) {
            OrderModel.Detials detials = orderModel.getDetails().get(i);
            ItemCartModel itemCartModel = new ItemCartModel();
            itemCartModel.setName(detials.getProduct().getName());
            itemCartModel.setNet_unit_price(Double.parseDouble(detials.getNet_unit_price()));
            itemCartModel.setProduct_id(Integer.parseInt(detials.getProduct_id()));
            itemCartModel.setProduct_batch_id("");
            if (detials.getProduct().getUnit() != null) {
                itemCartModel.setSale_unit(detials.getProduct().getUnit().getUnit_name());
            } else {
                itemCartModel.setSale_unit("n/a");
            }
            itemCartModel.setProduct_code(detials.getProduct().getCode());
            itemCartModel.setDiscount(0);
            itemCartModel.setTax(Double.parseDouble(detials.getTax()));
            itemCartModel.setTax_rate(Double.parseDouble(detials.getTax_rate()));
            itemCartModel.setSubtotal(Double.parseDouble(detials.getTotal()));
            itemCartModel.setQty(Integer.parseInt(detials.getQty()));
            itemCartModel.setImage(detials.getProduct().getImage());
            itemCartModel.setStock(detials.getProduct().getCan_make());
            //   itemCartModel.setSale_unit(detials.getsa);

            itemCartModelList.add(itemCartModel);
        }
        createOrderModel.setDetails(itemCartModelList);
        productList.clear();
        productList.addAll(itemCartModelList);
        adapter.notifyDataSetChanged();
        binding.setModel(createOrderModel);
    }

    public void endorder(String id) {
        Api.getService(Tags.base_url).changeStatus(id + "")
                .enqueue(new Callback<StatusResponse>() {
                    @Override
                    public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {

                        if (response.isSuccessful()) {
                            Intent intent = new Intent(OrderDetailsActivity.this, InvoiceActivity.class);
                            intent.putExtra("data", id);
                            startActivity(intent);
                            finish();

                        } else {


                            try {
                                Log.e("error_code", response.code() + "_" + response.errorBody().string());
                            } catch (NullPointerException e) {

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<StatusResponse> call, Throwable t) {
                        try {

                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    //     Toast.makeText(SignUpActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else if (t.getMessage().toLowerCase().contains("socket") || t.getMessage().toLowerCase().contains("canceled")) {
                                } else {
                                    //  Toast.makeText(SignUpActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {

                        }
                    }
                });
    }

    public void createOrder() {
        try {
            Gson gson = new Gson();
            String user_data = gson.toJson(createOrderModel);
            Log.e(";llll",user_data);
            ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();


            Api.getService(Tags.base_url)
                    .createOrder(createOrderModel)
                    .enqueue(new Callback<SingleOrderDataModel>() {
                        @Override
                        public void onResponse(Call<SingleOrderDataModel> call, Response<SingleOrderDataModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful()) {


                                if (response.body() != null && response.body().getData() != null) {
                                    Intent intent = new Intent(OrderDetailsActivity.this, InvoiceActivity.class);
                                    intent.putExtra("data", response.body().getData().getId()+"");
                                    startActivity(intent);
                                    finish();
                                    // navigateToOrderDetialsActivity(response.body());

                                }

                            } else {
                                dialog.dismiss();
                                try {
                                    Log.e("error_code", response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                if (response.code() == 500) {
                                    // Toast.makeText(CheckoutActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    //Toast.makeText(CheckoutActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<SingleOrderDataModel> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage() != "") {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        //      Toast.makeText(CheckoutActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        //    Toast.makeText(CheckoutActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }

}