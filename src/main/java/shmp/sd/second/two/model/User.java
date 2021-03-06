package shmp.sd.second.two.model;

import org.bson.Document;


public class User {
    public final String login;
    public final Currency currency;


    public User(Document doc) {
        this(doc.getString("login"), Currency.getById(doc.getInteger("currencyId")));
    }

    public User(String login, Currency currency) {
        this.login = login;
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "{" +
                "login: '" + login + "'" +
                ", currencyId: " + currency.getId() +
                '}';
    }
}
