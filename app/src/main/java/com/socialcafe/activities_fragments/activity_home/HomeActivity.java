package com.socialcafe.activities_fragments.activity_home;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.socialcafe.R;
import com.socialcafe.activities_fragments.activity_cart.CartActivity;
import com.socialcafe.activities_fragments.activity_cash_register_detials.CashRegisterDetialsActivity;
import com.socialcafe.activities_fragments.activity_currentorder.CurrentOrderActivity;
import com.socialcafe.activities_fragments.activity_discount_day.DiscountDayActivity;
import com.socialcafe.activities_fragments.activity_invoice.InvoiceActivity;
import com.socialcafe.activities_fragments.activity_login.LoginActivity;
import com.socialcafe.activities_fragments.activity_previousorder.PreviousOrderActivity;
import com.socialcafe.adapters.HomeAdapter;
import com.socialcafe.adapters.MarkAdapter;
import com.socialcafe.adapters.ProductAdapter;
import com.socialcafe.adapters.ProductDetialsAdapter;
import com.socialcafe.databinding.ActivityHomeBinding;
import com.socialcafe.language.Language;
import com.socialcafe.models.BrandDataModel;
import com.socialcafe.models.BrandModel;
import com.socialcafe.models.CategoryDataModel;
import com.socialcafe.models.CategoryModel;
import com.socialcafe.models.CreateOrderModel;
import com.socialcafe.models.InvoiceModel;
import com.socialcafe.models.ItemCartModel;
import com.socialcafe.models.ProductDataModel;
import com.socialcafe.models.ProductDetialsModel;
import com.socialcafe.models.ProductModel;
import com.socialcafe.models.StatusResponse;
import com.socialcafe.models.UserModel;
import com.socialcafe.preferences.Preferences;
import com.socialcafe.remote.Api;
import com.socialcafe.share.Common;
import com.socialcafe.tags.Tags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private Preferences preferences;
    private UserModel userModel;
    private String lang;
    private ActionBarDrawerToggle toggle;
    private ActivityResultLauncher<Intent> launcher;
    private ActivityResultLauncher<Intent> launcher2;
    private List<ProductModel> productModels, allproduct;

    private String type;
    private List<CategoryModel> categoryModelList;
    private HomeAdapter homeAdapter;
    private List<BrandModel> brandModelList;
    private List<ProductModel> productModelList;
    private MarkAdapter markAdapter;
    private ProductAdapter productAdapter;
    private ProductDetialsAdapter productDetialsAdapter;
    private List<ProductModel> productDetialsModelList;
    private List<ProductDetialsModel> allproductDetialsModelList;
    private int pos = -1;
    private ProductModel productModel;
    private List<Integer> productids;
    private List<Integer> productindex;
    private List<Integer> categoryindex;

    private int times;
    public int category_id;
    private int check = -1;

    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        getDataFromIntent();
        initView();

    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {

            type = intent.getStringExtra("type");

        }
    }

    private void initView() {
        categoryModelList = new ArrayList<>();
        categoryindex = new ArrayList<>();
        categoryindex.add(2);
        categoryindex.add(3);
        categoryindex.add(4);
        categoryindex.add(5);
        categoryindex.add(8);
        categoryindex.add(13);
        categoryindex.add(16);
        brandModelList = new ArrayList<>();
        productModelList = new ArrayList<>();
        allproduct = new ArrayList<>();
        productDetialsModelList = new ArrayList<>();
        allproductDetialsModelList = new ArrayList<>();
        productids = new ArrayList<>();
        productindex = new ArrayList<>();
        productModels = new ArrayList<>();
        preferences = Preferences.getInstance();
        getCartItemCount();
        userModel = preferences.getUserData(this);
        homeAdapter = new HomeAdapter(categoryModelList, this);
        markAdapter = new MarkAdapter(brandModelList, this);
        productAdapter = new ProductAdapter(productModelList, this);
        productDetialsAdapter = new ProductDetialsAdapter(allproductDetialsModelList, this);
        binding.recviewCategory.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        binding.recviewCategory.setAdapter(homeAdapter);
        binding.recView.setLayoutManager(new GridLayoutManager(this, 3));
        binding.recView.setAdapter(productAdapter);
        binding.recViewmark.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        binding.recViewmark.setAdapter(markAdapter);
        binding.recviewDetials.setLayoutManager(new LinearLayoutManager(this));
        binding.recviewDetials.setAdapter(productDetialsAdapter);
//        if (userModel != null) {
//            binding.setModel(userModel);
//        }
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);

        toggle = new ActionBarDrawerToggle(this, binding.drawar, binding.toolbar, R.string.open, R.string.close);
//
//        toggle.setHomeAsUpIndicator(R.drawable.ic_menu);


        toggle.syncState();


        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                lang = result.getData().getStringExtra("lang");
                //   refreshActivity(lang);
            }

        });
        launcher2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            checkAvialbilty();

        });
        binding.llCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
        binding.flCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
        binding.flclose3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSheet3();
            }
        });
        binding.llInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drawar.closeDrawer(GravityCompat.START);

                Intent intent = new Intent(HomeActivity.this, InvoiceActivity.class);
                intent.putExtra("data", "0");
                startActivity(intent);
