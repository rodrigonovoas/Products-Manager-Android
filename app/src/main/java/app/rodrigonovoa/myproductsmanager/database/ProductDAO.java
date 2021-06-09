package app.rodrigonovoa.myproductsmanager.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProductDAO {
    @Query("SELECT * FROM product")
    List<Product> getAllProducts();

    @Query("SELECT * FROM product where productid = :id")
    Product getProductById(int id);

    @Insert
    void insertProduct(Product product);

    @Delete
    void deleteProduct(Product product);

    @Update
    public int updateProduct(Product product);
}