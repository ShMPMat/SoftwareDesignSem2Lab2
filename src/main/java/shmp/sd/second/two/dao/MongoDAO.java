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
    private static MongoClient client = createMongoClient();
    private static MongoDatabase database = client.getDatabase("lab_second_shop");

    public MongoDAO() {
        database.getCollection("user")
                .createIndex(Indexes.hashed("login"), new IndexOptions().unique(true));
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

    private static MongoClient createMongoClient() {
        return MongoClients.create("mongodb://localhost:27017");
    }

    public static void main(String[] args) throws InterruptedException {
        MongoCollection<Document> collection = client.getDatabase("rxtest").getCollection("user");
//        collection.drop().subscribe(System.out::println);
        collection.insertOne(Document.parse((new User("test", Currency.Ruble)).toString())).subscribe(System.out::println);
        collection.insertOne(Document.parse((new User("west", Currency.Euro)).toString())).subscribe(System.out::println);
        collection.count().subscribe(System.out::println);

        Observable<User> user = collection.find().toObservable().map(User::new);
//        user.subscribe(MongoDriver::getPrintln);
        user.subscribe(System.out::println);

        Thread.sleep(1000);
//        client.getDatabase("rxtest");
    }
}
