package shmp.sd.second.two.dao;

import com.mongodb.rx.client.Success;
import rx.Observable;
import shmp.sd.second.two.model.Product;
import shmp.sd.second.two.model.User;

import java.util.ArrayList;
import java.util.List;

public class InMemoryDao implements ProductDao {
    private List<User> users = new ArrayList<>();
    private List<Product> products = new ArrayList<>();

    @Override
    public Observable<Product> getProducts() {
        return Observable.from(products);
    }

    @Override
    public Observable<User> getUser(String login) {
        return users.stream()
                .filter((u) -> u.login.equals(login))
                .map(Observable::just)
                .findFirst()
                .orElse(Observable.empty());
    }

    @Override
    public Observable<Success> addProduct(Product product) {
        products.add(product);

        return Observable.just(Success.SUCCESS);
    }

    @Override
    public Observable<Success> addUser(User user) {
        Boolean isPresent = users.stream().anyMatch((u) -> u.login.equals(user.login));

        if (isPresent) {
            return Observable.empty();
        }

        users.add(user);

        return Observable.just(Success.SUCCESS);
    }
}
