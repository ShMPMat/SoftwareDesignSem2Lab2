package shmp.sd.second.two.model;

import java.util.Arrays;
import java.util.function.Function;

public enum Currency {
    Ruble(0, (i) -> (double) i / 100 + " Rub"),
    Euro(1, (i) -> (double) (i / 80) / 100 + " Eur"),
    Dollar(2, (i) -> (double) (i / 70) / 100 + " Dol");

    private Currency(int id, Function<Integer, String> currencyFormatter) {
        this.id = id;
        this.currencyFormatter = currencyFormatter;
    }

    public int getId() {
        return id;
    }

    public Function<Integer, String> getCurrencyFormatter() {
        return currencyFormatter;
    }

    public static Currency getById(int id) {
        return Arrays.stream(values())
                .filter((c) -> c.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Unexpected value: " + id));
    }

    private int id;
    private Function<Integer, String> currencyFormatter;
}
