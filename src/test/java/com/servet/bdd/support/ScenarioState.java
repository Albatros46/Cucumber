package com.servet.bdd.support;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Data that one step class writes and another one reads within the same scenario:
 * CartSteps remembers what was added (with the price shown on the product list),
 * CheckoutSteps checks the checkout total against it. PicoContainer injects the same instance
 * into both and throws it away after the scenario.
 */
public class ScenarioState {

    private final Map<String, Double> addedProducts = new LinkedHashMap<>();

    public void added(String product, double price) {
        addedProducts.put(product, price);
    }

    public void removed(String product) {
        addedProducts.remove(product);
    }

    public Map<String, Double> addedProducts() {
        return Map.copyOf(addedProducts);
    }

    public double sumOfAddedPrices() {
        return addedProducts.values().stream().mapToDouble(Double::doubleValue).sum();
    }
}
