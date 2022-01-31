package com.socialcafe.activities_fragments.activity_invoice;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;

import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.exoplayer2.util.Log;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.socialcafe.R;
import com.socialcafe.adapters.PdfGenerator;
import com.socialcafe.adapters.PdfGeneratorListener;
import com.socialcafe.adapters.ProductBillAdapter;
import com.socialcafe.databinding.ActivityInvoiceBinding;
import com.socialcafe.language.Language;
import com.socialcafe.models.FailureResponse;
import com.socialcafe.models.InvoiceDataModel;
import com.socialcafe.models.PdfDocumentAdpter;
import com.socialcafe.models.SuccessResponse;
import com.socialcafe.models.UserModel;
import com.socialcafe.preferences.Preferences;
import com.socialcafe.remote.Api;
import com.socialcafe.share.Common;
import com.socialcafe.tags.Tags;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static android.os.Build.VERSION_CODES.KITKAT;

public class InvoiceActivity extends AppCompatActivity {
    private ActivityInvoiceBinding binding;
    private Preferences preferences;
    private String lang;
    private String salid;
    private UserModel userModel;
    private List<InvoiceDataModel.LimsProductSaleData> limsProductSaleDataList;
    private ProductBillAdapter productBillAdapter;
    private final String write_perm = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final int write_req = 100;
    //    private final String bluthoos_perm = Manifest.permission.BLUETOOTH;
//    private final String bluthoosadmin_perm = Manifest.permission.BLUETOOTH_ADMIN;
//
//    private final int bluthoos_req = 200;
//
    private boolean isPermissionGranted = false;
    private Image image;
    private Context context;

    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        this.context = newBase;
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_invoice);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        salid = intent.getStringExtra("data");
    }

    @RequiresApi(api = KITKAT)
    private void checkWritePermission() {

        if (ContextCompat.checkSelfPermission(this, write_perm) != PackageManager.PERMISSION_GRANTED) {


            isPermissionGranted = false;

            ActivityCompat.requestPermissions(this, new String[]{write_perm}, write_req);


        } else {
            printpdf();
          //  takeScreenshot(2);
            isPermissionGranted = true;

        }
    }

    @RequiresApi(api = KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == write_req && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //takeScreenshot(2);
            printpdf();
        }
    }


    private void initView() {
        limsProductSaleDataList = new ArrayList<>();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        productBillAdapter = new ProductBillAdapter(limsProductSaleDataList, this);
        binding.recView.setNestedScrollingEnabled(false);
        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        binding.recView.setAdapter(productBillAdapter);
        getlastInvoice();
        binding.btnConfirm3.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = KITKAT)
            @Override
            public void onClick(View view) {
                checkWritePermission();
            }
        });
    }

    public void getlastInvoice() {
        Log.e("kdkkd", salid + " " + userModel.getUser().getId());
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();


        Api.getService(Tags.base_url)
                .getInv(salid, userModel.getUser().getId() + "")
                .enqueue(new Callback<InvoiceDataModel>() {
                    @Override
                    public void onResponse(Call<InvoiceDataModel> call, Response<InvoiceDataModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().getStatus() == 200) {
                                if (response.body() != null) {
                                    updateData(response.body());

//                                    Intent intent = new Intent(HomeActivity.this, InvoiceActivity.class);
//                                    intent.putExtra("data", response.body().getData());
//                                    startActivity(intent);
                                } else if (response.body().getStatus() == 400) {
                                    Toast.makeText(InvoiceActivity.this, getResources().getString(R.string.no_invoice), Toast.LENGTH_SHORT).show();

                                }

                            }

                        } else {
                            if (response.code() == 500) {
                                Toast.makeText(InvoiceActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("ERROR", response.message() + "");

                                //     Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }


                        }
                    }

                    @Override
                    public void onFailure(Call<InvoiceDataModel> call, Throwable t) {
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

    private void updateData(InvoiceDataModel body) {
      //  Log.e("Dldlldl", body.getQr_code());
        binding.setModel(body);
        if (body.getLims_product_sale_data() != null && body.getLims_product_sale_data().size() > 0) {
            limsProductSaleDataList.addAll(body.getLims_product_sale_data());
            productBillAdapter.notifyDataSetChanged();
         //   Log.e("dkdkdk", limsProductSaleDataList.size() + "");
//      if(limsProductSaleDataList.size()>3){
//          LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, 100);
//          lp.weight = 1;
//          binding.fl.setLayoutParams(lp);
//
//      }
        }

    }

    private void takeScreenshot(int mode) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
                mPath = getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/" + now + ".jpeg";
            } else {
                mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpeg";
            }
            Log.e("kdkkdkd", mPath);
            // create bitmap screen capture
            NestedScrollView v1 = getWindow().getDecorView().findViewById(R.id.scrollView);
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = getBitmapFromView(v1, v1.getChildAt(0).getHeight(), v1.getChildAt(0).getWidth());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

           // MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Screen", "screen");

            //setting screenshot in imageview
            String filePath = imageFile.getPath();
            //android.util.Log.e("ddlldld", filePath);

            if (Build.VERSION.SDK_INT >= KITKAT)
                convertPDF(bitmap);

            //sendData(filePath);
            //printPhoto(FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider",new File(filePath)));

//   Bitmap ssbitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

        } catch (Exception e) {
            // Several error may come out with file handling or DOM
            android.util.Log.e("ddlldld", e.toString());
        }
    }

    private Bitmap getBitmapFromView(View view, int height, int width) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return bitmap;
    }

    //    public void openBT(ScanResult scanResult) throws IOException {
