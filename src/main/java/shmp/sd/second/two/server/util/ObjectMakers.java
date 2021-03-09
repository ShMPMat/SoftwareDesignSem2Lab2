package shmp.sd.second.two.server.util;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import shmp.sd.second.two.model.Currency;
import shmp.sd.second.two.model.Product;
import shmp.sd.second.two.model.User;
import shmp.sd.second.two.server.ServerException;

import java.util.Arrays;
import java.util.stream.Collectors;

import static shmp.sd.second.two.server.util.SafeParameterHandler.getRequestParamSafe;


public class ObjectMakers {
    public static User makeUser(HttpServerRequest<ByteBuf> req) throws ServerException {
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
                throw new ServerException("Parameter currency is not one of " + allCurrencies);
            }
        }

        if (login == null) {
            throw new ServerException("No parameter login provided");
        } else if (currency == null) {
            throw new ServerException("No parameter currency provided");
        }

        return new User(login, currency);
    }

    public static Product makeProduct(HttpServerRequest<ByteBuf> req) throws ServerException {
        String name = getRequestParamSafe(req, "name");

        Integer price = null;
        String stringPrice = getRequestParamSafe(req, "price");
        if (stringPrice != null) {
            try {
                price = Integer.parseInt(stringPrice);
            } catch (NumberFormatException ignored) {
                throw new ServerException("Parameter price is no int");
            }
        }

        if (name == null) {
            throw new ServerException("No parameter name provided");
        } else if (price == null) {
            throw new ServerException("No parameter price provided");
        }

        return new Product(name, price);
    }
}
