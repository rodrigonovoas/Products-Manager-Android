package app.rodrigonovoa.myproductsmanager.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Product {

    @PrimaryKey(autoGenerate = true)
    public int productid;

    @ColumnInfo(name = "registration_date")
    public Long registrationdate;

    public String name;

    @ColumnInfo(name = "sale_price")
    public float saleprice;

    @ColumnInfo(name = "purchase_price")
    public float purchaseprice;

    public String notations;

    @ColumnInfo(name = "image_path")
    public String imagepath;

    //Getters
    public int getProductid() {
        return productid;
    }

    public Long getRegistrationdate() {
        return registrationdate;
    }

    public String getName() {
        return name;
    }

    public float getSaleprice() {
        return saleprice;
    }

    public float getPurchaseprice() {
        return purchaseprice;
    }

    public String getNotations() {
        return notations;
    }

    public String getImagepath() {
        return imagepath;
    }

    //Setters


    public void setProductid(int productid) {
        this.productid = productid;
    }

    public void setRegistrationdate(Long registrationdate) {
        this.registrationdate = registrationdate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSaleprice(float saleprice) {
        this.saleprice = saleprice;
    }

    public void setPurchaseprice(float purchaseprice) {
        this.purchaseprice = purchaseprice;
    }

    public void setNotations(String notations) {
        this.notations = notations;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }
}