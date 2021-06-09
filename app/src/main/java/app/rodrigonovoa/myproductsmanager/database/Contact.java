package app.rodrigonovoa.myproductsmanager.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Contact {
    @PrimaryKey(autoGenerate = true)
    public int contactid;

    @ColumnInfo(name = "registration_date")
    public Long registrationdate;

    public String name;

    public String type;

    public String origin;

    public String notations;

    @ColumnInfo(name = "image_path")
    public String imagepath;

    //getters


    public int getContactid() {
        return contactid;
    }

    public Long getRegistrationdate() {
        return registrationdate;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getOrigin() {
        return origin;
    }

    public String getNotations() {
        return notations;
    }

    public String getImagepath() {
        return imagepath;
    }

    //setters


    public void setContactid(int contactid) {
        this.contactid = contactid;
    }

    public void setRegistrationdate(Long registrationdate) {
        this.registrationdate = registrationdate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setNotations(String notations) {
        this.notations = notations;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }
}