package app.rodrigonovoa.myproductsmanager.ui.contacts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import app.rodrigonovoa.myproductsmanager.R;
import app.rodrigonovoa.myproductsmanager.common.Utils;
import app.rodrigonovoa.myproductsmanager.database.Contact;
import app.rodrigonovoa.myproductsmanager.database.ContactDAO;
import app.rodrigonovoa.myproductsmanager.database.ProductsManagerDb;

public class AddModifyContact extends AppCompatActivity {

    public static final int PICK_IMAGE = 1;

    static EditText etName;
    static EditText etOrigin;
    static EditText etNotations;
    static Spinner spContactsType;
    ImageView imvContact;
    Button bAdd, bBack, bAddImage;

    int value = -1;

    ProductsManagerDb database;

    String image_path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_modify_contact);

        //Views
        imvContact = (ImageView) findViewById(R.id.imvContact);
        etName = (EditText) findViewById(R.id.edt_name);
        spContactsType = (Spinner) findViewById(R.id.sp_contact_type);
        etOrigin = (EditText) findViewById(R.id.edt_origin);
        etNotations = (EditText) findViewById(R.id.edt_notations);

        bAdd = findViewById(R.id.btn_add);
        bBack = findViewById(R.id.btn_back);
        bAddImage = findViewById(R.id.btn_add_photo);

        //get activity parameter

        Bundle b = getIntent().getExtras();

        if(b != null){
            value = b.getInt("ID");
        }

        //Fill spinner with options
        String[] arraySpinner = null;

        arraySpinner = new String[]{
                getString(R.string.add_contacts_client), getString(R.string.add_contacts_provider)
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spContactsType.setAdapter(adapter);

        //get db
        database = ProductsManagerDb.getInstance(this);

        //new retrieveTask(this).execute();

        if(value > 0){
            new getContactTask(database.contactDAO()).execute(value);
        }

        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(value == -1){
                    addContact();
                }else{
                    Contact contact = getNewContact();
                    new updateTask(database.contactDAO()).execute(contact);
                    finish();
                }
            }
        });

        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imvContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        bAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

    }

    private static void loadSpinner(String type, Context context){
        if (type.equalsIgnoreCase(context.getString(R.string.add_contacts_client))) {
            spContactsType.setSelection(0);
        } else {
            spContactsType.setSelection(1);
        }
    }

    public void addContact() {

        if (etName.getText().toString().equalsIgnoreCase("")) {
            etName.requestFocus();
            etName.setError("Enter name");
            return;
        }

        if (spContactsType.getSelectedItem().toString().equalsIgnoreCase("")) {
            spContactsType.requestFocus();
            Toast.makeText(this, "Enter contact type", Toast.LENGTH_SHORT).show();
            return;
        }

        insertData();
    }

    private Contact getNewContact(){
        Contact contact = new Contact();

        if(value > 0){
            contact.setContactid(value);
        }

        contact.setName(etName.getText().toString());
        contact.setType(spContactsType.getSelectedItem().toString());
        contact.setOrigin(etOrigin.getText().toString());
        contact.setNotations(etNotations.getText().toString());
        contact.setRegistrationdate(Utils.getInstance().currentDateToTimestamp());
        contact.setImagepath(image_path);

        return contact;
    }


    private void insertData() {

        Contact contact = getNewContact();

        new insertTask(database.contactDAO()).execute(contact);

        Toast.makeText(this,"Contact added",Toast.LENGTH_SHORT).show();

        finish();
    }

    /*
    private static class retrieveTask extends AsyncTask<Void,Void, List<Contact>> {

        private WeakReference<AddModifyContact> activityReference;

        // only retain a weak reference to the activity
        retrieveTask(AddModifyContact context) {
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
                activityReference.get().contacts_list = contacts;
            }
        }

    }
     */

    private static class insertTask extends AsyncTask<Contact, Void, Void> {
        private ContactDAO mContactDAO;

        insertTask(ContactDAO dao) {
            mContactDAO = dao;
        }

        @Override
        protected Void doInBackground(final Contact... params) {
            mContactDAO.insertContact(params[0]);
            Log.d(getClass().getSimpleName(), "add contact at background");

            return null;
        }
    }

    private static class updateTask extends AsyncTask<Contact, Void, Void> {
        private ContactDAO mContactDAO;

        updateTask(ContactDAO dao) {
            mContactDAO = dao;
        }

        @Override
        protected Void doInBackground(final Contact... params) {
            mContactDAO.updatecontact(params[0]);
            Log.d(getClass().getSimpleName(), "add contact at background");

            return null;
        }
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

            fillWithContactData(contact);
            Log.d(getClass().getSimpleName(), "get contact at background");

            return null;
        }
    }

    private static void fillWithContactData(Contact contact){
        loadSpinner(contact.getType(), etName.getContext());
        etName.setText(contact.getName());
        etOrigin.setText(contact.getOrigin());
        etNotations.setText(contact.getNotations());
    }

    public void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccione una Imagen"), PICK_IMAGE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            image_path = getPath(this, selectedImageUri);

            if (image_path.equalsIgnoreCase("") == false) {
                File image_file = new File(image_path);
                if (image_file.exists()) {
                    imvContact.setImageBitmap(BitmapFactory.decodeFile(image_file.toString()));
                }
            } else {
                imvContact.setImageBitmap(BitmapFactory.decodeFile(""));
            }

        }
    }


    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}