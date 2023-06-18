package waterplace.finalproj.model;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Supplier {
    private String name;
    private Long cnpj;
    private String phone;
    private List<Product> products;
    private Address address;
    private String uid;
    private double rating;

    // Cria um construtor com o padrão singleton
    private static Supplier instance;

    // Construtor privado sem parâmetros para o padrão singleton
    public Supplier(){}

    // Construtor privado para o padrão singleton
    private Supplier(DataSnapshot dataSnapshot){
        this.name = dataSnapshot.child("name").getValue().toString();
        this.cnpj = Long.parseLong(dataSnapshot.child("cnpj").getValue().toString());
        this.phone = dataSnapshot.child("phone").getValue().toString();
        if (dataSnapshot.child("products").exists()){
            this.products = (List<Product>) dataSnapshot.child("products").getValue();
        }
        this.address = dataSnapshot.child("Address").getValue(Address.class);
    }

    public static Supplier getInstance(){
        System.out.println(instance.name);
        return instance;
    }
    public static void setInstance(DataSnapshot dataSnapshot){
        instance = new Supplier(dataSnapshot);
    }

    public String getUid(){
        return uid;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCnpj() {
        return cnpj;
    }

    public void setCnpj(Long cnpj) {
        this.cnpj = cnpj;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Product> getProducts() {
        if (this.products == null) {
            this.products = new ArrayList<>();
        }
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void setAddress(Address address){
        this.address = address;
    }

    public Address getAddress() { return this.address; }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
