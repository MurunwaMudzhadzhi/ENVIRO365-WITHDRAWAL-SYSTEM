package com.enviro.assessment.junior.murunwamudzhadzhi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Enviro365 Investments withdrawal notice system.
 *
 * This service exposes REST APIs that allow investors (via the frontend)
 * to view their portfolio, submit withdrawal notices against a product,
 * view withdrawal history and export statements as CSV.
 */
@SpringBootApplication
public class Enviro365WithdrawalApplication {

    public static void main(String[] args) {
        SpringApplication.run(Enviro365WithdrawalApplication.class, args);
    }
}
