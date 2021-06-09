package app.rodrigonovoa.myproductsmanager.ui.operations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.sqlite.db.SimpleSQLiteQuery;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.List;
import app.rodrigonovoa.myproductsmanager.R;
import app.rodrigonovoa.myproductsmanager.common.Utils;
import app.rodrigonovoa.myproductsmanager.database.Operation;
import app.rodrigonovoa.myproductsmanager.database.ProductsManagerDb;
public class OperationListActivity extends AppCompatActivity {

    private static ProductsManagerDb database;

    static Context mContext;
    static Spinner sp_types;
    static TextView tv_total, tv_from, tv_to;
    static ImageView imv_from, imv_to, imv_clear, imv_filter;
    static ListView lv_operations;
    static Button btn_add;
    static Calendar cal;

    private static String fromdate, todate;

    private static Long fromdate_num = 0L , todate_num = 0L;

    static DatePickerDialog datePickerDialog;

    @Override
    protected void onResume() {
        super.onResume();

        clearAndReloadList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation_list);

        //get db
        database = ProductsManagerDb.getInstance(this);

        cal = Calendar.getInstance();

        mContext = this;

        fromdate = "";
        todate = "";

        lv_operations = findViewById(R.id.lv_operations);

        tv_total = findViewById(R.id.tv_total_amount);
        tv_from = findViewById(R.id.tv_from_date);
        tv_to = findViewById(R.id.tv_to_date);

        imv_from = findViewById(R.id.imv_from);
        imv_to = findViewById(R.id.imv_to);
        imv_clear = findViewById(R.id.imv_clear);
        imv_filter = findViewById(R.id.imv_filter);

        btn_add = findViewById(R.id.btn_add);
        sp_types = findViewById(R.id.sp_types);

        String[] arraySpinner = null;

        arraySpinner = new String[]{
                getString(R.string.operation_spinner_sale), getString(R.string.operation_spinnder_purchase), getString(R.string.operation_spinner_all)
        };


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_types.setAdapter(adapter);


        new retrieveTask(this).execute();


        imv_from.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectFromDate();
            }
        });

        imv_to.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectToDate();
            }
        });

        imv_clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearDates();
            }
        });

        imv_filter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                lv_operations.setAdapter(null);
                new retrieveTask(OperationListActivity.this).execute();
            }
        });


        btn_add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddModifyOperation.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onRestart() {
        //setup and clear list
        super.onRestart();
    }

    private void selectToDate(){
        Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if (month < 10) {
                    if (dayOfMonth < 10) {
                        todate = "0" + String.valueOf(dayOfMonth) + "-0" + String.valueOf(month + 1) + "-" + String.valueOf(year);
                    } else {
                        todate = String.valueOf(dayOfMonth) + "-0" + String.valueOf(month + 1) + "-" + String.valueOf(year);
                    }
                } else {
                    if (dayOfMonth < 10) {
                        todate = "0" + String.valueOf(dayOfMonth) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(year);
                    } else {
                        todate = String.valueOf(dayOfMonth) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(year);
                    }
                }

                todate_num = Utils.getInstance().fromDateToTimestamp(todate);

                tv_to.setText(todate);
            }
        }
                , mYear, mMonth, mDay);
        datePickerDialog.show();
        tv_to.setError(null);
    }

    private void selectFromDate(){
        Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if (month < 10) {
                    if (dayOfMonth < 10) {
                        fromdate = "0" + String.valueOf(dayOfMonth) + "-0" + String.valueOf(month + 1) + "-" + String.valueOf(year);
                    } else {
                        fromdate = String.valueOf(dayOfMonth) + "-0" + String.valueOf(month + 1) + "-" + String.valueOf(year);
                    }
                } else {
                    if (dayOfMonth < 10) {
                        fromdate = "0" + String.valueOf(dayOfMonth) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(year);
                    } else {
                        fromdate = String.valueOf(dayOfMonth) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(year);
                    }
                }

                fromdate_num = Utils.getInstance().fromDateToTimestamp(fromdate);
                tv_from.setText(fromdate);
            }
        }
                , mYear, mMonth, mDay);
        datePickerDialog.show();
        tv_from.setError(null);
    }


    private class retrieveTask extends AsyncTask<Void,Void, List<Operation>> {

        private WeakReference<OperationListActivity> activityReference;

        // only retain a weak reference to the activity
        retrieveTask(OperationListActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected List<Operation> doInBackground(Void... voids) {
            if (activityReference.get()!=null){

                SimpleSQLiteQuery query = new SimpleSQLiteQuery(createSqlQuery());

                return activityReference.get().database.operationDAO().getOperationsByConditions(query);
            }
            else
                return null;
        }

        @Override
        protected void onPostExecute(List<Operation> operations) {
            if (operations!=null && operations.size()>0 ){
                setUpAdapterAndClear(operations);
                setTotalAmount(operations);
            }
        }

    }

    private static void setUpAdapterAndClear(List<Operation> operations){

        //clearing variables


        lv_operations.setAdapter(null);
        clearDates();

        //setting adapter

        OperationListAdapter customAdapter = new OperationListAdapter(mContext, R.layout.row_operation_list, operations, database);

        lv_operations.setAdapter(customAdapter);
    }

    private static String createSqlQuery(){
        String query = "";

        String sale = mContext.getString(R.string.operation_spinner_sale);
        String purchase = mContext.getString(R.string.operation_spinnder_purchase);

        query = "select * from operation";


        if (fromdate_num == 0) {
            query = "SELECT * FROM operation where registration_date <= " + todate_num + "";
        } else if (todate_num == 0) {
            query = "SELECT * FROM operation where registration_date >= " + fromdate_num + "";
        }


        if ((fromdate_num == 0) && (todate_num == 0)) {
            query = "select * from operation";
        } else if (((fromdate_num == 0) == false) && ((todate_num == 0) == false)) {
            query = "SELECT * FROM operation where registration_date >= " + fromdate_num + " and registration_date <= " + todate_num + "";
        }


        if (query.equalsIgnoreCase("select * from operation")) {
            if (sp_types.getSelectedItem().toString().equalsIgnoreCase(sale)) {
                query += " where type = '" + sale + "'";
            } else if (sp_types.getSelectedItem().toString().equalsIgnoreCase(purchase)) {
                query += " where type = '" + purchase + "'";
            }
        } else {
            if (sp_types.getSelectedItem().toString().equalsIgnoreCase(sale)) {
                query += " and type = '" + sale + "'";
            } else if (sp_types.getSelectedItem().toString().equalsIgnoreCase(purchase)) {
                query += " and type = '" + purchase + "'";
            }
        }

        return query;
    }


    private static void setTotalAmount(List<Operation> operations){
        float total = 0.0f;

        for(int i=0;i<operations.size();i++){
            total += operations.get(i).cost;
        }

        if (total != 0) {
            String valor_formateado = "";
            valor_formateado = String.format("%,.2f", total);

            tv_total.setText(valor_formateado); //pongo el texto del total, que es la suma de los importe de las lineas
        } else {
            tv_total.setText("");
        }
    }


    public void volver(View v) {
        finish();
    }

    public static void clearDates(){
        fromdate = "";
        todate = "";

        tv_from.setText("");
        tv_to.setText("");
    }

    public void clearAndReloadList(){
        lv_operations.setAdapter(null);
        clearDates();
        new retrieveTask(OperationListActivity.this).execute();
    }

    public void reloadList(){
        new retrieveTask(OperationListActivity.this).execute();
    }

}