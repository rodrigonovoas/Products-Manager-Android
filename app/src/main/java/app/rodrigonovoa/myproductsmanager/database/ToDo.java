package app.rodrigonovoa.myproductsmanager.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ToDo {

    @PrimaryKey(autoGenerate = true)
    public int todoid;

    @ColumnInfo(name = "registration_date")
    public Long registrationdate;

    public String concept;

    public Boolean done;

    //getters

    public int getTodoid() {
        return todoid;
    }

    public Long getRegistrationdate() {
        return registrationdate;
    }

    public String getConcept() {
        return concept;
    }

    public Boolean getDone() {
        return done;
    }


    //setters

    public void setTodoid(int todoid) {
        this.todoid = todoid;
    }

    public void setRegistrationdate(Long registrationdate) {
        this.registrationdate = registrationdate;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }
}