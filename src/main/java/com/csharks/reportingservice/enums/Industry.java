package com.csharks.reportingservice.enums;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public enum Industry {
    PRODUCE, ECOMMERCE, MEDICAL, OTHER, MANUFACTURING;

    public static Industry getIndustry(String industry) {

        switch(industry){
            case "":
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No industry input. Please, try again.");
            case "PRODUCE":
                return Industry.PRODUCE;
            case "ECOMMERCE":
                return Industry.ECOMMERCE;
            case "MEDICAL":
                return Industry.MEDICAL;
            case "OTHER":
                return Industry.OTHER;
            case "MANUFACTURING":
                return Industry.MANUFACTURING;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid industry input. Please, try again.");
        }
    }

    public static List<Industry> createIndustryList() {
        List<Industry> industries = new ArrayList<Industry>(EnumSet.allOf(Industry.class));
        return industries;
    }
}
