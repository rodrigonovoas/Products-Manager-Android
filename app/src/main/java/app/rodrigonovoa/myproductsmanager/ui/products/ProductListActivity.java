package app.rodrigonovoa.myproductsmanager.ui.products;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import java.lang.ref.WeakReference;
import java.util.List;

import app.rodrigonovoa.myproductsmanager.R;
import app.rodrigonovoa.myproductsmanager.database.Contact;
import app.rodrigonovoa.myproductsmanager.database.Product;
import app.rodrigonovoa.myproductsmanager.database.ProductsManagerDb;
import app.rodrigonovoa.myproductsmanager.ui.contacts.AddModifyContact;
import app.rodrigonovoa.myproductsmanager.ui.contacts.ContactListActivity;
import app.rodrigonovoa.myproductsmanager.ui.contacts.ContactsListAdapter;

public class ProductListActivity extends AppCompatActivity {

    private ProductsManagerDb database;
    private ListView lv_products;
    private ImageView btn_add, btn_back;

    @Override
    protected void onResume() {
        super.onResume();

        clearAndLoadListView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        lv_products = (ListView) findViewById(R.id.lv_products_list);
        btn_add = (ImageView) findViewById(R.id.btn_add_product);
        btn_back = (ImageView) findViewById(R.id.btn_back);

        //get db

        database = ProductsManagerDb.getInstance(this);

        //views

        btn_add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddModifyProduct.class);
                startActivity(intent);
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        clearAndLoadListView();
    }

    private class retrieveTask extends AsyncTask<Void,Void, List<Product>> {

        private WeakReference<ProductListActivity> activityReference;

        // only retain a weak reference to the activity
        retrieveTask(ProductListActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected List<Product> doInBackground(Void... voids) {
            if (activityReference.get()!=null)
                return activityReference.get().database.productDAO().getAllProducts();
            else
                return null;
        }

        @Override
        protected void onPostExecute(List<Product> products) {
            if (products!=null && products.size()>0 ){
                setUpAdapter(products);
            }
        }

    }

    public void setUpAdapter(List<Product> products){
        ProductListAdapter customAdapter = new ProductListAdapter(this, R.layout.row_product_list, products, database);

        lv_products.setAdapter(customAdapter);
    }

    public void clearAndLoadListView(){
        lv_products.setAdapter(null);

        new retrieveTask(this).execute();
    }
}