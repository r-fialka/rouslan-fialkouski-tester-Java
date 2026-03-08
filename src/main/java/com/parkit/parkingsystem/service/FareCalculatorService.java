package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket, boolean discount) {

        if (ticket.getOutTime().before(ticket.getInTime())) {
            throw new IllegalArgumentException("Out time provided is before in time");
        }

        // Проверяем тип парковки
        if (ticket.getParkingSpot().getParkingType() == null) {
            throw new IllegalArgumentException("Unknown Parking Type");
        }

        long durationInMillis =
                ticket.getOutTime().getTime() - ticket.getInTime().getTime();

        double durationInHours =
                durationInMillis / (1000.0 * 60 * 60);

        // Free parking for first 30 minutes
        if (durationInMillis <= 30 * 60 * 1000) {
            ticket.setPrice(0);
            return;
        }

        double price;

        switch (ticket.getParkingSpot().getParkingType()) {

            case CAR:
                price = durationInHours * Fare.CAR_RATE_PER_HOUR;
                break;

            case BIKE:
                price = durationInHours * Fare.BIKE_RATE_PER_HOUR;
                break;

            default:
                throw new IllegalArgumentException("Unknown Parking Type");
        }

        if (discount) {
            price = price * 0.95;
        }

        ticket.setPrice(price);
    }

}