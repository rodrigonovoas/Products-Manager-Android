package app.rodrigonovoa.myproductsmanager.ui.contacts;

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
import app.rodrigonovoa.myproductsmanager.database.ProductsManagerDb;

public class ContactActivity extends AppCompatActivity {

    ImageView imv_contact;
    static TextView tv_name;
    static TextView tv_type;
    static TextView tv_origin;
    static TextView tv_notes;
    Button btn_back;

    static String filePath = "";

    ProductsManagerDb database;

    Bundle extras;
    int contactID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        imv_contact = (ImageView) findViewById(R.id.imv_contact);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_type = (TextView) findViewById(R.id.tv_type);
        tv_origin = (TextView) findViewById(R.id.tv_origin);
        tv_notes = (TextView) findViewById(R.id.tv_notes);
        btn_back = findViewById(R.id.btn_back);

        //get db
        database = ProductsManagerDb.getInstance(this);

        Intent intent = getIntent();
        extras = intent.getExtras();

        contactID = (Integer) extras.get("ID");

        //cargarImagen();

        if(contactID > 0){
            new getContactTask(database.contactDAO()).execute(contactID);
        }

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public static void loadContact(Contact contact) {
        tv_name.setText(contact.getName());
        tv_type.setText(contact.getType());
        tv_origin.setText(contact.getOrigin());
        tv_notes.setText(contact.getNotations());
        filePath = contact.getImagepath();
    }

    private static class getContactTask extends AsyncTask<Integer, Void, Void> {
        private ContactDAO mContactDAO;

        getContactTask(ContactDAO dao) {
            mContactDAO = dao;
        }

        @Override
        protected Void doInBackground(Integer... position) {
            Contact contact;

            contact = mContactDAO.getContactById(position[0].intValue());

            loadContact(contact);
            Log.d(getClass().getSimpleName(), "get contact at background");

            return null;
        }
    }


    public void cargarImagen() {
        if (filePath != null && filePath.equalsIgnoreCase("") == false) {
            File image_file = new File(filePath);
            if (image_file.exists()) {
                imv_contact.setImageBitmap(BitmapFactory.decodeFile(image_file.toString()));
            } else {
                imv_contact.setImageResource(R.drawable.contacts_question);
            }
        } else {
            imv_contact.setImageResource(R.drawable.contacts_question);
        }
    }

}