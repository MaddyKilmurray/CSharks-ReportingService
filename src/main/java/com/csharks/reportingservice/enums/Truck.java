package com.csharks.reportingservice.enums;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public enum Truck {
    HYBRID, FLATBED, BOX;

    public static Truck getTruck(String product) {

        switch(product){
            case "":
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No product input. Please, try again.");
            case "HYBRID":
                return Truck.HYBRID;
            case "FLATBED":
                return Truck.FLATBED;
            case "BOX":
                return Truck.BOX;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid product input. Please, try again.");
        }
    }

}
