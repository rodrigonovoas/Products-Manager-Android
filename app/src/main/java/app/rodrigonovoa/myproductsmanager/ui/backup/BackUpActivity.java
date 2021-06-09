package app.rodrigonovoa.myproductsmanager.ui.backup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.sqlite.db.SimpleSQLiteQuery;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import app.rodrigonovoa.myproductsmanager.R;
import app.rodrigonovoa.myproductsmanager.database.Contact;
import app.rodrigonovoa.myproductsmanager.database.Operation;
import app.rodrigonovoa.myproductsmanager.database.Product;
import app.rodrigonovoa.myproductsmanager.database.ProductsManagerDb;
import app.rodrigonovoa.myproductsmanager.ui.contacts.ContactListActivity;
import app.rodrigonovoa.myproductsmanager.ui.operations.OperationListActivity;
import app.rodrigonovoa.myproductsmanager.ui.products.ProductListActivity;

public class BackUpActivity extends AppCompatActivity {

    private ProductsManagerDb database;
    private String bigText = "";
    private TextView tv_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_up);

        //get db
        database = ProductsManagerDb.getInstance(this);

        tv_main = findViewById(R.id.tv_content);

        new retrieveContactsTask(this).execute();
        new retrieveOperationsTask(this).execute();
        new retrieveProductsTask(this).execute();

        tv_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPdf(bigText);
            }
        });

    }


    private void createPdf(String sometext){
        // create a new document
        PdfDocument document = new PdfDocument();
        // crate a page description
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        // start a page
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawText(sometext, 80, 50, paint);

        document.finishPage(page);

        File f_pdf = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/testpdf777.pdf");

        try {
            document.writeTo(new FileOutputStream(f_pdf));
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e("main", "error "+e.toString());
            Toast.makeText(this, "Something wrong: " + e.toString(),  Toast.LENGTH_LONG).show();
        }
        // close the document
        document.close();
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
                bigText += "\n" + contacts.toString();
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
            if (operations!=null && operations.size()>0 ){
                bigText += "\n" + operations.toString();
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
            if (products!=null && products.size()>0 ){
                bigText += "\n" + products.toString();
            }
        }

    }
}