package app.rodrigonovoa.myproductsmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import app.rodrigonovoa.myproductsmanager.common.Utils;
import app.rodrigonovoa.myproductsmanager.ui.backup.BackUpActivity;
import app.rodrigonovoa.myproductsmanager.ui.contacts.ContactListActivity;
import app.rodrigonovoa.myproductsmanager.ui.operations.AddModifyOperation;
import app.rodrigonovoa.myproductsmanager.ui.operations.OperationListActivity;
import app.rodrigonovoa.myproductsmanager.ui.products.ProductListActivity;
import app.rodrigonovoa.myproductsmanager.ui.todoList.TodoListActivity;

public class MainActivity extends AppCompatActivity {

    LinearLayout ll_operations;
    LinearLayout ll_clients_prov;
    LinearLayout ll_products;
    LinearLayout ll_todo;
    LinearLayout ll_exit;
    LinearLayout ll_backup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ll_operations = findViewById(R.id.ll_sales_purchases);
        ll_clients_prov = findViewById(R.id.ll_cli_prov);
        ll_products = findViewById(R.id.ll_products);
        ll_todo = findViewById(R.id.ll_todo);
        ll_exit = findViewById(R.id.ll_exit);
        ll_backup = findViewById(R.id.ll_copies);

        verifyStoragePermissions(this);

        //Log.d("MainActivity", Utils.getInstance().fromDateToTimestamp("15-04-1997 12:00:00").toString());


        ll_operations.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OperationListActivity.class);
                startActivity(intent);
            }
        });

        ll_clients_prov.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ContactListActivity.class);
                startActivity(intent);
            }
        });

        ll_products.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProductListActivity.class);
                startActivity(intent);
            }
        });

        ll_backup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BackUpActivity.class);
                startActivity(intent);
            }
        });


        ll_todo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TodoListActivity.class);
                startActivity(intent);
            }
        });

        ll_exit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finishAffinity();
            }
        });
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}