//                getlastInvoice();
//                Intent intent = new Intent(HomeActivity.this, InvoiceActivity.class);
//                startActivity(intent);
            }
        });
        binding.llcashRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drawar.closeDrawer(GravityCompat.START);

                check = 0;
                checkAvialbilty();
            }
        });
        binding.llCoalition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drawar.closeDrawer(GravityCompat.START);

                Intent intent = new Intent(HomeActivity.this, DiscountDayActivity.class);
                startActivity(intent);
            }
        });
        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mony = binding.edtMony.getText().toString();
                if (!mony.trim().isEmpty()) {
                    enterMony(mony);
                } else {
                    binding.edtMony.setError(getResources().getString(R.string.field_req));
                }
            }
        });
        binding.lllogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToSignInActivity();

            }
        });
        checkAvialbilty();
        if (userModel != null) {
//            EventBus.getDefault().register(this);
//            getNotificationCount();
//            updateTokenFireBase();
//            updateLocation();
        }

        binding.flclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSheet();
            }
        });
        binding.flclose2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSheet2();
            }
        });
        binding.btnchoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ItemCartModel> productDetailsList;
                CreateOrderModel add_order_model = preferences.getCartData(HomeActivity.this);
                if (add_order_model != null) {
                    productDetailsList = add_order_model.getDetails();
                } else {
                    add_order_model = new CreateOrderModel();
                    productDetailsList = new ArrayList<>();
                }
                //Log.e("ldkdkdkkd", productids.size() + " " + productModel.getTimes());
                if (productids.size() == times) {
                    binding.checkbox.setChecked(false);
                    allproductDetialsModelList.clear();
                    if (productModel.getFirst_stock().getQty() > 0) {
                        ItemCartModel productDetails = new ItemCartModel();
                        productDetails.setQty(1);
                        productDetails.setImage(productModel.getImage());
                        productDetails.setName(productModel.getName());
                        productDetails.setNet_unit_price(productModel.getPrice());
                        productDetails.setProduct_id(productModel.getId());
                        productDetails.setProduct_batch_id("");
                        productDetails.setProduct_code(productModel.getCode());
                        productDetails.setDiscount(0);
                        productDetails.setStock((int) productModel.getFirst_stock().getQty());

                        if (productModel.getTax() != null) {
                            productDetails.setTax((productModel.getPrice() * productModel.getTax().getRate()) / 100);
                            productDetails.setTax_rate(productModel.getTax().getRate());
                            productDetails.setSubtotal((((productModel.getPrice() * productModel.getTax().getRate()) / 100) + productModel.getPrice()) * productDetails.getQty());

                        } else {
                            productDetails.setSubtotal(productModel.getPrice() * productDetails.getQty());

                        }
                        if (productModel.getUnit() != null) {
                            productDetails.setSale_unit(productModel.getUnit().getUnit_name());
                        } else {
                            productDetails.setSale_unit("n/a");
                        }
                        productDetails.setProducts_id(productids);
                        productDetailsList.add(productDetails);
                        add_order_model.setDetails(productDetailsList);
                        productModel.setCount(1);
                        productModelList.set(pos, productModel);
                        productAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(HomeActivity.this, getResources().getString(R.string.unvailable), Toast.LENGTH_LONG).show();
                    }

                    preferences.create_update_cart(HomeActivity.this, add_order_model);
                    getCartItemCount();
                    closeSheet2();

                } else {
                    Toast.makeText(HomeActivity.this, getResources().getString(R.string.choose_product), Toast.LENGTH_LONG).show();
                }
            }
        });
