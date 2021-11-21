package app.rodrigonovoa.myproductsmanager.ui.backup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import app.rodrigonovoa.myproductsmanager.R;
import app.rodrigonovoa.myproductsmanager.common.Utils;
import app.rodrigonovoa.myproductsmanager.database.Contact;
import app.rodrigonovoa.myproductsmanager.database.Operation;
import app.rodrigonovoa.myproductsmanager.database.Product;
import app.rodrigonovoa.myproductsmanager.database.ProductsManagerDb;

public class BackUpActivity extends AppCompatActivity {

    private ProductsManagerDb database;
    private String bigText = "";
    private ImageView imv_backup, imv_back;
    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_up);

        //get db
        database = ProductsManagerDb.getInstance(this);

        utils = Utils.getInstance();

        imv_backup = findViewById(R.id.imv_backup);
        imv_back = findViewById(R.id.imv_back);

        new retrieveProductsTask(this).execute();
        new retrieveContactsTask(this).execute();
        new retrieveOperationsTask(this).execute();

        imv_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTxtBackup(BackUpActivity.this,bigText);
            }
        });

        imv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void createTxtBackup(Context context, String sBody) {
        try {
            File f_pdf = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/products_manager_backup_" + utils.currentDateToTimestamp().toString() + ".txt");
            FileWriter writer = new FileWriter(f_pdf);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(context, "Backup saved at Downloads folder", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class retrieveContactsTask extends AsyncTask<Void,Void, List<Contact>> {

        private WeakReference<BackUpActivity> activityReference;

        // only retain a weak reference to the activity
        retrieveContactsTask(BackUpActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected List<Contact> doInBackground(Void... voids) {
            if (activityReference.get()!=null)
                return activityReference.get().database.contactDAO().getAllContacts();
            else
                return null;
        }

        @Override
        protected void onPostExecute(List<Contact> contacts) {
            if (contacts!=null && contacts.size()>0 ){
                bigText += "\n" + "----- CONTACTS -----";

                for (int i = 0; i < contacts.size(); i++) {
                    bigText += "\n" + "ID: " + contacts.get(i).getContactid() + " Name: " + contacts.get(i).getName() + " Type: " + contacts.get(i).getType() + " Origin: " + contacts.get(i).getOrigin() + " Notations: "
                    + contacts.get(i).getNotations();
                }
            }

        }

    }

    private class retrieveOperationsTask extends AsyncTask<Void,Void, List<Operation>> {

        private WeakReference<BackUpActivity> activityReference;

        // only retain a weak reference to the activity
        retrieveOperationsTask(BackUpActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected List<Operation> doInBackground(Void... voids) {
            if (activityReference.get()!=null){
                return activityReference.get().database.operationDAO().getAllOperations();
            }
            else
                return null;
        }

        @Override
        protected void onPostExecute(List<Operation> operations) {
            if (operations != null && operations.size() > 0) {
                bigText += "\n" + "----- OPERATIONS -----";

                for (int i = 0; i < operations.size(); i++) {
                    bigText += "\n" + " Name: " + operations.get(i).getName() + " Type: " + operations.get(i).getType() + " Price: " + operations.get(i).getPrice() + " Qty: " + operations.get(i).getQuantity() + " Cost: " + operations.get(i).getCost() + " ContactID: " + operations.get(i).getContactid() + " Notations: " + operations.get(i).getNotations();
                }
            }
        }

    }

    private class retrieveProductsTask extends AsyncTask<Void,Void, List<Product>> {

        private WeakReference<BackUpActivity> activityReference;

        // only retain a weak reference to the activity
        retrieveProductsTask(BackUpActivity context) {
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
            if (products != null && products.size() > 0) {
                bigText += "\n" + "----- PRODUCTS -----";

                for (int i = 0; i < products.size(); i++) {
                    bigText += "\n" + "Name: " + products.get(i).getName() + " Purchase Price: " + products.get(i).getPurchaseprice() + " Sale Price: " + products.get(i).getSaleprice() + " Notations: " + products.get(i).getNotations();
                }
            }
        }

    }

}