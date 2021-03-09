package shmp.sd.second.two.server;

import com.mongodb.rx.client.Success;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServer;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import rx.Observable;
import shmp.sd.second.two.dao.ProductDao;
import shmp.sd.second.two.model.Currency;
import shmp.sd.second.two.model.Product;
import shmp.sd.second.two.model.User;
import shmp.sd.second.two.server.util.ObjectMakers;

import java.util.Objects;

import static shmp.sd.second.two.server.util.SafeParameterHandler.getRequestParamSafe;


public class ProductServer {
    private ProductDao dao;

    public ProductServer(ProductDao dao) {
        this.dao = dao;
    }

    public void run() {
        HttpServer.newServer(8080)
                .start((req, resp) -> {
                    String path = req.getDecodedPath().substring(1);
                    Observable<String> response = Observable
                            .just("Unknown command " + path);

                    switch (path) {
                        case "add-user":
                            try {
                                User user = ObjectMakers.makeUser(req);
                                Observable<Success> success = dao.addUser(user);

                                response = success.map(Objects::toString);
                            } catch (ServerException e) {
                                response = Observable.just(e.getMessage());
                            }
                            break;
                        case "add-product":
                            try {
                                Product product = ObjectMakers.makeProduct(req);
                                dao.addProduct(product).subscribe(System.out::println);

                                response = getPrintedProducts(fetchCurrency(req));
                            } catch (ServerException e) {
                                response = Observable.just(e.getMessage());
                            }
                            break;
                        case "get-products":
                            response = getPrintedProducts(fetchCurrency(req));
                            break;
                    }

                    return resp.writeString(response);
                })
                .awaitShutdown();
    }

    private Observable<String> printProduct(Observable<Currency> currency, Product product) {
        return currency.first().map(c ->
                "{ 'name': '" + product.name +
                        "', 'price': '" + c.getCurrencyFormatter().apply(product.price) +
                        "' }"
        );
    }

    private Observable<String> getPrintedProducts(Observable<Currency> currency) {
        return dao.getProducts()
                .concatMap(p -> printProduct(currency, p))
                .toList()
                .map((sl) -> "[\n" + String.join(",\n", sl) + "\n]");
    }

    private Observable<Currency> fetchCurrency(HttpServerRequest<ByteBuf> req) {
        Currency defaultCurrency = Currency.Ruble;

        String userLogin = getRequestParamSafe(req, "login");

        if (userLogin == null) {
            return Observable.just(defaultCurrency);
        }

        return dao.getUser(userLogin).map(u -> u.currency).defaultIfEmpty(defaultCurrency);
    }
}
