package app.rodrigonovoa.myproductsmanager.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {@ForeignKey(entity = Product.class,
        parentColumns = "productid",
        childColumns = "productid",
        onDelete = ForeignKey.CASCADE),

        @ForeignKey(entity = Contact.class,
                parentColumns = "contactid",
                childColumns = "contactid",
                onDelete = ForeignKey.CASCADE)})

public class Operation {
    @PrimaryKey(autoGenerate = true)
    public int operationid;

    @ColumnInfo(name = "registration_date")
    public Long registrationdate;

    public String type;

    public String name;

    public int quantity;

    public float price;

    public float cost;

    public String notations;

    public int productid;

    public int contactid;

    //getters


    public int getOperationid() {
        return operationid;
    }

    public Long getRegistrationdate() {
        return registrationdate;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public float getPrice() {
        return price;
    }

    public float getCost() {
        return cost;
    }

    public String getNotations() {
        return notations;
    }

    public int getProductid() {
        return productid;
    }

    public int getContactid() {
        return contactid;
    }

    //setters


    public void setOperationid(int operationid) {
        this.operationid = operationid;
    }

    public void setRegistrationdate(Long date) {
        this.registrationdate = date;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public void setNotations(String notations) {
        this.notations = notations;
    }

    public void setProductid(int productid) {
        this.productid = productid;
    }

    public void setContactid(int contactid) {
        this.contactid = contactid;
    }
}
