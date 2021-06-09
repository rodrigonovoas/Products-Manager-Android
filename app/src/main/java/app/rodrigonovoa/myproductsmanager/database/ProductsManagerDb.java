package app.rodrigonovoa.myproductsmanager.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Product.class, Contact.class, Operation.class, ToDo.class}, version = 1)
public abstract class ProductsManagerDb extends RoomDatabase {
    private static String DB_NAME = "products-manager-db";
    public abstract ProductDAO productDAO();
    public abstract ContactDAO contactDAO();
    public abstract OperationDAO operationDAO();
    public abstract ToDoDAO todoDAO();

    private static ProductsManagerDb productsDB;

    public static ProductsManagerDb getInstance(Context context) {
        if (null == productsDB) {
            productsDB = buildDatabaseInstance(context);
        }
        return productsDB;
    }

    private static ProductsManagerDb buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(context,
                ProductsManagerDb.class,
                DB_NAME)
                .allowMainThreadQueries().build();
    }

    public void cleanUp(){
        productsDB = null;
    }

}