package shmp.sd.second.two.server;

import com.mongodb.rx.client.Success;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServer;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import rx.Observable;
import shmp.sd.second.two.dao.MongoDAO;
import shmp.sd.second.two.model.Currency;
import shmp.sd.second.two.model.Product;
import shmp.sd.second.two.model.User;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class ProductServer {
    private MongoDAO dao = new MongoDAO();

    public void run() {
        HttpServer.newServer(8080)
                .start((req, resp) -> {
                    String path = req.getDecodedPath().substring(1);
                    Observable<String> response = Observable
                            .just("Unknown command " + path);

                    switch (path) {
                        case "add-user":
                            String login = getRequestParamSafe(req, "login");

                            Currency currency = null;
                            String stringCurrency = getRequestParamSafe(req, "currency");
                            if (stringCurrency != null) {
                                try {
                                    currency = Currency.valueOf(stringCurrency);
                                } catch (IllegalArgumentException ignored) {
                                    String allCurrencies = Arrays.stream(Currency.values())
                                            .map(Object::toString)
                                            .collect(Collectors.joining(", "));
                                    response = Observable.just(
                                            "Parameter 'currency' is not one of " + allCurrencies
                                    );
                                    break;
                                }
                            }

                            if (login == null) {
                                response = Observable.just("No parameter 'login' provided");
                                break;
                            } else if (currency == null) {
                                response = Observable.just("No parameter 'currency' provided");
                                break;
                            }

                            User user = new User(login, currency);
                            Observable<Success> success = dao.addUser(user);

                            response = success.map(Objects::toString);
                            break;
                        case "add-product":
                            String name = getRequestParamSafe(req, "name");

                            Integer price = null;
                            String stringPrice = getRequestParamSafe(req, "price");
                            if (stringPrice != null) {
                                try {
                                    price = Integer.parseInt(stringPrice);
                                } catch (NumberFormatException ignored) {
                                    response = Observable.just("Parameter 'price' is no int");
                                    break;
                                }
                            }

                            if (name == null) {
                                response = Observable.just("No parameter 'name' provided");
                                break;
                            } else if (price == null) {
                                response = Observable.just("No parameter 'price' provided");
                                break;
                            }

                            Product product = new Product(name, price);
                            dao.addProduct(product).subscribe(System.out::println);

                            response = getPrintedProducts(fetchCurrency(req));
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
                        "' , 'price': '" + c.getCurrencyFormatter().apply(product.price) +
                        "' }"
        );
    }

    private Observable<String> getPrintedProducts(Observable<Currency> currency) {
        currency.first();
        return dao.getProducts()
                .concatMap(p -> printProduct(currency, p))
                .takeLastBuffer(10000, TimeUnit.MILLISECONDS)
                .map((sl) -> String.join("\n", sl));
    }

    private Observable<Currency> fetchCurrency(HttpServerRequest<ByteBuf> req) {
        Currency defaultCurrency = Currency.Ruble;

        List<String> userLogin = req.getQueryParameters().get("login");

        if (userLogin == null) {
            return Observable.just(defaultCurrency);
        }

        return dao.getUser(userLogin.get(0)).first().map(u -> u.currency).defaultIfEmpty(defaultCurrency);
    }

    private String getRequestParamSafe(HttpServerRequest<ByteBuf> req, String paramName) {
        List<String> params = req.getQueryParameters().get(paramName);
        String param = null;

        if (params != null) {
            param = params.stream()
                    .findFirst()
                    .orElse(null);
        }

        return param;
    }
}