//        binding.checkbox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //   Log.e("dkdkk",binding.checkbox.isChecked()+"");
//                if (binding.checkbox.isChecked()) {
//                    Log.e("dkdkk", binding.checkbox.isChecked() + "" + productids.size());
//                    if (productids.size() > 0) {
//                        int id = productids.get(0);
//                        productids.clear();
//                        productids.add(id);
//                        int index = -1;
//                        for (int j = 1; j < allproductDetialsModelList.get(0).getProductModelList().size(); j++) {
//                            Log.e("hhhhh", productids.get(0) + " " + allproductDetialsModelList.get(0).getProductModelList().get(j).getFirst_stock().getProduct_id());
//
//                            if (productids.get(0) == allproductDetialsModelList.get(0).getProductModelList().get(j).getFirst_stock().getProduct_id()) {
//                                index = j;
//                                break;
//                            }
//                        }
//
//                        if (index != -1) {
//                            //  Log.e("sssss", index + "");
//                            allproductDetialsModelList.get(0).setSelection(index);
//
//                            for (int i = 1; i < allproductDetialsModelList.size(); i++) {
//
//                                if (notbig(allproductDetialsModelList.get(i).getProductModelList().get(index))) {
//                                    Log.e("ssiiisss", index + "");
//
//                                    allproductDetialsModelList.get(i).setSelection(index);
//                                    productids.add(allproductDetialsModelList.get(i).getProductModelList().get(index).getFirst_stock().getProduct_id());
//                                } else {
//                                    allproductDetialsModelList.get(i).setSelection(0);
//                                }
//                            }
//                        }
//
//                        productDetialsAdapter.notifyDataSetChanged();
//                    }
//                } else {
//                    for (int j = 1; j < allproductDetialsModelList.size(); j++) {
//                        allproductDetialsModelList.get(j).setSelection(0);
//                    }
//                    productDetialsAdapter.notifyDataSetChanged();
//                }
//            }
//        });
        binding.llMyOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CurrentOrderActivity.class);
                startActivity(intent);
            }
        });
        binding.llPreorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, PreviousOrderActivity.class);
                startActivity(intent);
            }
        });
        getProdusts();
        getProdusts("0", "0", "1");
        category_id = 0;
        // binding.recviewCategory.setNestedScrollingEnabled(true);
        getCategory();

    }

    public void openSheet() {

        //binding.btnAddBid.setAlpha(.4f);
        binding.expandLayout.setExpanded(true, true);
        getBrands();
    }

    public void closeSheet() {
        // binding.btnAddBid.setAlpha(0);
        binding.expandLayout.collapse(true);

    }

    public void openSheet2(ProductModel productModel, int pos) {

        //binding.btnAddBid.setAlpha(.4f);
        binding.expandLayout2.setExpanded(true, true);


    }

    public void closeSheet2() {
        // binding.btnAddBid.setAlpha(0);
        productids.clear();
        productindex.clear();
        binding.checkbox.setChecked(false);
        allproductDetialsModelList.clear();
        productDetialsAdapter.notifyDataSetChanged();
        binding.expandLayout2.collapse(true);

    }

    public void openSheet3() {

        //binding.btnAddBid.setAlpha(.4f);
        binding.expandLayout3.setExpanded(true, true);
        //getBrands();
    }

    public void closeSheet3() {
        // binding.btnAddBid.setAlpha(0);
        binding.expandLayout3.collapse(true);
        if (check == 0) {
            check = -1;
            Intent intent = new Intent(HomeActivity.this, CashRegisterDetialsActivity.class);
            launcher2.launch(intent);
        }

    }

    private void getCategory() {
        categoryModelList.clear();
        homeAdapter.notifyDataSetChanged();

        Api.getService(Tags.base_url)
                .getDepartments()
                .enqueue(new Callback<CategoryDataModel>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(Call<CategoryDataModel> call, Response<CategoryDataModel> response) {
                        // binding.progBarCategory.setVisibility(View.GONE);
                        if (response.isSuccessful()) {

                            if (response.body() != null && response.body().getStatus() == 200) {
                                if (response.body().getData() != null) {
                                    if (response.body().getData().size() > 0) {

                                        updateDate(response.body());
                                    } else {
                                        //               binding.tvNoDataCategory.setVisibility(View.VISIBLE);

                                    }
                                }
                            } else {
                                //     binding.tvNoDataCategory.setVisibility(View.VISIBLE);

                                //  Toast.makeText(SignUpActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                            }


                        } else {

                            //binding.tvNoDataCategory.setVisibility(View.VISIBLE);

                            switch (response.code()) {
                                case 500:
                                    //   Toast.makeText(SignUpActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    //   Toast.makeText(SignUpActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            try {
                                Log.e("error_code", response.code() + "_");
                            } catch (NullPointerException e) {

                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<CategoryDataModel> call, Throwable t) {
                        try {
                            //binding.progBarCategory.setVisibility(View.GONE);
                            //binding.tvNoDataCategory.setVisibility(View.VISIBLE);

//                            binding.arrow.setVisibility(View.VISIBLE);
//
//                            binding.progBar.setVisibility(View.GONE);
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

    private void updateDate(CategoryDataModel body) {
        categoryModelList.addAll(body.getData());
        homeAdapter.notifyDataSetChanged();

        new Handler()
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Log.e("dkdkk","slldlk");
                        binding.recviewCategory.scrollToPosition(0);
                        //  binding.recviewCategory.smoothScrollToPosition(0);
                    }
                }, 10);
    }

    private void getBrands() {
        brandModelList.clear();
        markAdapter.notifyDataSetChanged();
        binding.progBarBrand.setVisibility(View.VISIBLE);
        binding.tvNoDataBrand.setVisibility(View.GONE);
        Api.getService(Tags.base_url)
                .getBrands()
                .enqueue(new Callback<BrandDataModel>() {
                    @Override
                    public void onResponse(Call<BrandDataModel> call, Response<BrandDataModel> response) {
                        binding.progBarBrand.setVisibility(View.GONE);
                        if (response.isSuccessful()) {

                            if (response.body() != null && response.body().getStatus() == 200) {
                                if (response.body().getData() != null) {
                                    if (response.body().getData().size() > 0) {
                                        brandModelList.addAll(response.body().getData());
                                        markAdapter.notifyDataSetChanged();
                                    } else {
                                        binding.tvNoDataBrand.setVisibility(View.VISIBLE);

                                    }
                                }
                            } else {
                                binding.tvNoDataBrand.setVisibility(View.VISIBLE);

                                //  Toast.makeText(SignUpActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                            }


                        } else {

                            binding.tvNoDataBrand.setVisibility(View.VISIBLE);

                            switch (response.code()) {
                                case 500:
                                    //   Toast.makeText(SignUpActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    //   Toast.makeText(SignUpActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            try {
                                Log.e("error_code", response.code() + "_");
                            } catch (NullPointerException e) {

                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<BrandDataModel> call, Throwable t) {
                        try {
                            binding.progBarBrand.setVisibility(View.GONE);
                            binding.tvNoDataBrand.setVisibility(View.VISIBLE);

//                            binding.arrow.setVisibility(View.VISIBLE);
//
//                            binding.progBar.setVisibility(View.GONE);
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

    @SuppressLint("NotifyDataSetChanged")
    public void getProdusts(String brand_id, String cat_id, String isfeatured) {
        Log.e("kkkkk",userModel.getUser().getId()+"");
        productModelList.clear();
        productAdapter.notifyDataSetChanged();
        binding.progBar.setVisibility(View.VISIBLE);
        binding.tvNoData.setVisibility(View.GONE);
        Api.getService(Tags.base_url)
                .getProducts(userModel.getUser().getId() + "", brand_id, cat_id, isfeatured, "15")
                .enqueue(new Callback<ProductDataModel>() {
                    @Override
                    public void onResponse(Call<ProductDataModel> call, Response<ProductDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {

                            if (response.body() != null && response.body().getStatus() == 200) {
                                if (response.body().getData() != null) {
                                    if (response.body().getData().size() > 0) {
                                        productModelList.clear();
                                        productModelList.addAll(response.body().getData());
                                        productAdapter.notifyDataSetChanged();
                                    } else if (response.body().getStatus() == 405) {
                                        productModelList.clear();
                                        productAdapter.notifyDataSetChanged();
                                        Toast.makeText(HomeActivity.this, getResources().getString(R.string.permisson), Toast.LENGTH_LONG).show();
                                    } else {
                                        productModelList.clear();
                                        productAdapter.notifyDataSetChanged();
                                        binding.tvNoData.setVisibility(View.VISIBLE);

                                    }
                                }
                            }
                            else if (response.body().getStatus() == 405) {
                                productModelList.clear();
                                productAdapter.notifyDataSetChanged();
                                Toast.makeText(HomeActivity.this, getResources().getString(R.string.permisson), Toast.LENGTH_LONG).show();
                            }
                            else {
                                binding.tvNoData.setVisibility(View.VISIBLE);
                                Log.e("kdkdk", response.code() + "");
                                //  Toast.makeText(SignUpActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                            }


                        } else {

                            binding.tvNoData.setVisibility(View.VISIBLE);

                            switch (response.code()) {
                                case 500:
                                    //   Toast.makeText(SignUpActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    //   Toast.makeText(SignUpActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            try {
                                Log.e("error_code", response.code() + "_");
                            } catch (NullPointerException e) {

                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<ProductDataModel> call, Throwable t) {
                        try {
                            binding.progBar.setVisibility(View.GONE);
                            binding.tvNoData.setVisibility(View.VISIBLE);

//                            binding.arrow.setVisibility(View.VISIBLE);
//
//                            binding.progBar.setVisibility(View.GONE);
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

//    private void getProdustDetials(ProductModel productModel, int pos) {
//        binding.progBarDetials.setVisibility(View.VISIBLE);
//        productids.clear();
//        binding.checkbox.setChecked(false);
//        allproductDetialsModelList.clear();
//        productDetialsAdapter.notifyDataSetChanged();
//        this.productModel = productModel;
//        this.pos = pos;
//        //getProdustDetials(productModel);
//        // Log.e("kdjdjdj",productModel.getFirst_stock().getProduct_id()+"  "+productModel.getFirst_stock().getWarehouse_id());
//        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
//        dialog.setCancelable(false);
//        dialog.show();
//        Api.getService(Tags.base_url)
//                .getProductDetials(productModel.getFirst_stock().getProduct_id() + "", userModel.getUser().getWarehouse_id() + "")
//                .enqueue(new Callback<ProductDataModel>() {
//                    @Override
//                    public void onResponse(Call<ProductDataModel> call, Response<ProductDataModel> response) {
//                        dialog.dismiss();
//                        binding.progBarDetials.setVisibility(View.GONE);
//                        if (response.isSuccessful()) {
//                            if (response.body() != null && response.body().getStatus() == 200) {
//                                updateData(response.body());
//                            } else {
//                                Log.e("kdkdk", response.code() + "");
//                                //  Toast.makeText(SignUpActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
//
//                            }
//
//
//                        } else {
//
//
//                            switch (response.code()) {
//                                case 500:
//                                    //   Toast.makeText(SignUpActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
//                                    break;
//                                default:
//                                    //   Toast.makeText(SignUpActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
//                                    break;
//                            }
//                            try {
//                                Log.e("error_code", response.code() + "_");
//                            } catch (NullPointerException e) {
//
//                            }
//                        }
//
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<ProductDataModel> call, Throwable t) {
//                        try {
//                            dialog.dismiss();
//                            binding.progBarDetials.setVisibility(View.GONE);
//
////                            binding.arrow.setVisibility(View.VISIBLE);
////
////                            binding.progBar.setVisibility(View.GONE);
//                            if (t.getMessage() != null) {
//                                Log.e("error", t.getMessage());
//                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
//                                    //     Toast.makeText(SignUpActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
//                                } else if (t.getMessage().toLowerCase().contains("socket") || t.getMessage().toLowerCase().contains("canceled")) {
//                                } else {
//                                    //  Toast.makeText(SignUpActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//
//                        } catch (Exception e) {
//
//                        }
//                    }
//                });
//
//    }
//
//    private void getProdustDetials(ProductModel productModel, int pos, int pos2) {
//
//        //getProdustDetials(productModel);
//        // Log.e("kdjdjdj",productModel.getFirst_stock().getProduct_id()+"  "+productModel.getFirst_stock().getWarehouse_id());
//        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
//        dialog.setCancelable(false);
//        dialog.show();
//        Api.getService(Tags.base_url)
//                .getProductDetials(productModel.getFirst_stock().getProduct_id() + "", userModel.getUser().getWarehouse_id() + "")
//                .enqueue(new Callback<ProductDataModel>() {
//                    @Override
//                    public void onResponse(Call<ProductDataModel> call, Response<ProductDataModel> response) {
//                        dialog.dismiss();
//                        binding.progBarDetials.setVisibility(View.GONE);
//                        if (response.isSuccessful()) {
//                            if (response.body() != null && response.body().getStatus() == 200) {
//                                updateData2(response.body(), pos2, pos, productModel);
//                            } else {
//                                Log.e("kdkdk", response.code() + "");
//                                //  Toast.makeText(SignUpActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
//
//                            }
//
//
//                        } else {
//
//
//                            switch (response.code()) {
//                                case 500:
//                                    //   Toast.makeText(SignUpActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
//                                    break;
//                                default:
//                                    //   Toast.makeText(SignUpActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
//                                    break;
//                            }
//                            try {
//                                Log.e("error_code", response.code() + "_");
//                            } catch (NullPointerException e) {
//
//                            }
//                        }
//
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<ProductDataModel> call, Throwable t) {
//                        try {
//                            dialog.dismiss();
//                            binding.progBarDetials.setVisibility(View.GONE);
//
////                            binding.arrow.setVisibility(View.VISIBLE);
////
////                            binding.progBar.setVisibility(View.GONE);
//                            if (t.getMessage() != null) {
//                                Log.e("error", t.getMessage());
//                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
//                                    //     Toast.makeText(SignUpActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
//                                } else if (t.getMessage().toLowerCase().contains("socket") || t.getMessage().toLowerCase().contains("canceled")) {
//                                } else {
//                                    //  Toast.makeText(SignUpActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//
//                        } catch (Exception e) {
//
//                        }
//                    }
//                });
//
//    }

    private void updateData2(ProductDataModel body, int pos, int layoutPosition, ProductModel productModel) {
        List<ItemCartModel> productDetailsList;
        CreateOrderModel add_order_model = preferences.getCartData(HomeActivity.this);
        if (add_order_model != null) {
            productDetailsList = add_order_model.getDetails();
        } else {
            add_order_model = new CreateOrderModel();
            productDetailsList = new ArrayList<>();
        }
        if (!body.getType().equals("box")) {
            Log.e("dkkdj", productModel.getFirst_stock().getQty() + "");
            ItemCartModel productDetails = productDetailsList.get(pos);

            if ((int) productModel.getFirst_stock().getQty() >= 1 + productDetails.getQty()) {
                productDetails.setQty(1 + productDetails.getQty());
                productDetails.setNet_unit_price(productModel.getPrice());
                if (productModel.getTax() != null) {
                    productDetails.setSubtotal((((productModel.getPrice() * productModel.getTax().getRate()) / 100) + productModel.getPrice()) * productDetails.getQty());

                } else {
                    productDetails.setSubtotal(productModel.getPrice() * productDetails.getQty());

                }
                productDetailsList.remove(pos);
                productDetailsList.add(pos, productDetails);

                add_order_model.setDetails(productDetailsList);
                productModel.setCount(productDetails.getQty());
                productModelList.set(layoutPosition, productModel);
                productAdapter.notifyDataSetChanged();
                preferences.create_update_cart(HomeActivity.this, add_order_model);
                getCartItemCount();
            } else {
                Toast.makeText(this, getResources().getString(R.string.unvailable), Toast.LENGTH_LONG).show();
                productModel.setCount(productDetails.getQty());
                productModelList.set(layoutPosition, productModel);
                productAdapter.notifyDataSetChanged();
            }
        } else {
            boolean add = true;
            Log.e("kkdjdjdj", body.getSubProducts().size() + "");
            ItemCartModel productDetails = productDetailsList.get(pos);
            List<Integer> integerList = productDetails.getProducts_id();
            for (int i = 0; i < body.getSubProducts().size(); i++) {
                if (integerList.contains(body.getSubProducts().get(i).getId())) {
                    int count = Collections.frequency(integerList, body.getSubProducts().get(i).getId());

                    Log.e("kkdjdjdj", count * (productDetails.getQty() + 1) + " " + body.getSubProducts().get(i).getFirst_stock().getQty());

                    if (count * (productDetails.getQty() + 1) > body.getSubProducts().get(i).getFirst_stock().getQty()) {
                        add = false;
                        break;
                    }
                }
            }
            if (add) {
                productDetails.setQty(1 + productDetails.getQty());
                productDetails.setNet_unit_price(productModel.getPrice());
                if (productModel.getTax() != null) {
                    productDetails.setSubtotal((((productModel.getPrice() * productModel.getTax().getRate()) / 100) + productModel.getPrice()) * productDetails.getQty());

                } else {
                    productDetails.setSubtotal(productModel.getPrice() * productDetails.getQty());

                }
                productDetailsList.remove(pos);
                productDetailsList.add(pos, productDetails);

                add_order_model.setDetails(productDetailsList);
                productModel.setCount(productDetails.getQty());
                productModelList.set(layoutPosition, productModel);
                productAdapter.notifyDataSetChanged();
                preferences.create_update_cart(HomeActivity.this, add_order_model);
                getCartItemCount();
            } else {
                Toast.makeText(this, getResources().getString(R.string.unvailable), Toast.LENGTH_LONG).show();
                productModel.setCount(productDetails.getQty());
                productModelList.set(layoutPosition, productModel);
                productAdapter.notifyDataSetChanged();
            }
        }

    }

//    private void updateData(ProductDataModel body) {
//        // Log.e("kdkkdk", body.getType());
//        if (body.getType().equals("box")) {
//
//            if (body.getSubProducts() != null) {
//                if (body.getSubProducts().size() > 0) {
//                    productDetialsModelList.clear();
//                    productDetialsModelList.addAll(body.getSubProducts());
//                    applychange(productModel, productDetialsModelList, body.getProduct().getTimes());
//                    openSheet2(productModel, pos);
//
//                } else {
//                    Toast.makeText(HomeActivity.this, getResources().getString(R.string.no_products), Toast.LENGTH_LONG).show();
//
//                }
//            }
//        } else {
//            List<ItemCartModel> productDetailsList;
//            CreateOrderModel add_order_model = preferences.getCartData(HomeActivity.this);
//            if (add_order_model != null) {
//                productDetailsList = add_order_model.getDetails();
//            } else {
//                add_order_model = new CreateOrderModel();
//                productDetailsList = new ArrayList<>();
//            }
//            if (productModel.getFirst_stock().getQty() > 0) {
//                ItemCartModel productDetails = new ItemCartModel();
//                productDetails.setQty(1);
//                productDetails.setImage(productModel.getImage());
//                productDetails.setName(productModel.getName());
//                productDetails.setNet_unit_price(productModel.getPrice());
//                productDetails.setProduct_id(productModel.getId());
//                productDetails.setStock((int) productModel.getFirst_stock().getQty());
//                productDetails.setProduct_batch_id("");
//                productDetails.setProduct_code(productModel.getCode());
//                productDetails.setProducts_id(new ArrayList<>());
//                productDetails.setDiscount(0);
//                if (productModel.getTax() != null) {
//                    productDetails.setTax((productModel.getPrice() * productModel.getTax().getRate()) / 100);
//                    productDetails.setTax_rate(productModel.getTax().getRate());
//                    productDetails.setSubtotal((((productModel.getPrice() * productModel.getTax().getRate()) / 100) + productModel.getPrice()) * productDetails.getQty());
//
//                } else {
//                    productDetails.setSubtotal(productModel.getPrice() * productDetails.getQty());
//
//                }
//                if (productModel.getUnit() != null) {
//                    productDetails.setSale_unit(productModel.getUnit().getUnit_name());
//                } else {
//                    productDetails.setSale_unit("n/a");
//                }
//                productDetailsList.add(productDetails);
//                add_order_model.setDetails(productDetailsList);
//                productModel.setCount(1);
//                productModelList.set(pos, productModel);
//                productAdapter.notifyDataSetChanged();
//                preferences.create_update_cart(HomeActivity.this, add_order_model);
//                getCartItemCount();
//            }
//
//
//        }
//
//    }

//    private void applychange(ProductModel productModel, List<ProductModel> productDetialsModelList, int times) {
//        allproductDetialsModelList.clear();
//        // productDetialsModelList.clear();
//        productDetialsAdapter.notifyDataSetChanged();
//        productDetialsModelList.add(0, new ProductModel(getResources().getString(R.string.choose_product)));
//        this.times = times;
//        for (int i = 0; i < times; i++) {
//            ProductDetialsModel productDetialsModel = new ProductDetialsModel();
//            productDetialsModel.setProductModelList(productDetialsModelList);
//            allproductDetialsModelList.add(productDetialsModel);
//        }
//        productDetialsAdapter.notifyDataSetChanged();
//
//    }

    public void showBrand(String s) {
        closeSheet();
        category_id = 0;
        getProdusts(s, "0", "0");

    }

    public void showCategory(String s) {
        productModelList.clear();
        productAdapter.notifyDataSetChanged();
        category_id = Integer.parseInt(s);
        getProdusts("0", s, "0");

    }

    public void addtocart(ProductModel productModel, int layoutPosition) {
        productids.clear();
        List<ItemCartModel> productDetailsList;
        CreateOrderModel add_order_model = preferences.getCartData(HomeActivity.this);
        // productDetailsList = new ArrayList<>();
        if (add_order_model != null) {
            productDetailsList = add_order_model.getDetails();
            if (productDetailsList == null) {
                productDetailsList = new ArrayList<>();
            }
        } else {
            // Log.e(";;;;","kkxxdkjdjjdj");
            add_order_model = new CreateOrderModel();
            productDetailsList = new ArrayList<>();
        }
        int pos = -1;

        for (int i = 0; i < productDetailsList.size(); i++) {
            //  Log.e("droooee", productDetailsList.get(i).getProduct_id() + "  " + productModel.getId());
            if (productDetailsList.get(i).getProduct_id() == productModel.getId()) {
                pos = i;
                break;
            }
        }

        if ((productModel.getCategory().getMake().equals("make")||productModel.getCategory().getMake().equals("both"))&&productModel.getCategory().getMake_place().equals("in")) {
            // Log.e("ssss", "ssssss");
                if (productModel.getCan_make() > 0) {
                    if (pos == -1) {
                        ItemCartModel productDetails = new ItemCartModel();
                        productDetails.setQty(1);
                        productDetails.setImage(productModel.getImage());
                        productDetails.setName(productModel.getName());
                        productDetails.setNet_unit_price(productModel.getPrice());
                        productDetails.setProduct_id(productModel.getId());
                        productDetails.setProduct_batch_id("");
                        productDetails.setProduct_code(productModel.getCode());
                        productDetails.setDiscount(0);
                        // productDetails.setCategory_id(productModel.getCategory_id());
                        productDetails.setStock((int) productModel.getCan_make());

                        if (productModel.getTax() != null) {
                            productDetails.setTax((productModel.getPrice() * productModel.getTax().getRate()) / 100);
                            productDetails.setTax_rate(productModel.getTax().getRate());
                            productDetails.setSubtotal((((productModel.getPrice() * productModel.getTax().getRate()) / 100) + productModel.getPrice()) * productDetails.getQty());

                        } else {
                            productDetails.setSubtotal(productModel.getPrice() * productDetails.getQty());

                        }
                        if (productModel.getUnit() != null) {
                            productDetails.setSale_unit(productModel.getUnit().getUnit_name());
                        } else {
                            productDetails.setSale_unit("n/a");
                        }
                        // productDetails.setProducts_id(Arrays.toString(productids.toArray()));
                        productDetailsList.add(productDetails);
                        add_order_model.setDetails(productDetailsList);
                        productModel.setCount(1);

                    } else {
                        ItemCartModel productDetails = productDetailsList.get(pos);
                        if (productDetails.getQty() + 1 <= productModel.getCan_make()) {
                            Log.e("jjjjjjj", productDetails.getQty() + " " + productModel.getCan_make());

                            productDetails.setQty(1 + productDetails.getQty());
                            productDetails.setNet_unit_price(productModel.getPrice());
                            if (productModel.getTax() != null) {
                                productDetails.setSubtotal((((productModel.getPrice() * productModel.getTax().getRate()) / 100) + productModel.getPrice()) * productDetails.getQty());

                            } else {
                                productDetails.setSubtotal(productModel.getPrice() * productDetails.getQty());

                            }
                            productDetailsList.remove(pos);
                            productDetailsList.add(pos, productDetails);

                            add_order_model.setDetails(productDetailsList);
                            productModel.setCount(productDetails.getQty());
                        } else {
                            Toast.makeText(HomeActivity.this, getResources().getString(R.string.unvailable), Toast.LENGTH_LONG).show();

                        }
                    }
                    productModelList.set(layoutPosition, productModel);
                    productAdapter.notifyDataSetChanged();
                    //accessDatabase.udatefirststock(productModel.getFirst_stock(), this);
                    //  accessDatabase.udateproduct(productModel, this);

                } else {
                    Toast.makeText(HomeActivity.this, getResources().getString(R.string.unvailable), Toast.LENGTH_LONG).show();
                }


        } else {
            if (productModel.getFirst_stock() != null && productModel.getFirst_stock().getQty() > 0) {
                if (pos == -1) {
                    ItemCartModel productDetails = new ItemCartModel();
                    productDetails.setQty(1);
                    productDetails.setImage(productModel.getImage());
                    productDetails.setName(productModel.getName());
                    productDetails.setNet_unit_price(productModel.getPrice());
                    productDetails.setProduct_id(productModel.getId());
                    productDetails.setProduct_batch_id("");
                    productDetails.setProduct_code(productModel.getCode());
                    productDetails.setDiscount(0);
                    //  productDetails.setCategory_id(productModel.getCategory_id());

                    productDetails.setStock((int) productModel.getFirst_stock().getQty());

                    if (productModel.getTax() != null) {
                        productDetails.setTax((productModel.getPrice() * productModel.getTax().getRate()) / 100);
                        productDetails.setTax_rate(productModel.getTax().getRate());
                        productDetails.setSubtotal((((productModel.getPrice() * productModel.getTax().getRate()) / 100) + productModel.getPrice()) * productDetails.getQty());

                    } else {
                        productDetails.setSubtotal(productModel.getPrice() * productDetails.getQty());

                    }
                    if (productModel.getUnit() != null) {
                        productDetails.setSale_unit(productModel.getUnit().getUnit_name());
                    } else {
                        productDetails.setSale_unit("n/a");
                    }
                    // productDetails.setProducts_id(Arrays.toString(productids.toArray()));
                    productDetailsList.add(productDetails);
                    add_order_model.setDetails(productDetailsList);
                    productModel.setCount(1);

                } else {

                    ItemCartModel productDetails = productDetailsList.get(pos);
                    Log.e("jjjjjjj", productDetails.getQty() + " " + productModel.getFirst_stock().getQty());

                    if (productDetails.getQty() + 1 <= productModel.getFirst_stock().getQty()) {
                        productDetails.setQty(1 + productDetails.getQty());
                        productDetails.setNet_unit_price(productModel.getPrice());
                        if (productModel.getTax() != null) {
                            productDetails.setSubtotal((((productModel.getPrice() * productModel.getTax().getRate()) / 100) + productModel.getPrice()) * productDetails.getQty());

                        } else {
                            productDetails.setSubtotal(productModel.getPrice() * productDetails.getQty());

                        }
                        productDetailsList.remove(pos);
                        productDetailsList.add(pos, productDetails);

                        add_order_model.setDetails(productDetailsList);
                        productModel.setCount(productDetails.getQty());
                    } else {
                        Toast.makeText(HomeActivity.this, getResources().getString(R.string.unvailable), Toast.LENGTH_LONG).show();

                    }
                }
                productModelList.set(layoutPosition, productModel);
                productAdapter.notifyDataSetChanged();
//                accessDatabase.udatefirststock(productModel.getFirst_stock(), this);
//                accessDatabase.udateproduct(productModel, this);

            } else {
                Toast.makeText(HomeActivity.this, getResources().getString(R.string.unvailable), Toast.LENGTH_LONG).show();
            }

        }
        if (pos != -1) {
            productModel.setCount(productDetailsList.get(pos).getQty());
            productModelList.set(layoutPosition, productModel);
            productAdapter.notifyDataSetChanged();
        }
        if (productDetailsList.size() > 0) {
            preferences.create_update_cart(HomeActivity.this, add_order_model);
            getCartItemCount();
        }
        else {
            preferences.clearCart(this);
            getCartItemCount();
        }

    }

    public void addid(ProductModel productModel, int index) {
        if (productindex.contains(index)) {
            productids.set(productindex.indexOf(index), productModel.getFirst_stock().getProduct_id());
        } else {
            productindex.add(index);
            productids.add(productModel.getFirst_stock().getProduct_id());
        }
    }

//    public boolean notbig(ProductModel productModel) {
//        int count = Collections.frequency(productids, productModel.first_stock.getProduct_id());
//        Log.e("countssss", count + "  " + (int) productModel.getFirst_stock().getQty() + "");
//
//        if (count + 1 <= (int) productModel.getFirst_stock().getQty()) {
//            return true;
//        }
//        return false;
//    }

    public void getCartItemCount() {
        if (preferences.getCartData(this) != null && preferences.getCartData(this).getDetails() != null) {
            //  view.onCartCountUpdate(preferences.getCartData(context).getCartModelList().size());
            binding.setCartcount(preferences.getCartData(this).getDetails().size());

        } else {
            binding.setCartcount(0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (preferences != null) {
            getCartItemCount();
            category_id = 0;

            getProdusts("0", "0", "1");
        }
    }

    public void getlastInvoice() {

        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();


        Api.getService(Tags.base_url)
                .getLatestSale(userModel.getUser().getId() + "")
                .enqueue(new Callback<InvoiceModel>() {
                    @Override
                    public void onResponse(Call<InvoiceModel> call, Response<InvoiceModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().getStatus() == 200) {
                                if (response.body().getData() != null) {
                                    try {
                                        binding.drawar.closeDrawer(GravityCompat.START);
                                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(response.body().getData()));
                                        startActivity(myIntent);
                                    } catch (ActivityNotFoundException e) {
//                                        Toast.makeText(this, "No application can handle this request."
//                                                + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
                                        e.printStackTrace();
                                    }
//                                    Intent intent = new Intent(HomeActivity.this, InvoiceActivity.class);
//                                    intent.putExtra("data", response.body().getData());
//                                    startActivity(intent);
                                } else if (response.body().getStatus() == 400) {
                                    Toast.makeText(HomeActivity.this, getResources().getString(R.string.no_invoice), Toast.LENGTH_SHORT).show();

                                }

                            }

                        } else {
                            if (response.code() == 500) {
                                Toast.makeText(HomeActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("ERROR", response.message() + "");

                                //     Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }


                        }
                    }

                    @Override
                    public void onFailure(Call<InvoiceModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            if (t.getMessage() != null) {
                                Log.e("msg_category_error", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    // Toast.makeText(SubscriptionActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    //Toast.makeText(SubscriptionActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }

//    private void getProdustDrink(ProductModel productModel, int pos, int layoutpos, int amount) {
//
//        this.productModel = productModel;
//        //getProdustDetials(productModel);
//        Log.e("kdjdjdj",productModel.getId()+"  "+userModel.getUser().getWarehouse_id());
//        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
//        dialog.setCancelable(false);
//        dialog.show();
//        Api.getService(Tags.base_url)
//                .getProductDetials(productModel.getId() + "", userModel.getUser().getWarehouse_id() + "", amount + "")
//                .enqueue(new Callback<ProductDataModel>() {
//                    @Override
//                    public void onResponse(Call<ProductDataModel> call, Response<ProductDataModel> response) {
//                        dialog.dismiss();
//                        if (response.isSuccessful()) {
//                            if (response.body() != null && response.body().getStatus() == 200) {
//                                updateDataDrink(response.body(), pos, layoutpos);
//                            } else {
//                                Log.e("kdkdk", response.code() + "");
//                                //  Toast.makeText(SignUpActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
//
//                            }
//
//
//                        } else {
//
//
//                            switch (response.code()) {
//                                case 500:
//                                    //   Toast.makeText(SignUpActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
//                                    break;
//                                default:
//                                    //   Toast.makeText(SignUpActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
//                                    break;
//                            }
//                            try {
//                                Log.e("error_code", response.code() + "_");
//                            } catch (NullPointerException e) {
//
//                            }
//                        }
//
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<ProductDataModel> call, Throwable t) {
//                        try {
//                            dialog.dismiss();
//
////                            binding.arrow.setVisibility(View.VISIBLE);
////
////                            binding.progBar.setVisibility(View.GONE);
//                            if (t.getMessage() != null) {
//                                Log.e("error", t.getMessage());
//                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
//                                    //     Toast.makeText(SignUpActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
//                                } else if (t.getMessage().toLowerCase().contains("socket") || t.getMessage().toLowerCase().contains("canceled")) {
//                                } else {
//                                    //  Toast.makeText(SignUpActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//
//                        } catch (Exception e) {
//
//                        }
//                    }
//                });
//
//    }

//    private void updateDataDrink(ProductDataModel body, int pos, int layoutPosition) {
//        if (body.getProduct().getQty() > 0) {
//            List<ItemCartModel> productDetailsList;
//            CreateOrderModel add_order_model = preferences.getCartData(HomeActivity.this);
//            if (add_order_model != null) {
//                productDetailsList = add_order_model.getDetails();
//            } else {
//                add_order_model = new CreateOrderModel();
//                productDetailsList = new ArrayList<>();
//            }
//            if (pos == -1) {
//                ItemCartModel productDetails = new ItemCartModel();
//                productDetails.setQty(1);
//                productDetails.setImage(productModel.getImage());
//                productDetails.setName(productModel.getName());
//                productDetails.setNet_unit_price(productModel.getPrice());
//                productDetails.setProduct_id(productModel.getId());
//                productDetails.setStock((int) productModel.getFirst_stock().getQty());
//                productDetails.setProduct_batch_id("");
//                productDetails.setProduct_code(productModel.getCode());
//                productDetails.setProducts_id(new ArrayList<>());
//                productDetails.setDiscount(0);
//                if (productModel.getTax() != null) {
//                    productDetails.setTax((productModel.getPrice() * productModel.getTax().getRate()) / 100);
//                    productDetails.setTax_rate(productModel.getTax().getRate());
//                    productDetails.setSubtotal((((productModel.getPrice() * productModel.getTax().getRate()) / 100) + productModel.getPrice()) * productDetails.getQty());
//
//                } else {
//                    productDetails.setSubtotal(productModel.getPrice() * productDetails.getQty());
//
//                }
//                if (productModel.getUnit() != null) {
//                    productDetails.setSale_unit(productModel.getUnit().getUnit_name());
//                } else {
//                    productDetails.setSale_unit("n/a");
//                }
//                productDetailsList.add(productDetails);
//                add_order_model.setDetails(productDetailsList);
//                productModel.setCount(1);
//                productModelList.set(layoutPosition, productModel);
//                productAdapter.notifyDataSetChanged();
//                preferences.create_update_cart(HomeActivity.this, add_order_model);
//                getCartItemCount();
//
//
//            } else {
//                ItemCartModel productDetails = productDetailsList.get(pos);
//
//                if ((int) productModel.getFirst_stock().getQty() >= 1 + productDetails.getQty()) {
//                    productDetails.setQty(1 + productDetails.getQty());
//                    productDetails.setNet_unit_price(productModel.getPrice());
//                    if (productModel.getTax() != null) {
//                        productDetails.setSubtotal((((productModel.getPrice() * productModel.getTax().getRate()) / 100) + productModel.getPrice()) * productDetails.getQty());
//
//                    } else {
//                        productDetails.setSubtotal(productModel.getPrice() * productDetails.getQty());
//
//                    }
//                    productDetailsList.remove(pos);
//                    productDetailsList.add(pos, productDetails);
//
//                    add_order_model.setDetails(productDetailsList);
//                    productModel.setCount(productDetails.getQty());
//                    productModelList.set(layoutPosition, productModel);
//                    productAdapter.notifyDataSetChanged();
//                    preferences.create_update_cart(HomeActivity.this, add_order_model);
//                    getCartItemCount();
//                }
//
//            }
//        } else {
//            Toast.makeText(this, getResources().getString(R.string.unvailable), Toast.LENGTH_LONG).show();
//            if (pos != -1) {
//                List<ItemCartModel> productDetailsList;
//                CreateOrderModel add_order_model = preferences.getCartData(HomeActivity.this);
//                if (add_order_model != null) {
//                    productDetailsList = add_order_model.getDetails();
//                } else {
//                    add_order_model = new CreateOrderModel();
//                    productDetailsList = new ArrayList<>();
//                }
//                ItemCartModel productDetails = productDetailsList.get(pos);
//
//                productModel.setCount(productDetails.getQty());
//                productModelList.set(layoutPosition, productModel);
//                productAdapter.notifyDataSetChanged();
//            }
//        }
//    }

    public void checkAvialbilty() {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .checkAvilabilty(userModel.getUser().getId() + "", userModel.getUser().getWarehouse_id() + "")
                .enqueue(new Callback<StatusResponse>() {
                    @Override
                    public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {

                            if (response.body().getStatus() == 200) {
                                if (check == 0) {
                                    Intent intent = new Intent(HomeActivity.this, CashRegisterDetialsActivity.class);
                                    launcher2.launch(intent);
                                }

                            } else if (response.body().getStatus() == 1000) {
                                openSheet3();
                                //  Log.e("kdkdk", response.code() + "");
                                //  Toast.makeText(SignUpActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                            }


                        } else {


                            switch (response.code()) {
                                case 500:
                                    //   Toast.makeText(SignUpActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    //   Toast.makeText(SignUpActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            try {
                                Log.e("error_code", response.code() + "_");
                            } catch (NullPointerException e) {

                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<StatusResponse> call, Throwable t) {
                        try {
                            dialog.dismiss();
//                            binding.arrow.setVisibility(View.VISIBLE);
//
//                            binding.progBar.setVisibility(View.GONE);
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

    public void enterMony(String mony) {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .enterMony(userModel.getUser().getId() + "", userModel.getUser().getWarehouse_id() + "", mony)
                .enqueue(new Callback<StatusResponse>() {
                    @Override
                    public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {

                            if (response.body().getStatus() == 200) {
                                closeSheet3();
                            }


                        } else {


                            switch (response.code()) {
                                case 500:
                                    //   Toast.makeText(SignUpActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    //   Toast.makeText(SignUpActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            try {
                                Log.e("error_code", response.code() + "_");
                            } catch (NullPointerException e) {

                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<StatusResponse> call, Throwable t) {
                        try {
                            dialog.dismiss();
//                            binding.arrow.setVisibility(View.VISIBLE);
//
//                            binding.progBar.setVisibility(View.GONE);
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

    public void navigateToSignInActivity() {
        preferences.create_update_userdata(this, null);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void getProdusts() {
        // progressDialog.show();
        // Log.e("kdkdkkd", userModel.getUser().getId() + "");
        //productModelList.clear();
        // productAdapter.notifyDataSetChanged();
        // binding.progBar.setVisibility(View.VISIBLE);
        //binding.tvNoData.setVisibility(View.GONE);
        Api.getService(Tags.base_url)
                .getmainproduct(userModel.getUser().getId() + "")
                .enqueue(new Callback<ProductDataModel>() {
                    @Override
                    public void onResponse(Call<ProductDataModel> call, Response<ProductDataModel> response) {
                        //  binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {

                            if (response.body() != null && response.body().getStatus() == 200) {
                                if (response.body().getData() != null) {

                                    if (response.body().getData().size() > 0) {
                                        allproduct.clear();
                                        allproduct.addAll(response.body().getData());
                                    } else {
                                        // Log.e("kdkdksssss", response.code() + "");

//                                        binding.tvNoData.setVisibility(View.VISIBLE);

                                    }
                                }
                            } else if (response.body().getStatus() == 401) {

                                //      Toast.makeText(HomeActivity.this, getResources().getString(R.string.permisson), Toast.LENGTH_LONG).show();
                                //  progressDialog.dismiss();
                            } else {
                                //   binding.tvNoData.setVisibility(View.VISIBLE);
                                //   progressDialog.dismiss();
                                Log.e("kdkdksssss", response.code() + "");
                                //  Toast.makeText(SignUpActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                            }


                        } else {
//                            progressDialog.dismiss();
//                            binding.tvNoData.setVisibility(View.VISIBLE);

                            switch (response.code()) {
                                case 1000:
                                    //   Toast.makeText(SignUpActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    //   Toast.makeText(SignUpActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            try {
                                Log.e("error_code", response.code() + "_");
                            } catch (NullPointerException e) {

                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<ProductDataModel> call, Throwable t) {
                        try {
//                            binding.progBar.setVisibility(View.GONE);
//                            binding.tvNoData.setVisibility(View.VISIBLE);
//                            progressDialog.dismiss();
                            //  accessDatabase.getProduct(HomeActivity.this, isfeatured, "featured");

//                            binding.arrow.setVisibility(View.VISIBLE);
//
//                            binding.progBar.setVisibility(View.GONE);
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

}
