package app.rodrigonovoa.myproductsmanager.ui.operations;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import app.rodrigonovoa.myproductsmanager.common.Utils;
import app.rodrigonovoa.myproductsmanager.R;
import app.rodrigonovoa.myproductsmanager.database.Contact;
import app.rodrigonovoa.myproductsmanager.database.ContactDAO;
import app.rodrigonovoa.myproductsmanager.database.Operation;
import app.rodrigonovoa.myproductsmanager.database.OperationDAO;
import app.rodrigonovoa.myproductsmanager.database.Product;
import app.rodrigonovoa.myproductsmanager.database.ProductDAO;
import app.rodrigonovoa.myproductsmanager.database.ProductsManagerDb;
import app.rodrigonovoa.myproductsmanager.ui.products.AddModifyProduct;

public class AddModifyOperation extends AppCompatActivity {

    private Context mContext;
    static Spinner sp_products;
    static Spinner sp_type;
    static Spinner sp_contacts;
    static EditText edt_qty;
    static EditText edt_notes;
    static TextView tv_price;
    static TextView tv_cost;
    static TextView tv_contact_type_title;
    Button btn_add, btn_back;

    private Boolean sale_type = true;

    static CountDownLatch signal = new CountDownLatch(2);

    private static String contact_type = "";

    int value = -1;

    List<Contact> contacts_list = new ArrayList<Contact>();
    static List<Product> products_list = null;

    static List<Integer> productsid_list = new ArrayList<Integer>();
    static List<Integer> contactsid_list = new ArrayList<Integer>();

    private ProductsManagerDb database;

