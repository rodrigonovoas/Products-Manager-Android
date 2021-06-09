package app.rodrigonovoa.myproductsmanager.ui.todoList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import app.rodrigonovoa.myproductsmanager.R;
import app.rodrigonovoa.myproductsmanager.database.Product;
import app.rodrigonovoa.myproductsmanager.database.ProductDAO;
import app.rodrigonovoa.myproductsmanager.database.ProductsManagerDb;
import app.rodrigonovoa.myproductsmanager.database.ToDo;
import app.rodrigonovoa.myproductsmanager.database.ToDoDAO;
import app.rodrigonovoa.myproductsmanager.ui.products.AddModifyProduct;
import app.rodrigonovoa.myproductsmanager.ui.products.ProductActivity;
import app.rodrigonovoa.myproductsmanager.ui.products.ProductListActivity;
import app.rodrigonovoa.myproductsmanager.ui.products.ProductListAdapter;

public class TodoListAdapter extends ArrayAdapter<ToDo> {
    private List<ToDo> todo_list;
    private int resourceLayout;
    private Context mContext;
    private ProductsManagerDb database;

    public TodoListAdapter(Context context, int resource, List<ToDo> items, ProductsManagerDb db) {
        super(context, resource, items);
        this.todo_list = items;
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
    public ToDo getItem(int position) {
        return todo_list.get(position);
    }

    @Override
    public int getCount() {
        return todo_list.size();
    }

    @Override
    public long getItemId(int position) {
        return todo_list.get(position).getTodoid();
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

        ToDo todo = getItem(position);

        if (todo != null) {
            LinearLayout ll_row = (LinearLayout) v.findViewById(R.id.ll_row);
            TextView tv_content = (TextView) v.findViewById(R.id.tv_row_content);
            CheckBox cb_done = (CheckBox) v.findViewById(R.id.cb_done);


            ll_row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDeleteDialog(v.getContext(),todo);
                }
            });

            cb_done.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ToDo newTodo = todo;
                    newTodo.setDone(isChecked);

                    if(isChecked){ ;
                        tv_content.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    }else{
                        tv_content.setPaintFlags(tv_content.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    }

                    new updateTask(database.todoDAO()).execute(todo);
                }
            }
            );

            tv_content.setText(todo.getConcept());

            if(todo.getDone()){
                cb_done.setChecked(true);
                tv_content.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            }else{
                tv_content.setPaintFlags(tv_content.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        }

        return v;
    }

    private void openDeleteDialog(Context context, ToDo todo) {
        AlertDialog.Builder dialog;

        dialog =  new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.contact_list_dialog));
        dialog.setTitle(context.getString(R.string.list_todo_dialog_title));

        dialog.setPositiveButton(context.getString(R.string.list_todo_dialog_option1), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new deleteTask(database.todoDAO()).execute(todo);

                if (mContext instanceof TodoListActivity) {
                    ((TodoListActivity)mContext).clearAndLoadListView();
                }
            }
        });


        dialog.setNegativeButton(context.getString(R.string.list_todo_dialog_option2), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialog.show();
    }



    private static class deleteTask extends AsyncTask<ToDo, Void, Void> {
        private ToDoDAO mTodoDAO;

        deleteTask(ToDoDAO dao) {
            mTodoDAO = dao;
        }

        @Override
        protected Void doInBackground(final ToDo... params) {
            mTodoDAO.deleteToDo(params[0]);
            Log.d(getClass().getSimpleName(), "delete todo at background");

            return null;
        }
    }

    private static class updateTask extends AsyncTask<ToDo, Void, Void> {
        private ToDoDAO mToDoDAO;

        updateTask(ToDoDAO dao) {
            mToDoDAO = dao;
        }

        @Override
        protected Void doInBackground(final ToDo... params) {
            mToDoDAO.updateToDo(params[0]);
            Log.d(getClass().getSimpleName(), "todo updated");

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
