package com.ridewithease.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CustomerTest {

    @Test
    public void testCustomerCreation() {
        Customer customer = new Customer("John Doe", "john@test.com", "1234567890", "password");

        assertEquals("John Doe", customer.getFullName());
        assertEquals("john@test.com", customer.getEmail());
        assertEquals("RIDER", customer.getRole()); // Inherited logic check
    }

    @Test
    public void testLoyaltyPoints() {
        Customer customer = new Customer();
        assertEquals(0, customer.getLoyaltyPoints()); // Default should be 0

        customer.setLoyaltyPoints(100);
        assertEquals(100, customer.getLoyaltyPoints());
    }
}