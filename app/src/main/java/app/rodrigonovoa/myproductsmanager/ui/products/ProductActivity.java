package app.rodrigonovoa.myproductsmanager.ui.products;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import app.rodrigonovoa.myproductsmanager.R;
import app.rodrigonovoa.myproductsmanager.database.Contact;
import app.rodrigonovoa.myproductsmanager.database.ContactDAO;
import app.rodrigonovoa.myproductsmanager.database.Product;
import app.rodrigonovoa.myproductsmanager.database.ProductDAO;
import app.rodrigonovoa.myproductsmanager.database.ProductsManagerDb;
import app.rodrigonovoa.myproductsmanager.ui.contacts.ContactActivity;

public class ProductActivity extends AppCompatActivity {

    ImageView imv_product;
    static TextView tv_name;
    static TextView tv_sale_price;
    static TextView tv_purchase_price;
    static TextView tv_notes;
    Button btn_back;

    static String filePath = "";

    ProductsManagerDb database;

    Bundle extras;
    int productID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        imv_product = (ImageView) findViewById(R.id.imv_contact);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_sale_price = (TextView) findViewById(R.id.tv_sale_price);
        tv_purchase_price = (TextView) findViewById(R.id.tv_purchase_price);
        tv_notes = (TextView) findViewById(R.id.tv_notes);
        btn_back = findViewById(R.id.btn_back);

        //get db
        database = ProductsManagerDb.getInstance(this);

        Intent intent = getIntent();
        extras = intent.getExtras();

        productID = (Integer) extras.get("ID");

        //cargarImagen();

        if(productID > 0){
            new getProductTask(database.productDAO()).execute(productID);
        }

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public static void loadProduct(Product product) {
        tv_name.setText(product.getName());
        tv_purchase_price.setText(String.valueOf(product.getPurchaseprice()));
        tv_sale_price.setText(String.valueOf(product.getSaleprice()));
        tv_notes.setText(product.getNotations());
        filePath = product.getImagepath();
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

            loadProduct(product);
            Log.d(getClass().getSimpleName(), "get product at background");

            return null;
        }
    }


    public void cargarImagen() {
        if (filePath != null && filePath.equalsIgnoreCase("") == false) {
            File image_file = new File(filePath);
            if (image_file.exists()) {
                imv_product.setImageBitmap(BitmapFactory.decodeFile(image_file.toString()));
            } else {
                imv_product.setImageResource(R.drawable.contacts_question);
            }
        } else {
            imv_product.setImageResource(R.drawable.contacts_question);
        }
    }
}