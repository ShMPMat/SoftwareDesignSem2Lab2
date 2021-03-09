package shmp.sd.second.two;

import shmp.sd.second.two.dao.MongoDAO;
import shmp.sd.second.two.server.ProductServer;


public class Main {
    public static void main(final String[] args) {
        new ProductServer(new MongoDAO("mongodb://localhost:27017")).run();
    }
}
