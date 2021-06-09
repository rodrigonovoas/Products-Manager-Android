package app.rodrigonovoa.myproductsmanager;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.room.Room;

import app.rodrigonovoa.myproductsmanager.database.Product;
import app.rodrigonovoa.myproductsmanager.database.ProductDAO;
import app.rodrigonovoa.myproductsmanager.database.ProductsManagerDb;

public class ProductsManagerApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