    float precio_provisional = 0, importe_provisional = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_modify_operation);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder(); //con el StrictMode permite acceder al fichero creado (copia).
        StrictMode.setVmPolicy(builder.build());

        mContext = this;
        edt_qty = (EditText) findViewById(R.id.edt_qty);
        edt_notes = (EditText) findViewById(R.id.edt_notes);
        tv_price = (TextView) findViewById(R.id.tv_price);
        tv_cost = (TextView) findViewById(R.id.tv_cost);
        tv_contact_type_title = (TextView) findViewById(R.id.tv_contact_title);
        sp_products = (Spinner) findViewById(R.id.sp_products);
        sp_type = (Spinner) findViewById(R.id.sp_type);
        sp_contacts = (Spinner) findViewById(R.id.sp_contacts);

        contact_type = getString(R.string.add_contacts_client);

        btn_add = findViewById(R.id.btn_add);
        btn_back = findViewById(R.id.btn_back);

        //get db
        database = ProductsManagerDb.getInstance(this);

        loadTypesSpinner();
        //loadContactsSpinner();
        loadProductsSpinner();

        //get activity parameter

        Bundle b = getIntent().getExtras();

        if(b != null){
            value = b.getInt("ID");
        }

        if(value > 0){
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    new getOperationTask(database.operationDAO()).execute(value);
                }
            }, 1500);

        }

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(value > 0){
                    Operation newOperation = getNewOperation();
                    new updateTask(database.operationDAO()).execute(newOperation);
                    finish();
                }else{
                    if (edt_qty.getText().toString().equalsIgnoreCase("0")) {
                        Toast.makeText(mContext, mContext.getString(R.string.operation_add_qty_warning_msg), Toast.LENGTH_SHORT).show();
                    } else {
                        addOperation();
                    }
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        sp_products.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                tv_price.setText(String.valueOf(loadProvisionalPrice(position)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // código
            }

        });

        sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String sale = getString(R.string.operation_spinner_sale);

                if (sp_type.getSelectedItem().toString().equalsIgnoreCase(sale)) {
                    sale_type = true;
                    contact_type = getString(R.string.add_contacts_client);
                    tv_contact_type_title.setText(getString(R.string.add_contacts_client));
                } else {
                    sale_type = false;
                    contact_type = getString(R.string.add_contacts_provider);
                    tv_contact_type_title.setText(getString(R.string.add_contacts_provider));
                }

                contacts_list = new ArrayList<Contact>();
                contactsid_list = new ArrayList<Integer>();
                sp_contacts.setAdapter(null);
                loadContactsSpinner();
                tv_price.setText(String.valueOf(loadProvisionalPrice(sp_products.getSelectedItemPosition())));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // código
            }

        });

        edt_qty.setText("0");


        edt_qty.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                loadProvisionalCost();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });

    }


    private void fillWithOperationData(Operation operation){

        runOnUiThread(new Runnable() {
            public void run() {
                edt_qty.setText(String.valueOf(operation.getQuantity()));
                edt_notes.setText(operation.getNotations());
                tv_price.setText(String.valueOf(operation.getPrice()));
                tv_cost.setText(String.valueOf(operation.getCost()));

                precio_provisional = operation.getPrice();
                importe_provisional = operation.getCost();

                try {
                    signal.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int product_position = 0;
                int contacts_position = 0;

                product_position = productsid_list.indexOf(operation.productid);
                contacts_position = contactsid_list.indexOf(operation.contactid);


                String sale = sp_type.getContext().getString(R.string.operation_spinner_sale);
                int type_position = 0;

                if(operation.getType().equalsIgnoreCase(sale)){
                    type_position = 0;
                }else{
                    type_position = 1;
                }

                int finalType_position = type_position;
                sp_type.post(new Runnable() {
                    @Override
                    public void run() {
                        sp_type.setSelection(finalType_position);
                    }
                });

                int finalProduct_position = product_position;
                sp_products.post(new Runnable() {
                    @Override
                    public void run() {
                        sp_products.setSelection(finalProduct_position);
                    }
                });

                int finalContacts_position = contacts_position;

                sp_contacts.post(new Runnable() {
                    @Override
                    public void run() {
                        sp_contacts.setSelection(finalContacts_position);
                    }
                });
            }
        });

        //setUpSpTypesListener(this);
    }

    private class getOperationTask extends AsyncTask<Integer, Void, Void> {
        private OperationDAO mOperationDAO;

        getOperationTask(OperationDAO dao) {
            mOperationDAO = dao;
        }

        @Override
        protected Void doInBackground(Integer... position) {
            Operation operation;

            operation = mOperationDAO.getOperationById(position[0].intValue());

            fillWithOperationData(operation);

            Log.d(getClass().getSimpleName(), "get operation at background");

            return null;
        }
    }

    private class retrieveContactsTask extends AsyncTask<Void, Void, List<Contact>> {

        private WeakReference<AddModifyOperation> activityReference;

        // only retain a weak reference to the activity
        retrieveContactsTask(AddModifyOperation context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected List<Contact> doInBackground(Void... voids) {
            if (activityReference.get() != null) {
                return activityReference.get().database.contactDAO().getContactsByType(contact_type);
            } else
                return null;
        }

        @Override
        protected void onPostExecute(List<Contact> contacts) {

            if (contacts != null && contacts.size() > 0) {
                contacts_list = contacts;

                contacts.add(null);
                for(int i=0;i<contacts.size();i++){
                    if(contacts.get(i)==null){
                        contactsid_list.add(0);
                    }else{
                        contactsid_list.add(contacts.get(i).contactid);
                    }
                }

                String[] arraySpinner = null;

                List<String> contactList = new ArrayList<String>();

                for(int i=0;i<contacts.size();i++){
                    if(contacts.get(i)==null){
                        contactList.add("");
                    }else{
                        contactList.add(contacts.get(i).getName());
                    }
                }

                if(contactList.size() > 0){
                    arraySpinner = contactList.toArray(new String[0]);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                        R.layout.spinner_item, arraySpinner);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_contacts.setAdapter(adapter);
            }

            signal.countDown();
        }
    }

    private class retrieveProductsTask extends AsyncTask<Void, Void, List<Product>> {

        private WeakReference<AddModifyOperation> activityReference;

        // only retain a weak reference to the activity
        retrieveProductsTask(AddModifyOperation context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected List<Product> doInBackground(Void... voids) {
            if (activityReference.get() != null) {
                return activityReference.get().database.productDAO().getAllProducts();
            } else
                return null;
        }

        @Override
        protected void onPostExecute(List<Product> products) {

            if (products != null && products.size() > 0) {
                products_list = products;

                for(int i=0;i<products.size();i++){
                    productsid_list.add(products.get(i).productid);
                }

                String[] arraySpinner = null;

                List<String> productList = new ArrayList<String>();

                for(int i=0;i<products.size();i++){
                    productList.add(products.get(i).getName());
                }

                if(productList.size() > 0){
                    arraySpinner = productList.toArray(new String[0]);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                        R.layout.spinner_item, arraySpinner);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_products.setAdapter(adapter);
            }

            signal.countDown();
        }
    }

    private static class updateTask extends AsyncTask<Operation, Void, Void> {
        private OperationDAO mOperationDAO;

        updateTask(OperationDAO dao) {
            mOperationDAO = dao;
        }

        @Override
        protected Void doInBackground(final Operation... params) {
            mOperationDAO.updateOperation(params[0]);
            Log.d(getClass().getSimpleName(), "operation updated");

            return null;
        }
    }

    public void addOperation() {
        if (edt_qty.getText().toString().equalsIgnoreCase("")) {
            edt_qty.requestFocus();
            edt_qty.setError("Introduce la cantidad.");
        } else if (sp_products.getSelectedItem().toString().equalsIgnoreCase("")) {
            sp_products.requestFocus();
        } else {
            insertarDatos();
        }
    }

    public void insertarDatos() {
        Operation newOperation = getNewOperation();
        new inserOperationtTask(database.operationDAO()).execute(newOperation);

        finish();
    }

    private Operation getNewOperation(){
        Operation newOperation = new Operation();

        if(contacts_list.size() > 0 && contacts_list.get(sp_contacts.getSelectedItemPosition()) != null){
            Contact addedContact = contacts_list.get(sp_contacts.getSelectedItemPosition());
            newOperation.setContactid(addedContact.getContactid());
        }else{
            newOperation.setContactid(null);
        }

        Product addedProduct = products_list.get(sp_products.getSelectedItemPosition());

        newOperation.setCost(importe_provisional);
        newOperation.setRegistrationdate(Utils.getInstance().currentDateToTimestamp());
        newOperation.setPrice(precio_provisional);
        newOperation.setProductid(addedProduct.getProductid());
        newOperation.setName(addedProduct.getName());
        newOperation.setNotations(edt_notes.getText().toString());
        newOperation.setQuantity(Integer.valueOf(edt_qty.getText().toString()));
        newOperation.setType(sp_type.getSelectedItem().toString());

        return newOperation;
    }

    private static class inserOperationtTask extends AsyncTask<Operation, Void, Void> {
        private OperationDAO mOperationDAO;

        inserOperationtTask(OperationDAO dao) {
            mOperationDAO = dao;
        }

        @Override
        protected Void doInBackground(final Operation... params) {
            mOperationDAO.insertOperation(params[0]);
            Log.d(getClass().getSimpleName(), "add operation at background");

            return null;
        }
    }

    public void loadTypesSpinner(){
        String[] arraySpinner = null;

        arraySpinner = new String[]{
                getString(R.string.operation_spinner_sale), getString(R.string.operation_spinnder_purchase)
        };


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_type.setAdapter(adapter);
    }

    public void loadProductsSpinner() {
        new retrieveProductsTask(this).execute();
    }


    public void loadContactsSpinner() {
        new retrieveContactsTask(this).execute();
    }

    public float loadProvisionalPrice(int position) {
        float price = 0.0f;
        if (position >= 0){

            if(sale_type){
                price = products_list.get(position).getSaleprice();
            }else{
                price = products_list.get(position).getPurchaseprice();
            }

            precio_provisional = price;
        }

        return price;
    }

    public void loadProvisionalCost() {
        String valor_formateado;
        float calculo, cantidad;

        if (edt_qty.getText().toString().equalsIgnoreCase("")) {
            cantidad = 0;
        } else {
            cantidad = Float.valueOf(edt_qty.getText().toString());
        }


        calculo = precio_provisional * cantidad;

        importe_provisional = calculo;
        valor_formateado = String.format("%,.2f", calculo);

        tv_cost.setText(valor_formateado);
    }

    }