//        try {
//            dialog2.dismiss();
//
//
//            // Standard SerialPortService ID
//            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
//            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
//            mmSocket.connect();
//            mmOutputStream = mmSocket.getOutputStream();
//            inputStream = mmSocket.getInputStream();
//
//            beginListenForData();
//
//             myLabel.setText("Bluetooth Opened");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /*
//     * after opening a connection to bluetooth printer device,
//     * we have to listen and check if a data were sent to be printed.
//     */
//    void sendData(String strPath) throws IOException {
//
//
//        Bitmap imageBit = BitmapFactory.decodeFile(strPath);
//
//        ByteArrayOutputStream blob = new ByteArrayOutputStream();
//        imageBit.compress(Bitmap.CompressFormat.PNG, 0, blob);
//        byte[] bitmapdata = blob.toByteArray();
//
//     //   binding.image.setImageBitmap(imageBit);
//
//        findBT();
//
//     //   mmOutputStream.write(bitmapdata);
//        // tell the user data were sent
//        //  myLabel.setText("Data Sent");
//
//    }
//
//    void beginListenForData() {
////        try {
////            final Handler handler = new Handler();
////
////            // this is the ASCII code for a newline character
////            final byte delimiter = 10;
////
////            stopWorker = false;
////            readBufferPosition = 0;
////            readBuffer = new byte[1024];
////
////            workerThread = new Thread(new Runnable() {
////                public void run() {
////
////                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {
////
////                        try {
////
////                            int bytesAvailable = inputStream.available();
////
////                            if (bytesAvailable > 0) {
////
////                                byte[] packetBytes = new byte[bytesAvailable];
////                                inputStream.read(packetBytes);
////
////                                for (int i = 0; i < bytesAvailable; i++) {
////
////                                    byte b = packetBytes[i];
////                                    if (b == delimiter) {
////
////                                        byte[] encodedBytes = new byte[readBufferPosition];
////                                        System.arraycopy(
////                                                readBuffer, 0,
////                                                encodedBytes, 0,
////                                                encodedBytes.length
////                                        );
////
////                                        // specify US-ASCII encoding
////                                        final String data = new String(encodedBytes, "US-ASCII");
////                                        readBufferPosition = 0;
////
////                                        // tell the user data were sent to bluetooth printer device
////                                        handler.post(new Runnable() {
////                                            public void run() {
////                                                // myLabel.setText(data);
////                                            }
////                                        });
////
////                                    } else {
////                                        readBuffer[readBufferPosition++] = b;
////                                    }
////                                }
////                            }
////
////                        } catch (IOException ex) {
////                            stopWorker = true;
////                        }
////
////                    }
////                }
////            });
////
////            workerThread.start();
////
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//    }
////    void closeBT() throws IOException {
////        try {
////            stopWorker = true;
////            mmOutputStream.close();
////            mmInputStream.close();
////            mmSocket.close();
////            myLabel.setText("Bluetooth Closed");
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////    }
//private void checkWifi(){
//    IntentFilter filter = new IntentFilter();
//    filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
//    final WifiManager wifiManager =
//            (WifiManager)this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);;
//    registerReceiver(new BroadcastReceiver(){
//        @Override
//        public void onReceive(Context arg0, Intent arg1) {
//            // TODO Auto-generated method stub
//            Log.d("wifi","Open Wifimanager");
//
//            String scanList = wifiManager.getScanResults().toString();
//            Log.d("wifi","Scan:"+scanList);
//        }
//    },filter);
//    wifiManager.startScan();
//}
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void convertPDF(Bitmap bitmap) {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels ;
        float width = displaymetrics.widthPixels ;
        String FILE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
            FILE = getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/FirstPdf.pdf";
        } else {
            FILE = Environment.getExternalStorageDirectory().toString() + "/FirstPdf.pdf";
        }
        PdfDocument document = new PdfDocument();
        //  document.open();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder( bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#ffffff"));
        //canvas.drawPaint(paint);
        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

        canvas.drawBitmap(bitmap, 0, 0, null);

        document.finishPage(page);
        //  document.add(new Paragraph("My Heading"));
        File pdfFile = new File(FILE);

        try {

            document.writeTo(new FileOutputStream(pdfFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //printpdf();

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void printpdf() {

        PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
        try {
            String file;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
                file = getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/";
            } else {
                file = Environment.getExternalStorageDirectory().toString() + "/";
            }

            PdfGenerator.getBuilder()
                    .setContext(this)
                    .fromViewSource()
                    .fromView(binding.getRoot().findViewById(R.id.scrollView))
                    /* "fromLayoutXML()" takes array of layout resources.
                     * You can also invoke "fromLayoutXMLList()" method here which takes list of layout resources instead of array. */
                    //.setPageSize(PdfGenerator.PageSize.A4)
                    /* It takes default page size like A4,A5. You can also set custom page size in pixel
                     * by calling ".setCustomPageSize(int widthInPX, int heightInPX)" here. */
                    .setFileName("FirstPdf")
                    .setFolderName(file)
                    /* It is folder name. If you set the folder name like this pattern (FolderA/FolderB/FolderC), then
                     * FolderA creates first.Then FolderB inside FolderB and also FolderC inside the FolderB and finally
                     * the pdf file named "Test-PDF.pdf" will be store inside the FolderB. */
                    .openPDFafterGeneration(true)
                    /* It true then the generated pdf will be shown after generated. */
                    .build(new PdfGeneratorListener() {
                        @Override
                        public void onFailure(FailureResponse failureResponse) {
                            super.onFailure(failureResponse);
                            Log.d(TAG, "onFailure: " + failureResponse.getErrorMessage());
                            /* If pdf is not generated by an error then you will findout the reason behind it
                             * from this FailureResponse. */
                            //Toast.makeText(MainActivity.this, "Failure : "+failureResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
                           // Toast.makeText(getContext(), "" + failureResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void showLog(String log) {
                            super.showLog(log);
                            Log.d(TAG, "log: " + log);
                            /*It shows logs of events inside the pdf generation process*/
                        }

                        @Override
                        public void onStartPDFGeneration() {

                        }

                        @Override
                        public void onFinishPDFGeneration() {

                        }

                        @Override
                        public void onSuccess(SuccessResponse response) {
                            super.onSuccess(response);
                            /* If PDF is generated successfully then you will find SuccessResponse
                             * which holds the PdfDocument,File and path (where generated pdf is stored)*/
                            //Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                           // Log.d(TAG, "Success: " + response.getPath());
                            String file1=response.getPath();
                            PrintDocumentAdapter printDocumentAdapter = new PdfDocumentAdpter(context, file1);

                            printManager.print("Document", printDocumentAdapter, new PrintAttributes.Builder().build());
                        }
                    });

        } catch (Exception e) {
            android.util.Log.e("sssssss", e.getMessage());
        }
    }
}
