package app.rodrigonovoa.myproductsmanager.ui.products;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;
import java.util.List;

import app.rodrigonovoa.myproductsmanager.R;
import app.rodrigonovoa.myproductsmanager.database.Contact;
import app.rodrigonovoa.myproductsmanager.database.ContactDAO;
import app.rodrigonovoa.myproductsmanager.database.Product;
import app.rodrigonovoa.myproductsmanager.database.ProductDAO;
import app.rodrigonovoa.myproductsmanager.database.ProductsManagerDb;
import app.rodrigonovoa.myproductsmanager.ui.contacts.AddModifyContact;
import app.rodrigonovoa.myproductsmanager.ui.contacts.ContactListActivity;
import app.rodrigonovoa.myproductsmanager.ui.contacts.ContactsListAdapter;

public class ProductListAdapter extends ArrayAdapter<Product> {
    private List<Product> product_list;
    private int resourceLayout;
    private Context mContext;
    private ProductsManagerDb database;

    public ProductListAdapter(Context context, int resource, List<Product> items, ProductsManagerDb db) {
        super(context, resource, items);
        this.product_list = items;
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
    public Product getItem(int position) {
        return product_list.get(position);
    }

    @Override
    public int getCount() {
        return product_list.size();
    }

    @Override
    public long getItemId(int position) {
        return product_list.get(position).getProductid();
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

        Product product = getItem(position);

        if (product != null) {
            LinearLayout ll_row = (LinearLayout) v.findViewById(R.id.ll_row);
            TextView tv_name = (TextView) v.findViewById(R.id.tv_row_content);
            ImageView imv_options = (ImageView) v.findViewById(R.id.imv_options);


            ll_row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ProductActivity.class);
                    intent.putExtra("ID", product.productid);
                    context.startActivity(intent);
                }
            });

            imv_options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openOptionsDialog(v.getContext(), product);
                }
            });

            tv_name.setText(product.getName());
        }

        return v;
    }

    private void openOptionsDialog(Context context, Product product) {

        AlertDialog.Builder dialog;

        dialog =  new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.contact_list_dialog));
        dialog.setTitle(context.getString(R.string.contact_list_dialog_title));

        dialog.setPositiveButton(context.getString(R.string.contact_list_dialog_option3), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, AddModifyProduct.class);
                intent.putExtra("ID", product.productid);
                context.startActivity(intent);
            }
        });

        dialog.setNeutralButton(context.getString(R.string.contact_list_dialog_option2), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                new deleteTask(database.productDAO()).execute(product);

                if (mContext instanceof ProductListActivity) {
                    ((ProductListActivity)mContext).clearAndLoadListView();
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

    private static class deleteTask extends AsyncTask<Product, Void, Void> {
        private ProductDAO mProductDAO;

        deleteTask(ProductDAO dao) {
            mProductDAO = dao;
        }

        @Override
        protected Void doInBackground(final Product... params) {
            mProductDAO.deleteProduct(params[0]);
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
