package waterplace.finalproj.model;

import com.google.firebase.database.DataSnapshot;

import java.util.List;

public class Supplier {
    private String name;
    private int cnpj;
    private String phone;
    private List<String> products;

    private List<String> requests;

    // Cria um construtor com o padrão singleton
    private static Supplier instance;

    // Construtor privado sem parâmetros para o padrão singleton
    public Supplier(){}

    // Construtor privado para o padrão singleton
    private Supplier(DataSnapshot dataSnapshot){
        this.name = dataSnapshot.child("name").getValue().toString();
        this.cnpj = Integer.parseInt(dataSnapshot.child("cnpj").getValue().toString());
        this.phone = dataSnapshot.child("phone").getValue().toString();
    }

    public static Supplier getInstance(){
        return instance;
    }
    public static void setInstance(DataSnapshot dataSnapshot){
        instance = new Supplier(dataSnapshot);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCnpj() {
        return cnpj;
    }

    public void setCnpj(int cnpj) {
        this.cnpj = cnpj;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getProducts() {
        return products;
    }

    public void setProducts(List<String> products) {
        this.products = products;
    }
}
