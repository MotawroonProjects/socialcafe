package com.socialcafe.activities_fragments.activity_cash_register_detials;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.socialcafe.R;
import com.socialcafe.databinding.ActivityCashRegisterDetailsBinding;
import com.socialcafe.language.Language;
import com.socialcafe.models.ClosingDataModel;
import com.socialcafe.models.StatusResponse;
import com.socialcafe.models.UserModel;
import com.socialcafe.preferences.Preferences;
import com.socialcafe.remote.Api;
import com.socialcafe.share.Common;
import com.socialcafe.tags.Tags;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CashRegisterDetialsActivity extends AppCompatActivity {
    private ActivityCashRegisterDetailsBinding binding;
    private Preferences preferences;
    private String lang;
    private UserModel userModel;
    private ClosingDataModel closingDataModel;

    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cash_register_details);
        initView();

    }


    private void initView() {


        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        getClosing();
        binding.btnClosing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closing();
            }
        });

    }

    public void getClosing() {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .getClosing(userModel.getUser().getId() + "", userModel.getUser().getWarehouse_id() + "")
                .enqueue(new Callback<ClosingDataModel>() {
                    @Override
                    public void onResponse(Call<ClosingDataModel> call, Response<ClosingDataModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {

                            if (response.body().getStatus() == 200) {
                                closingDataModel = response.body();
                                binding.setModel(closingDataModel.getData());

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
                    public void onFailure(Call<ClosingDataModel> call, Throwable t) {
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

    public void closing() {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .closing(closingDataModel.getData().getId() + "")
                .enqueue(new Callback<StatusResponse>() {
                    @Override
                    public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {

                            if (response.body().getStatus() == 200) {
                                //  closeSheet3();
                                finish();
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

}
