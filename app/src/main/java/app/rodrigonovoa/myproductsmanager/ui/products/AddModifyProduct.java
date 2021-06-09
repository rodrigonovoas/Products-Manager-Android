package app.rodrigonovoa.myproductsmanager.ui.products;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.util.List;

import app.rodrigonovoa.myproductsmanager.R;
import app.rodrigonovoa.myproductsmanager.common.Utils;
import app.rodrigonovoa.myproductsmanager.database.Product;
import app.rodrigonovoa.myproductsmanager.database.ProductDAO;
import app.rodrigonovoa.myproductsmanager.database.ProductsManagerDb;

import static app.rodrigonovoa.myproductsmanager.ui.contacts.AddModifyContact.PICK_IMAGE;

public class AddModifyProduct extends AppCompatActivity {


    static EditText edt_name;
    static EditText edt_salePrice;
    static EditText edt_purchasePrice;
    static EditText edt_notes;
    Button btn_add, btn_back, btn_image;
    ImageView imv_product;

    List<Product> products_list;

    int value = -1;

    ProductsManagerDb database;

    String image_path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_modify_product);

        edt_name = (EditText) findViewById(R.id.edt_name);
        edt_salePrice = (EditText) findViewById(R.id.edt_sale_price);
        edt_purchasePrice = (EditText) findViewById(R.id.edt_purchase_price);
        edt_notes = (EditText) findViewById(R.id.edt_product_notations);

        btn_add = findViewById(R.id.btn_add_product);
        btn_back = findViewById(R.id.btn_back);
        btn_image = findViewById(R.id.btn_add_image);

        imv_product = findViewById(R.id.imv_product);

        //get db
        database = ProductsManagerDb.getInstance(this);


        //get activity parameter

        Bundle b = getIntent().getExtras();

        if(b != null){
            value = b.getInt("ID");
        }

        if(value > 0){
            new getProductTask(database.productDAO()).execute(value);
        }


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(value == -1){
                    addProduct();
                }else{
                    Product product = getNewProduct();
                    new updateTask(database.productDAO()).execute(product);
                    finish();
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    public void addProduct() {

        if (edt_name.getText().toString().equalsIgnoreCase("")) {
            edt_name.requestFocus();
            edt_name.setError("Enter name");
            return;
        }

        if (edt_salePrice.getText().toString().equalsIgnoreCase("")) {
            edt_salePrice.requestFocus();
            edt_salePrice.setError("Enter sale price");
            return;
        }

        insertData();
    }

    private Product getNewProduct(){
        Product product = new Product();

        if(value > 0){
            product.setProductid(value);
        }

        product.setName(edt_name.getText().toString());
        product.setRegistrationdate(Utils.getInstance().currentDateToTimestamp());
        product.setSaleprice(Float.valueOf(edt_salePrice.getText().toString()));


        if(edt_purchasePrice.getText().toString().equalsIgnoreCase("")){
            product.setPurchaseprice(0);
        }else{
            product.setPurchaseprice(Float.valueOf(edt_purchasePrice.getText().toString()));
        }

        product.setImagepath(image_path);
        product.setNotations(edt_notes.getText().toString());

        return product;
    }

    //se insertan los datos tras comprobar que el producto no existía previamente
    public void insertData() {
        Product product = getNewProduct();

        new insertTask(database.productDAO()).execute(product);

        finish();
    }

    private static class insertTask extends AsyncTask<Product, Void, Void> {
        private ProductDAO mProductDAO;

        insertTask(ProductDAO dao) {
            mProductDAO = dao;
        }

        @Override
        protected Void doInBackground(final Product... params) {
            mProductDAO.insertProduct(params[0]);
            Log.d(getClass().getSimpleName(), "add product at background");
            return null;
        }
    }

    private static class updateTask extends AsyncTask<Product, Void, Void> {
        private ProductDAO mProductDAO;

        updateTask(ProductDAO dao) {
            mProductDAO = dao;
        }

        @Override
        protected Void doInBackground(final Product... params) {
            mProductDAO.updateProduct(params[0]);
            Log.d(getClass().getSimpleName(), "contact updated");

            return null;
        }
    }

    private static class getProductTask extends AsyncTask<Integer, Void, Void> {
        private ProductDAO mProductDAO;

        getProductTask(ProductDAO dao) {
            mProductDAO = dao;
        }

        @Override
        protected Void doInBackground(Integer... position) {
            Product product;

            product = mProductDAO.getProductById(position[0].intValue());

            fillWithProductData(product);
            Log.d(getClass().getSimpleName(), "get contact at background");

            return null;
        }
    }

    private static void fillWithProductData(Product product){
        edt_name.setText(product.getName());
        edt_salePrice.setText(String.valueOf(product.getSaleprice()));
        edt_purchasePrice.setText(String.valueOf(product.getPurchaseprice()));
        edt_notes.setText(product.getNotations());
    }

    public void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccione una Imagen"), PICK_IMAGE);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            image_path = getPath(this, selectedImageUri);

            if (image_path.equalsIgnoreCase("") == false) {
                File image_file = new File(image_path);
                if (image_file.exists()) {
                    imv_product.setImageBitmap(BitmapFactory.decodeFile(image_file.toString()));
                }
            } else {
                imv_product.setImageBitmap(BitmapFactory.decodeFile(""));
            }

        }
    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}