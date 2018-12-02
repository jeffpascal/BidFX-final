package com.bidfx.orderbook;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit test for {@link OrderHandler} class.
 *
 * @author BidFX Systems Limited
 */
class OrderHandlerTest {
    private OrderHandler orderHandler;

    @BeforeEach
    void setUp() {
        orderHandler = new OrderHandler();
    }
    
    @AfterEach
    void cleanUp(){
    	orderHandler = null;
    }

    @Test
    @DisplayName("Add an order to the order book")
    void testOne() {
        Order order = new Order("1", 729.0, 100L, Side.BID);

        Map<String, Object> changedLevels = orderHandler.handleOrder(order);
        Map<Object, Object> expected = new TreeMap<>();
        expected.put(PriceFields.BID, 729.0);
        expected.put(PriceFields.BID_SIZE, 100L);
        assertEquals(expected, changedLevels);
    }

    @Test
    @DisplayName("Add a second order with the same price")
    void testTwo() {
        Order order1 = new Order("1", 729.0, 100L, Side.BID);
        Order order2 = new Order("2", 729.0, 50L, Side.BID);

        orderHandler.handleOrder(order1);
        Map<String, Object> changedLevels = orderHandler.handleOrder(order2);
        Map<Object, Object> expected = new TreeMap<>();
        expected.put(PriceFields.BID_SIZE, 150L);
        assertEquals(expected, changedLevels);
    }

    @Test
    @DisplayName("Add a second order with a worse price")
    void testThree() {
        Order order1 = new Order("1", 729.0, 100L, Side.BID);
        Order order2 = new Order("2", 728.9, 50L, Side.BID);

        orderHandler.handleOrder(order1);
        Map<String, Object> changedLevels = orderHandler.handleOrder(order2);
        Map<Object, Object> expected = new TreeMap<>();
        expected.put(PriceFields.BID2, 728.9);
        expected.put(PriceFields.BID_SIZE2, 50L);
        assertEquals(expected, changedLevels);
    }

    @Test
    @DisplayName("Add a second order with a better price")
    void testFour() {
        Order order1 = new Order("1", 729.0, 100L, Side.BID);
        Order order2 = new Order("2", 729.1, 50L, Side.BID);

        orderHandler.handleOrder(order1);
        Map<String, Object> changedLevels = orderHandler.handleOrder(order2);
        Map<Object, Object> expected = new TreeMap<>();
        expected.put(PriceFields.BID, 729.1);
        expected.put(PriceFields.BID_SIZE, 50L);
        expected.put(PriceFields.BID2, 729.0);
        expected.put(PriceFields.BID_SIZE2, 100L);
        assertEquals(expected, changedLevels);
    }

    @Test
    @DisplayName("Removing an order from the best level")
    void testFive() {
        Order order1 = new Order("1", 729.0, 100L, Side.BID);
        Order order2 = new Order("2", 729.0, 50L, Side.BID);
        Order order2delete = new Order("2", 729.0, 0L, Side.BID);

        orderHandler.handleOrder(order1);
        orderHandler.handleOrder(order2);
        Map<String, Object> changedLevels = orderHandler.handleOrder(order2delete);
        Map<Object, Object> expected = new TreeMap<>();
        expected.put(PriceFields.BID_SIZE, 100L);
        assertEquals(expected, changedLevels);
    }

    @Test
    @DisplayName("Removing the last order from the worst price level")
    void testSix() {
        Order order1 = new Order("1", 729.0, 100, Side.BID);
        Order order2 = new Order("2", 728.9, 50, Side.BID);
        Order order2remove = new Order("2", 728.9, 0, Side.BID);

        orderHandler.handleOrder(order1);
        orderHandler.handleOrder(order2);
        Map<String, Object> changedLevels = orderHandler.handleOrder(order2remove);
        Map<String, Object> expected = new TreeMap<>();
        expected.put(PriceFields.BID2, null);
        expected.put(PriceFields.BID_SIZE2, null);
        assertEquals(expected, changedLevels);
    }

    @Test
    @DisplayName("Removing the last order from the best price level")
    void testSeven() {
        Order order1 = new Order("1", 729.0, 100, Side.BID);
        Order order2 = new Order("2", 729.1, 50, Side.BID);
        Order order3 = new Order("3", 729.2, 25, Side.BID);
        Order order3delete = new Order("3", 729.2, 0, Side.BID);

        orderHandler.handleOrder(order1);
        orderHandler.handleOrder(order2);
        orderHandler.handleOrder(order3);
        Map<String, Object> changedLevels = orderHandler.handleOrder(order3delete);
        Map<String, Object> expected = new TreeMap<>();
        expected.put(PriceFields.BID, 729.1);
        expected.put(PriceFields.BID_SIZE, 50L);
        expected.put(PriceFields.BID2, 729.0);
        expected.put(PriceFields.BID_SIZE2, 100L);
        assertEquals(expected, changedLevels);
    }
    
    @Test 
    @DisplayName("Removing non-last with same price")
    void testEight()
    {
    	 Order order1 = new Order("1", 729.0, 100, Side.BID);
         Order order2 = new Order("2", 729.0, 50, Side.BID);
         Order order3 = new Order("3", 729.2, 25, Side.BID);
         Order order4 = new Order("4", 728.1, 30, Side.BID);
         Order order3delete = new Order("2", 729.0, 0, Side.BID);
         
         orderHandler.handleOrder(order1);
         orderHandler.handleOrder(order2);
         orderHandler.handleOrder(order3);
         orderHandler.handleOrder(order4);
         Map<String, Object> changedLevels = orderHandler.handleOrder(order3delete);
         Map<String, Object> expected = new TreeMap<>();
         
         expected.put(PriceFields.BID_SIZE2, 100L);
         expected.put(PriceFields.BID3, 728.1);
         expected.put(PriceFields.BID_SIZE3, 30L);
         
         assertEquals(expected, changedLevels);
    }
    
}