package app.rodrigonovoa.myproductsmanager.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ContactDAO {
    @Query("SELECT * FROM contact")
    List<Contact> getAllContacts();

    @Query("SELECT * FROM contact where type = :type")
    List<Contact> getContactsByType(String type);

    @Query("SELECT * FROM contact where contactid = :id")
    Contact getContactById(int id);

    @Insert
    void insertContact(Contact contact);

    @Delete
    void deleteContact(Contact contact);

    @Update
    public int updatecontact(Contact contact);
}