package shmp.sd.second.two.server;

import net.sourceforge.jwebunit.junit.WebTester;
import net.sourceforge.jwebunit.util.TestingEngineRegistry;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import shmp.sd.second.two.dao.InMemoryDao;
import shmp.sd.second.two.model.Currency;
import shmp.sd.second.two.model.Product;
import shmp.sd.second.two.model.User;


public class ProductServerTest {
    private static String WEBSITE_URL = "http://localhost:8080";
    private static WebTester webTester;

    private static InMemoryDao taskDao = new InMemoryDao();

    @BeforeClass
    public static void setUpTester() {
        new Thread(() -> new ProductServer(taskDao).run()).start();

        webTester = new WebTester();
        webTester.setTestingEngineKey(TestingEngineRegistry.TESTING_ENGINE_HTMLUNIT);
        webTester.getTestContext().setBaseUrl(WEBSITE_URL);
    }

    @Before
    public void cleanTaskDao() {
        taskDao.clear();
    }

    @Test
    public void connectionTest() {
        webTester.beginAt("/get-products");
        webTester.assertResponseCode(200);
    }

    @Test
    public void showProductsTest() {
        Product[] products = {
                new Product("Product1", 111),
                new Product("Product2", 112)
        };
        taskDao.addProduct(products[0]);
        taskDao.addProduct(products[1]);

        webTester.beginAt("/get-products");

        webTester.assertTextPresent(products[0].getName());
        webTester.assertTextPresent(products[1].getName());
    }

    @Test
    public void showDefaultUserCurrencyTest() {
        Currency defaultUserCurrency = Currency.Ruble;
        Product[] products = {
                new Product("Product1", 111),
                new Product("Product2", 112)
        };
        taskDao.addProduct(products[0]);
        taskDao.addProduct(products[1]);

        webTester.beginAt("/get-products");

        webTester.assertTextPresent(defaultUserCurrency.getCurrencyFormatter().apply(products[0].getPrice()));
        webTester.assertTextPresent(defaultUserCurrency.getCurrencyFormatter().apply(products[1].getPrice()));
    }

    @Test
    public void addProductsTest() {
        Product[] products = {
                new Product("Product12", 111),
                new Product("Product22", 112)
        };

        for (Product product : products) {
            webTester.beginAt("/add-product?name=" + product.getName() + "&price=" + product.getPrice());
        }

        webTester.assertTextPresent(products[0].getName());
        webTester.assertTextPresent(products[1].getName());
    }

    @Test
    public void addProductsIncorrectlyTest() {
        Product product = new Product("Nope", 111);

        webTester.beginAt("/add-product?name=" + product.getName());
        webTester.assertTextPresent("No parameter price");

        webTester.beginAt("/add-product?price=" + product.getPrice());
        webTester.assertTextPresent("No parameter name");
    }

    @Test
    public void addUsersTest() {
        User[] users = {
                new User("Canadian", Currency.Dollar),
                new User("German", Currency.Euro)
        };

        for (User user : users) {
            webTester.beginAt("/add-user?login=" + user.getLogin() + "&currency=" + user.getCurrency());
            webTester.assertTextPresent("SUCCESS");
        }
    }

    @Test
    public void addDuplicateUserTest() {
        User[] users = {
                new User("Canadian", Currency.Dollar),
                new User("Canadian", Currency.Euro)
        };

        webTester.beginAt("/add-user?login=" + users[0].getLogin() + "&currency=" + users[0].getCurrency());
        webTester.assertTextPresent("SUCCESS");

        webTester.beginAt("/add-user?login=" + users[1].getLogin() + "&currency=" + users[1].getCurrency());
        webTester.assertTextPresent("FAILED");
    }


    @Test
    public void userCurrencyChangeTest() {
        User[] users = {
                new User("Canadian", Currency.Dollar),
                new User("German", Currency.Euro)
        };
        Product product = new Product("Test", 10000);
        taskDao.addUser(users[0]);
        taskDao.addUser(users[1]);
        taskDao.addProduct(product);

        for (User user : users) {
            webTester.beginAt("/get-products?login=" + user.getLogin());
            webTester.assertTextPresent(user.getCurrency().getCurrencyFormatter().apply(product.getPrice()));
        }
    }
}
