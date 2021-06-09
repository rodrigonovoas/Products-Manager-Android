package app.rodrigonovoa.myproductsmanager.ui.contacts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import app.rodrigonovoa.myproductsmanager.R;
import app.rodrigonovoa.myproductsmanager.database.Contact;
import app.rodrigonovoa.myproductsmanager.database.ContactDAO;
import app.rodrigonovoa.myproductsmanager.database.ProductsManagerDb;
import app.rodrigonovoa.myproductsmanager.ui.products.AddModifyProduct;

/*
CustomerAdapter para personalizar la ListView, con sus respectivos métodos
 */
public class ContactsListAdapter extends ArrayAdapter<Contact> {
    private List<Contact> contact_list;
    private int resourceLayout;
    private Context mContext;
    private ProductsManagerDb database;

    public ContactsListAdapter(Context context, int resource, List<Contact> items, ProductsManagerDb db) {
        super(context, resource, items);
        this.contact_list = items;
        this.resourceLayout = resource;
        this.mContext = context;
        this.database = db;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public Contact getItem(int position) {
        return contact_list.get(position);
    }

    @Override
    public int getCount() {
        return contact_list.size();
    }

    @Override
    public long getItemId(int position) {
        return contact_list.get(position).getContactid();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(resourceLayout, null);
        }

        Contact contact = getItem(position);

        if (contact != null) {
            LinearLayout ll_row = (LinearLayout) v.findViewById(R.id.ll_row);
            TextView tv_name = (TextView) v.findViewById(R.id.tv_row_title);
            ImageView imv_options = (ImageView) v.findViewById(R.id.imv_options);
            ImageView imv_type = (ImageView) v.findViewById(R.id.imv_contact_type);

            if(contact.getType().equalsIgnoreCase(getContext().getString(R.string.add_contacts_client))){
                imv_type.setImageResource(R.drawable.contacts_client_icon);
            }else{
                imv_type.setImageResource(R.drawable.contacts_provider_icon);
            }

            imv_options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openOptionsDialog(v.getContext(), contact);
                }
            });

            ll_row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ContactActivity.class);
                    intent.putExtra("ID", contact.contactid);
                    context.startActivity(intent);
                }
            });

            tv_name.setText(contact.getName());
        }

        return v;
    }

    private void openOptionsDialog(Context context, Contact contact) {

        AlertDialog.Builder dialog;

        dialog =  new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.contact_list_dialog));
        dialog.setTitle(context.getString(R.string.contact_list_dialog_title));

        dialog.setPositiveButton(context.getString(R.string.contact_list_dialog_option3), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, AddModifyContact.class);
                intent.putExtra("ID", contact.contactid);
                context.startActivity(intent);
            }
        });

        dialog.setNeutralButton(context.getString(R.string.contact_list_dialog_option2), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                new deleteTask(database.contactDAO()).execute(contact);

                if (mContext instanceof ContactListActivity) {
                    ((ContactListActivity)mContext).clearAndLoadListView();
                }

                Toast.makeText(context, context.getString(R.string.common_record_deleted), Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        dialog.setNegativeButton(context.getString(R.string.contact_list_dialog_option1), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private static class deleteTask extends AsyncTask<Contact, Void, Void> {
        private ContactDAO mContactDAO;

        deleteTask(ContactDAO dao) {
            mContactDAO = dao;
        }

        @Override
        protected Void doInBackground(final Contact... params) {
            mContactDAO.deleteContact(params[0]);
            Log.d(getClass().getSimpleName(), "delete contact at background");

            return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

}
