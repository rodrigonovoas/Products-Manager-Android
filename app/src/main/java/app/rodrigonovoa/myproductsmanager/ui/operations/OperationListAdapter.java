package app.rodrigonovoa.myproductsmanager.ui.operations;

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

import app.rodrigonovoa.myproductsmanager.R;
import app.rodrigonovoa.myproductsmanager.database.Operation;
import app.rodrigonovoa.myproductsmanager.database.OperationDAO;
import app.rodrigonovoa.myproductsmanager.database.Product;
import app.rodrigonovoa.myproductsmanager.database.ProductDAO;
import app.rodrigonovoa.myproductsmanager.database.ProductsManagerDb;
import app.rodrigonovoa.myproductsmanager.ui.products.AddModifyProduct;
import app.rodrigonovoa.myproductsmanager.ui.products.ProductActivity;
import app.rodrigonovoa.myproductsmanager.ui.products.ProductListActivity;

public class OperationListAdapter extends ArrayAdapter<Operation> {
    private List<Operation> operation_list;
    private int resourceLayout;
    private Context mContext;
    private ProductsManagerDb database;

    public OperationListAdapter(Context context, int resource, List<Operation> items, ProductsManagerDb db) {
        super(context, resource, items);
        this.operation_list = items;
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
    public Operation getItem(int position) {
        return operation_list.get(position);
    }

    @Override
    public int getCount() {
        return operation_list.size();
    }

    @Override
    public long getItemId(int position) {
        return operation_list.get(position).getOperationid();
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

        Operation operation = getItem(position);

        if (operation != null) {
            Context context = v.getContext();
            LinearLayout ll_row = (LinearLayout) v.findViewById(R.id.ll_row);
            TextView tv_name = (TextView) v.findViewById(R.id.tv_row_content);
            ImageView imv_options = (ImageView) v.findViewById(R.id.imv_options);
            ImageView imv_type = (ImageView) v.findViewById(R.id.imv_operation_type);

            if(operation.getType().equalsIgnoreCase(getContext().getString(R.string.operation_spinnder_purchase))){
                imv_type.setImageResource(R.drawable.operation_row_purchase_icon);
            }else{
                imv_type.setImageResource(R.drawable.operation_row_sale_icon);
            }


            ll_row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, OperationActivity.class);
                    intent.putExtra("ID", operation.operationid);
                    context.startActivity(intent);
                }
            });

            imv_options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openOptionsDialog(v.getContext(), operation);
                }
            });

            tv_name.setText(operation.getName() + " | " + context.getString(R.string.operation_info_price) + ": " + String.valueOf(operation.getPrice()) + " | " + context.getString(R.string.operation_info_qty) + ": " +
                    String.valueOf(operation.getQuantity()) + " | " + context.getString(R.string.operation_info_cost) + ": "  + String.valueOf(operation.getCost()));
        }

        return v;
    }

    private void openOptionsDialog(Context context, Operation operation) {

        AlertDialog.Builder dialog;

        dialog =  new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.contact_list_dialog));
        dialog.setTitle(context.getString(R.string.contact_list_dialog_title));

        dialog.setPositiveButton(context.getString(R.string.contact_list_dialog_option3), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, AddModifyOperation.class);
                intent.putExtra("ID", operation.operationid);
                context.startActivity(intent);
            }
        });

        dialog.setNeutralButton(context.getString(R.string.contact_list_dialog_option2), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                new deleteTask(database.operationDAO()).execute(operation);

                if (mContext instanceof OperationListActivity) {
                    ((OperationListActivity)mContext).reloadList();
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

    private static class deleteTask extends AsyncTask<Operation, Void, Void> {
        private OperationDAO mOperatinoDAO;

        deleteTask(OperationDAO dao) {
            mOperatinoDAO = dao;
        }

        @Override
        protected Void doInBackground(final Operation... params) {
            mOperatinoDAO.deleteOperation(params[0]);
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

