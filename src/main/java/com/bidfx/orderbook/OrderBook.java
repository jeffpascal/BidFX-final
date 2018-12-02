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
	private TreeMap<String, Object> update;
	public ArrayList<Order> orderBook = new ArrayList<Order>();

	public void remove(Order order) {
		update = new TreeMap<String, Object>();
		for (int i = 0; i < orderBook.size(); i++) {
			if (orderBook.get(i).getOrderId() == order.getOrderId()) {
				if (i == orderBook.size() - 1) { // if we're removing last element
					long size = 0;
					orderBook.remove(i);
					for (int j = 0; j < i; j++) {
						if (orderBook.get(j).getPrice() == order.getPrice())
							size = size + orderBook.get(j).getSize();
					}
					if (size > 0) {
						update.put("BidSize".concat(String.valueOf(i)), size);
					} else {
						update.put("BidSize".concat(String.valueOf(i + 1)), null);
						update.put("Bid".concat(String.valueOf(i + 1)), null);
					}
					// format test requirement if element to remove is last

					return;
				} else // any other case
				{

					for (int j = i + 1; j < orderBook.size(); j++) {
						// format for every element present after removed item
						update.put("BidSize".concat(String.valueOf(j)), orderBook.get(j).getSize());
						update.put("Bid".concat(String.valueOf(j)), orderBook.get(j).getPrice());

					}
					long size = 0;
					for (int j = 0; j < i; j++) { // make sure Size update is right
						if (orderBook.get(j).getPrice() == order.getPrice())
							size = size + orderBook.get(j).getSize();
					}
					if (size > 0) {
						update.put("BidSize".concat(String.valueOf(i)), size);
					}
					orderBook.remove(i);
				}

			}

		}

	}

	public void add(Order order) {
		update = new TreeMap<String, Object>();
		if (orderBook.size() == 0) { // if the array is empty
			orderBook.add(order);
			update.put("Bid1", order.getPrice());
			update.put("BidSize1", order.getSize());
			return;
		} else// other 3 cases
			for (int i = 0; i < orderBook.size(); i++) {
				if (orderBook.get(i).getPrice() == order.getPrice()) {
					orderBook.add(i + 1, order);
					update.put("BidSize".concat(String.valueOf(i + 1)), order.getSize() + orderBook.get(i).getSize());

					return; // make sure we leave add function
				} else if (orderBook.get(i).getPrice() < order.getPrice()) {
					orderBook.add(i, order);

					for (int j = 0; j < orderBook.size(); j++) {
						update.put("Bid".concat(String.valueOf(j + 1)), orderBook.get(j).getPrice());
						update.put("BidSize".concat(String.valueOf(j + 1)), orderBook.get(j).getSize());

					}
					return; // make sure we leave add function
				} else if (orderBook.get(i).getPrice() > order.getPrice()) {
					int newIndex = 0;
					for (int j = orderBook.size() - 1; j >= 0; j--) {
						if (orderBook.get(j).getPrice() > order.getPrice()) {

							newIndex = j + 1;
							orderBook.add(newIndex, order);
							update.put("Bid".concat(String.valueOf(newIndex + 1)), order.getPrice());
							update.put("BidSize".concat(String.valueOf(newIndex + 1)), order.getSize());
							return; // make sure we leave add function
						}
					}

				}

			}

	}

	public Map<String, Object> getChangedLevels() {
		// TODO Auto-generated method stub

		return update;
	}

	// TODO Implement your custom logic here
}
