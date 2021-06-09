package app.rodrigonovoa.myproductsmanager.ui.todoList;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.lang.ref.WeakReference;
import java.util.List;

import app.rodrigonovoa.myproductsmanager.R;
import app.rodrigonovoa.myproductsmanager.common.Utils;
import app.rodrigonovoa.myproductsmanager.database.Product;
import app.rodrigonovoa.myproductsmanager.database.ProductDAO;
import app.rodrigonovoa.myproductsmanager.database.ProductsManagerDb;
import app.rodrigonovoa.myproductsmanager.database.ToDo;
import app.rodrigonovoa.myproductsmanager.database.ToDoDAO;
import app.rodrigonovoa.myproductsmanager.ui.products.AddModifyProduct;
import app.rodrigonovoa.myproductsmanager.ui.products.ProductListActivity;
import app.rodrigonovoa.myproductsmanager.ui.products.ProductListAdapter;

public class TodoListActivity extends AppCompatActivity {

    private ProductsManagerDb database;
    private ListView lv_todo;
    private Button btn_back, btn_add;

    @Override
    protected void onResume() {
        super.onResume();

        clearAndLoadListView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);

        lv_todo = (ListView) findViewById(R.id.lv_todo_list);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_add = (Button) findViewById(R.id.btn_add);

        //get db

        database = ProductsManagerDb.getInstance(this);

        btn_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openAddDialog();
            }
        });

        clearAndLoadListView();
    }

    private class retrieveTask extends AsyncTask<Void,Void, List<ToDo>> {

        private WeakReference<TodoListActivity> activityReference;

        // only retain a weak reference to the activity
        retrieveTask(TodoListActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected List<ToDo> doInBackground(Void... voids) {
            if (activityReference.get()!=null)
                return activityReference.get().database.todoDAO().getAllToDo();
            else
                return null;
        }

        @Override
        protected void onPostExecute(List<ToDo> todo) {
            if (todo!=null && todo.size()>0 ){
                setUpAdapter(todo);
            }
        }

    }

    public void setUpAdapter(List<ToDo> todo_list){
        TodoListAdapter customAdapter = new TodoListAdapter(this, R.layout.row_todo_list, todo_list, database);

        lv_todo.setAdapter(customAdapter);
    }

    public void clearAndLoadListView(){
        lv_todo.setAdapter(null);

        new retrieveTask(this).execute();
    }

    private void openAddDialog(){
        Context context = this;
        AlertDialog.Builder dialog;

        EditText input = new EditText(this);

        dialog =  new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.contact_list_dialog));
        dialog.setTitle(context.getString(R.string.list_todo_add_dialog_title));

        dialog.setView(input);

        dialog.setPositiveButton(context.getString(R.string.list_todo_add_dialog_option1), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(input.getText().toString().equalsIgnoreCase("")==false){
                    ToDo todo = new ToDo();
                    todo.setConcept(input.getText().toString());
                    todo.setDone(false);
                    todo.setRegistrationdate(Utils.getInstance().currentDateToTimestamp());

                    new insertTask(database.todoDAO()).execute(todo);

                    clearAndLoadListView();

                    dialog.dismiss();
                }
            }
        });


        dialog.setNegativeButton(context.getString(R.string.list_todo_add_dialog_option2), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private static class insertTask extends AsyncTask<ToDo, Void, Void> {
        private ToDoDAO mToDoDAO;

        insertTask(ToDoDAO dao) {
            mToDoDAO = dao;
        }

        @Override
        protected Void doInBackground(final ToDo... params) {
            mToDoDAO.insertToDo(params[0]);
            Log.d(getClass().getSimpleName(), "add todo at background");
            return null;
        }
    }

}