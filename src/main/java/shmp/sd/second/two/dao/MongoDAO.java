package shmp.sd.second.two.dao;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.rx.client.*;
import org.bson.Document;
import rx.Observable;
import shmp.sd.second.two.model.Currency;
import shmp.sd.second.two.model.Product;
import shmp.sd.second.two.model.User;


public class MongoDAO {
    private  MongoClient client;
    private  MongoDatabase database;

    public MongoDAO(String address) {
        client = MongoClients.create(address);
        database = client.getDatabase("lab_second_shop");
    }

    public Observable<Product> getProducts() {
        return database.getCollection("product")
                .find()
                .toObservable()
                .map(Product::new);
    }

    public Observable<User> getUser(String login) {
        return database.getCollection("user")
                .find(Filters.eq("login", login))
                .toObservable()
                .map(User::new);
    }

    public Observable<Success> addProduct(Product product) {
        return database.getCollection("product").insertOne(Document.parse(product.toString()));
    }

    public Observable<Success> addUser(User user) {
        return database.getCollection("user").insertOne(Document.parse(user.toString()));
    }
}
