package app.rodrigonovoa.myproductsmanager.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.List;

@Dao
public interface OperationDAO {
    @Query("SELECT * FROM operation")
    List<Operation> getAllOperations();

    @Query("SELECT * FROM operation where operationid = :id")
    Operation getOperationById(int id);

    @RawQuery
    List<Operation> getOperationsByConditions(SupportSQLiteQuery query);

    @Insert
    void insertOperation(Operation operation);

    @Delete
    void deleteOperation(Operation operation);

    @Update
    public int updateOperation(Operation operation);
}