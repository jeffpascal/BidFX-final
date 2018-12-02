/*
 * Copyright (c) 2018. BidFX Systems Limited. All rights reserved.
 */

package com.bidfx.orderbook;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * This class represents an order book for a share or stock. An
 * {@code OrderBook} should retain the state of the share, keeping track of the
 * orders that are in the market.
 *
 * @author BidFX Systems Limited
 */
@SuppressWarnings("all")
public class OrderBook {

    /**
     * Represents the Delta after a remove/add operation
     */
    private TreeMap<String, Object> updateDelta;

    /**
     * Represents an orderBook
     */
    public ArrayList<Order> orderBook = new ArrayList<Order>();

    /**
     * Adds an order to orderBook
     * 
     * @param order The order to be added
     */
    public void add(Order order) {
        updateDelta = new TreeMap<String, Object>();
        if (orderBook.size() == 0) { // if the array is empty
            orderBook.add(order);
            updateDelta.put("Bid1", order.getPrice());
            updateDelta.put("BidSize1", order.getSize());
        } else// other 3 cases
            addIfNotFirst(order); // method call to add if the order is not the first one to be added
    }

    /**
     * Removes an order from orderBook
     * 
     * @param order The order to be removed
     */
    public void remove(Order order) {
        updateDelta = new TreeMap<String, Object>();
        for (int i = 0; i < orderBook.size(); i++)
            if (orderBook.get(i).getOrderId() == order.getOrderId())
                if (i == orderBook.size() - 1) // if we're removing last element
                    removeIfLast(i, order); // remove if order is the last order
                else // any other case
                    removeIfNotLast(i, order); // remove if order is not last order

    }

    /**
     * Adds {@code Order} if it is not the first one
     * 
     * @param order Order to be added
     */
    private void addIfNotFirst(Order order) {
        for (int i = 0; i < orderBook.size(); i++) {
            if (orderBook.get(i).getPrice() == order.getPrice()) { // CASE IF WE HAVE EQUAL NEW PRICE SHARE
                orderBook.add(i + 1, order);
                updateDelta.put("BidSize".concat(String.valueOf(i + 1)), order.getSize() + orderBook.get(i).getSize());

                return; // make sure we leave add function
            } else if (orderBook.get(i).getPrice() < order.getPrice()) {// CASE IF WE HAVE HIGHER NEW PRICE SHARE

                orderBook.add(i, order);

                for (int j = 0; j < orderBook.size(); j++) {
                    updateDelta.put("Bid".concat(String.valueOf(j + 1)), orderBook.get(j).getPrice());
                    updateDelta.put("BidSize".concat(String.valueOf(j + 1)), orderBook.get(j).getSize());

                }
                return; // make sure we leave add function
            } else if (orderBook.get(i).getPrice() > order.getPrice()) { // CASE IF WE HAVE LOWER NEW PRICE SHARE
                int newIndex = 0;
                for (int j = orderBook.size() - 1; j >= 0; j--) {
                    if (orderBook.get(j).getPrice() > order.getPrice()) {

                        newIndex = j + 1;
                        orderBook.add(newIndex, order);
                        updateDelta.put("Bid".concat(String.valueOf(newIndex + 1)), order.getPrice());
                        updateDelta.put("BidSize".concat(String.valueOf(newIndex + 1)), order.getSize());
                        return; // make sure we leave add function
                    }
                }

            }

        }
    }

    /**
     * Removes order at index given if it is the last one
     * 
     * @param indexToRemove the index of the order to be removed
     * @param order         Order to be removed
     */
    private void removeIfLast(int indexToRemove, Order order) {
        long sizeIfPriceEqual = 0;
        orderBook.remove(indexToRemove);
        for (int j = 0; j < indexToRemove; j++) {
            if (orderBook.get(j).getPrice() == order.getPrice())
                sizeIfPriceEqual = sizeIfPriceEqual + orderBook.get(j).getSize();
        }
        if (sizeIfPriceEqual > 0) {
            updateDelta.put("BidSize".concat(String.valueOf(indexToRemove)), sizeIfPriceEqual);
        } else {
            updateDelta.put("BidSize".concat(String.valueOf(indexToRemove + 1)), null);
            updateDelta.put("Bid".concat(String.valueOf(indexToRemove + 1)), null);
        }
        // format test requirement if element to remove is last

        return;
    }

    /**
     * Removes order at index given if it is not the last one
     * 
     * @param indexToRemove the index of the order to be removed
     * @param order         Order to be removed
     */
    private void removeIfNotLast(int indexToRemove, Order order) {
        for (int j = indexToRemove + 1; j < orderBook.size(); j++) {
            // format for every element present after removed item
            updateDelta.put("BidSize".concat(String.valueOf(j)), orderBook.get(j).getSize());
            updateDelta.put("Bid".concat(String.valueOf(j)), orderBook.get(j).getPrice());

        }
        long sizeIfPriceEqual = 0;
        for (int j = 0; j < indexToRemove; j++) { // make sure Size updateDelta is right
            if (orderBook.get(j).getPrice() == order.getPrice())
                sizeIfPriceEqual = sizeIfPriceEqual + orderBook.get(j).getSize();
        }
        if (sizeIfPriceEqual > 0) {
            updateDelta.put("BidSize".concat(String.valueOf(indexToRemove)), sizeIfPriceEqual);
        }
        orderBook.remove(indexToRemove);

    }

    /**
     * Return the delta (the changes)
     * 
     * @return The updated delta variable
     */
    public Map<String, Object> getChangedLevels() {
        // TODO Auto-generated method stub

        return updateDelta;
    }

    // TODO Implement your custom logic here
}
