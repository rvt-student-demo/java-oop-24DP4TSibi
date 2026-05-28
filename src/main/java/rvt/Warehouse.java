package rvt;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Warehouse {

    private Map<String, Integer> prices;
    private Map<String, Integer> stocks;

    public Warehouse() {
        this.prices = new HashMap<>();
        this.stocks = new HashMap<>();
    }

    public void addProduct(String product, int price, int stock) {
        this.prices.put(product, price);
        this.stocks.put(product, stock);
    }

    public int price(String product) {
        if (!this.prices.containsKey(product)) {
            return -99;
        }
        return this.prices.get(product);
    }

    public int stock(String product) {
        return this.stocks.getOrDefault(product, 0);
    }

    public boolean take(String product) {
        Integer s = this.stocks.get(product);
        if (s == null || s <= 0) {
            return false;
        }
        this.stocks.put(product, s - 1);
        return true;
    }

    public Set<String> products() {
        return this.prices.keySet();
    }
}
