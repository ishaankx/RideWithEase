package com.ridewithease.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VehicleTest {

    @Test
    public void testVehicleDetails() {
        Vehicle car = new Vehicle("Toyota", "Corolla", "DL-1234", "White", 2022);


        assertNotNull(car);
    }
}