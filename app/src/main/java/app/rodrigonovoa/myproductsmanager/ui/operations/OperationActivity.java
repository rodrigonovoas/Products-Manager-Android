package app.rodrigonovoa.myproductsmanager.ui.operations;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import app.rodrigonovoa.myproductsmanager.R;
import app.rodrigonovoa.myproductsmanager.common.Utils;
import app.rodrigonovoa.myproductsmanager.database.Contact;
import app.rodrigonovoa.myproductsmanager.database.ContactDAO;
import app.rodrigonovoa.myproductsmanager.database.Operation;
import app.rodrigonovoa.myproductsmanager.database.OperationDAO;
import app.rodrigonovoa.myproductsmanager.database.Product;
import app.rodrigonovoa.myproductsmanager.database.ProductDAO;
import app.rodrigonovoa.myproductsmanager.database.ProductsManagerDb;
import app.rodrigonovoa.myproductsmanager.ui.contacts.ContactActivity;

public class OperationActivity extends AppCompatActivity {

    static TextView tv_date;
    static TextView tv_type;
    static TextView tv_qty;
    static TextView tv_price;
    static TextView tv_cost;
    static TextView tv_product;
    static TextView tv_notes;
    static TextView tv_contact;

    Bundle extras;
    int operacionID = 0;

    static ProductsManagerDb database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation);

        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_type = (TextView) findViewById(R.id.tv_type);
        tv_qty = (TextView) findViewById(R.id.tv_qty);
        tv_price = (TextView) findViewById(R.id.tv_price);
        tv_cost = (TextView) findViewById(R.id.tv_cost);
        tv_product = (TextView) findViewById(R.id.tv_product);
        tv_contact = (TextView) findViewById(R.id.tv_contact);
        tv_notes = (TextView) findViewById(R.id.tv_notes);

        Intent intent = getIntent();
        extras = intent.getExtras();

        operacionID = (Integer) extras.get("ID");

        //get db
        database = ProductsManagerDb.getInstance(this);

        if(operacionID > 0){
            new getOperationTask(database.operationDAO()).execute(operacionID);
        }
    }

    private static class getOperationTask extends AsyncTask<Integer, Void, Void> {
        private OperationDAO mOperationDAO;

        getOperationTask(OperationDAO dao) {
            mOperationDAO = dao;
        }

        @Override
        protected Void doInBackground(Integer... position) {
            Operation operation;

            operation = mOperationDAO.getOperationById(position[0].intValue());

            loadOperation(operation);
            Log.d(getClass().getSimpleName(), "get contact at background");

            return null;
        }
    }

    private static class getProductByIdTask extends AsyncTask<Integer, Void, Void> {
        private ProductDAO mProductDAO;

        getProductByIdTask(ProductDAO dao) {
            mProductDAO = dao;
        }

        @Override
        protected Void doInBackground(Integer... position) {
            Product product;

            product = mProductDAO.getProductById(position[0].intValue());

            tv_product.setText(product.getName());

            return null;
        }
    }

    private static class getContactByIdTask extends AsyncTask<Integer, Void, Void> {
        private ContactDAO mContactDAO;

        getContactByIdTask(ContactDAO dao) {
            mContactDAO = dao;
        }

        @Override
        protected Void doInBackground(Integer... position) {
            Contact contact;

            contact = mContactDAO.getContactById(position[0].intValue());

            tv_contact.setText(contact.getName());

            return null;
        }
    }



    private static void loadOperation(Operation operation){
        tv_date.setText(Utils.getInstance().fromTimestampToDateString(operation.getRegistrationdate()));
        tv_type.setText(operation.getType());

        if(operation.getQuantity() > 0){
            tv_qty.setText(String.valueOf(operation.getQuantity()));
        }else{
            tv_qty.setText("0");
        }

        if(operation.getPrice() > 0){
            tv_price.setText(String.valueOf(operation.getPrice()));
        }else{
            tv_price.setText("0");
        }

        if(operation.getCost() > 0){
            tv_cost.setText(String.valueOf(operation.getCost()));
        }else{
            tv_cost.setText("0");
        }

        tv_notes.setText(operation.getNotations());

        if(operation.getProductid() > 0){
            new getProductByIdTask(database.productDAO()).execute(operation.getProductid());
        }

        if(operation.getContactid() != null && operation.getContactid() > 0){
            new getContactByIdTask(database.contactDAO()).execute(operation.getContactid());
        }
    }
}