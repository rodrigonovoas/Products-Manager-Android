package app.rodrigonovoa.myproductsmanager.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ToDoDAO {
    @Query("SELECT * FROM todo")
    List<ToDo> getAllToDo();

    @Insert
    void insertToDo(ToDo todo);

    @Delete
    void deleteToDo(ToDo todo);

    @Update
    int updateToDo(ToDo todo);
}