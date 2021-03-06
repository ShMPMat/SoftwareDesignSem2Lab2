package shmp.sd.second.two.model;

import org.bson.Document;


public class Product {
    public final String name;
    public final int price;


    public Product(Document doc) {
        this(doc.getString("name"), doc.getInteger("price"));
    }

    public Product(String name, int price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public String toString() {
        return "{" +
                "name: '" + name + "'" +
                ", price: " + price +
                '}';
    }
}
