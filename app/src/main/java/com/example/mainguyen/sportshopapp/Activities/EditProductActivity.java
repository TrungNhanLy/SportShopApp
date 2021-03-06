package com.example.mainguyen.sportshopapp.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mainguyen.sportshopapp.R;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.mainguyen.sportshopapp.App.AppController;
import com.example.mainguyen.sportshopapp.Title.BaseActivity;
import com.example.mainguyen.sportshopapp.Utils.Common;
import com.example.mainguyen.sportshopapp.Utils.ImageUtils;
import com.example.mainguyen.sportshopapp.Utils.RealPathUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

public class EditProductActivity extends BaseActivity{

    private static final String TAG = EditProductActivity.class.getSimpleName();

    private static final int REQUEST_CAMERA = 1001;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_ACCESS_EXTERNAL_STORAGE_PERMISSION = 2;
    private static final int REQUEST_GALLERY = 1002;

    private static final String KEY_DEP_IID = "productId";
    private static final String KEY_DEP_NAME = "productName";
    private static final String KEY_DEP_QUANTITY = "quantity";
    private static final String KEY_DEP_PRICE = "price";
    private static final String KEY_DEP_DATE = "dateUpdate";
    private static final String KEY_DEP_SIZE = "size";
    private static final String KEY_DEP_STATUS = "status";
    private static final String KEY_DEP_DESC = "description";
    private static final String KEY_DEP_IMAGE= "imageFile";
    private static final String KEY_DEP_IMAGE_NAME= "imageName";



    private static String url_edit_product = Common.API_SERVER_IP+"api/product/edit";
    TextView productId;
    EditText productName;
    EditText quantity;
    EditText price;
    EditText date;
    Spinner status;
    Spinner size;
    EditText description;
    ImageView iv_image;
    Button btn_camera;
    Button btnEdit;
    // Button btnDeleteDepartment;
    private Uri selectedImageUri;
    private final CharSequence[] items = {"Take Photo", "From Gallery"};
    private Bitmap mBitmap;
    private String realPath = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        btnEdit = (Button) findViewById(R.id.btnEditProduct);
        productId=(TextView) findViewById(R.id.txtIDPR);
        productName = (EditText) findViewById(R.id.proName);
        quantity = (EditText) findViewById(R.id.proQuantity);
        price = (EditText) findViewById(R.id.proPrice);
        date = (EditText) findViewById(R.id.proDate);
        status = (Spinner) findViewById(R.id.proType);
        size = (Spinner) findViewById(R.id.proSize);
        description = (EditText) findViewById(R.id.proDescription);
        iv_image = (ImageView) findViewById(R.id.iv_images);
        btn_camera = (Button) findViewById(R.id.btn_camera);
        // btnDeleteDepartment = (Button) findViewById(R.id.btnDeleteDepartment);

        Intent item = getIntent();
        productId.setText(item.getStringExtra(KEY_DEP_IID));
        productName.setText(item.getStringExtra(KEY_DEP_NAME));
        quantity.setText(item.getStringExtra(KEY_DEP_QUANTITY));
        price.setText(item.getStringExtra(KEY_DEP_PRICE));
        date.setText(item.getStringExtra(KEY_DEP_DATE));
        description.setText(item.getStringExtra(KEY_DEP_DESC));

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooserDialog();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executePostDataToServerToUpdate();
               // executePostDataToServerToUpdate(valueIdPr,valueNamePr,valueQuantityPr, valueStatusPr, valueSizePr, valuePricePr,valueDatePr,valueDescriptionPr,fileName);
                //ProgressDialog pDialog = new ProgressDialog(getApplicationContext());
            }
        });
        if (selectedImageUri == null) {
            btnEdit.setClickable(false);
        }
    }

