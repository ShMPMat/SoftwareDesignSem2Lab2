package shmp.sd.second.two.dao;

import com.mongodb.rx.client.Success;
import rx.Observable;
import shmp.sd.second.two.model.Product;
import shmp.sd.second.two.model.User;


public interface ProductDao {
    Observable<Product> getProducts();

    Observable<User> getUser(String login);

    Observable<Success> addProduct(Product product);

    Observable<Success> addUser(User user);
}
