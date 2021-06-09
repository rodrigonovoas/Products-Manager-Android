package app.rodrigonovoa.myproductsmanager.ui.contacts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.lang.ref.WeakReference;
import java.util.List;

import app.rodrigonovoa.myproductsmanager.R;
import app.rodrigonovoa.myproductsmanager.database.Contact;
import app.rodrigonovoa.myproductsmanager.database.ProductsManagerDb;

public class ContactListActivity extends AppCompatActivity {

    private ProductsManagerDb database = null;
    private ListView lv_contacts;
    private Button btn_add, btn_back;

    @Override
    protected void onResume() {
        super.onResume();

        clearAndLoadListView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        lv_contacts = (ListView) findViewById(R.id.lv_contacts);
        btn_add = (Button) findViewById(R.id.btn_add_contact);
        btn_back = (Button) findViewById(R.id.btn_back);


        //get database ref

        database = ProductsManagerDb.getInstance(this);

        //views

        btn_add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddModifyContact.class);
                startActivity(intent);
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        new retrieveTask(this).execute();
    }

    private class retrieveTask extends AsyncTask<Void,Void, List<Contact>> {

        private WeakReference<ContactListActivity> activityReference;

        // only retain a weak reference to the activity
        retrieveTask(ContactListActivity context) {
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
                setUpAdapter(contacts);
            }
        }

    }

    public void setUpAdapter(List<Contact> contacts){
        ContactsListAdapter customAdapter = new ContactsListAdapter(this, R.layout.row_contact_list, contacts, database);

        lv_contacts.setAdapter(customAdapter);
    }

    public void clearAndLoadListView(){
        new retrieveTask(this).execute();
        lv_contacts.setAdapter(null);
    }

}