private void executePostDataToServerToUpdate(){

        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_edit_product,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        //Toast.makeText(EditProductActivity.this, s, Toast.LENGTH_LONG).show();
                        Toast.makeText(EditProductActivity.this, "Update Information Of Product Success!", Toast.LENGTH_SHORT).show();
                        Intent showProductActivity = new Intent(getApplicationContext(), ProductActivity.class);
                        startActivity(showProductActivity);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();
                        //Showing toast
                        Toast.makeText(EditProductActivity.this, "Update information of product unsuccess", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Creating parameters
                String image = Common.getStringImage(mBitmap);
                File file = new File(realPath);
                String fileName = file.getName();
                String valueIdPr = productId.getText().toString().trim();
                String valueNamePr = productName.getText().toString().trim();
                String valueQuantityPr = quantity.getText().toString().trim();
                String valueStatusPr = status.getSelectedItem().toString().trim();
                String valueSizePr = size.getSelectedItem().toString().trim();
                String valuePricePr = price.getText().toString().trim();
                String valueDatePr = date.getText().toString().trim();
                String valueDescriptionPr = description.getText().toString().trim();
                String status;
                String size;
                if(valueStatusPr.equals("Club Clothes")){
                    status="0";
                }else if(valueStatusPr.equals("National Clothes")){
                    status="1";
                }else{
                    status="2";
                }
                switch(valueSizePr){
                    case "S":
                        size="1";
                        break;
                    case "M":
                        size="2";
                        break;
                    case "L":
                        size="3";
                        break;
                    case "XL":
                        size="4";
                        break;
                    case "XXL":
                        size="5";
                        break;
                    case "XXXL":
                        size="6";
                        break;
                    case "1":
                        size="7";
                        break;
                    case "2":
                        size="8";
                        break;
                    case "3":
                        size="9";
                        break;
                    case "4":
                        size="10";
                        break;
                    case "5":
                        size="11";
                        break;
                    case "32":
                        size="12";
                        break;
                    case "33":
                        size="13";
                        break;
                    case "34":
                        size="14";
                        break;
                    case "35":
                        size="15";
                        break;
                    case "36":
                        size="16";
                        break;
                    case "37":
                        size="17";
                        break;
                    case "38":
                        size="18";
                        break;
                    case "39":
                        size="19";
                        break;
                    case "40":
                        size="20";
                        break;
                    case "41":
                        size="21";
                        break;
                    case "42":
                        size="22";
                        break;
                    case "43":
                        size="23";
                        break;
                    case "44":
                        size="24";
                        break;
                    case "45":
                        size="25";
                        break;
                    default:
                        size="null";
                        break;

                }
                Log.v("Status",status);
                Log.v("Size",size);
                Map<String, String> params = new Hashtable<String, String>();
               // Intent intent = getIntent();
                //Adding parameters
                params.put(KEY_DEP_IID, valueIdPr);
                params.put(KEY_DEP_NAME, valueNamePr);
                params.put(KEY_DEP_QUANTITY, valueQuantityPr);
                params.put(KEY_DEP_PRICE, valuePricePr);
                params.put(KEY_DEP_DATE, valueDatePr);
                params.put(KEY_DEP_STATUS, status);
                params.put(KEY_DEP_DESC, valueDescriptionPr);
                params.put(KEY_DEP_SIZE, size);
                params.put(KEY_DEP_IMAGE_NAME, fileName);
                params.put(KEY_DEP_IMAGE, image);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    /**
     * Finally i convert the image in base64 and send to server how a String and in my Server reconvert to image.
     */


    private void openFileChooserDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("Add Picture");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        initCameraPermission();
                        break;
                    case 1:
                        initGalleryPermission();
                        break;
                    default:
                }
            }
        });
        builder.show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void initGalleryPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Permission to access EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_ACCESS_EXTERNAL_STORAGE_PERMISSION);
        } else {
            initGalleryIntent();
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void initCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                Toast.makeText(this, "Permission to use Camera", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            initCameraIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initCameraIntent();
            } else {
                Toast.makeText(this, "Permission denied by user", Toast.LENGTH_SHORT).show();
            }
        }if (requestCode == REQUEST_ACCESS_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initGalleryIntent();
            } else {
                Toast.makeText(this, "Permission denied by user", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    private void initGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    private void initCameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = getOutputMediafile(1);
        selectedImageUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CAMERA);
        }
    }

    private File getOutputMediafile(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), getResources().getString(R.string.app_name)
        );
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyHHdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == 1) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".png");
        } else {
            return null;
        }

        return mediaFile;
    }


    @Override
    @SuppressLint("NewApi")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                realPath = selectedImageUri.getPath();
            } else if (requestCode == REQUEST_GALLERY) {
                selectedImageUri = data.getData();
                if (Build.VERSION.SDK_INT < 11) {
                    realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.getData());
                } else if (Build.VERSION.SDK_INT < 19) {
                    realPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());
                } else {
                    realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());
                }

                Log.d(TAG, "real path: " + realPath);
            }
            mBitmap = ImageUtils.getScaledImage(selectedImageUri, this);
            setImageBitmap(mBitmap);
        }

    }

    private void setImageBitmap(Bitmap bm) {
        iv_image.setImageBitmap(bm);
        btnEdit.setClickable(true);

    }
}
