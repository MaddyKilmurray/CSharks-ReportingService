package com.csharks.reportingservice;

import com.csharks.reportingservice.dto.report.ReportDTO;
import com.csharks.reportingservice.service.ReportingServiceClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReportingController {

    @Autowired
    ReportingServiceClass reportingServiceClass;

    @GetMapping("/report/bySalesRep/{dataType}")
    public List<ReportDTO> reportBySalesRep(@PathVariable(name = "dataType") String dataType) {
        return reportingServiceClass.reportBySalesRep(dataType);
    }

    @GetMapping("/report/byProduct/{dataType}")
    public List<ReportDTO> reportByProduct(@PathVariable(name = "dataType") String dataType) {
        return reportingServiceClass.reportByProduct(dataType);
    }

    @GetMapping("/report/byCountry/{dataType}")
    public List<ReportDTO> reportByCountry(@PathVariable(name = "dataType") String dataType) {
        return reportingServiceClass.reportByCountry(dataType);
    }

    @GetMapping("/report/byCity/{dataType}")
    public List<ReportDTO> reportByCity(@PathVariable(name = "dataType") String dataType) {
        return reportingServiceClass.reportByCity(dataType);
    }

    @GetMapping("/report/byIndustry/{dataType}")
    public List<ReportDTO> reportByIndustry(@PathVariable(name = "dataType") String dataType) {
        return reportingServiceClass.reportByIndustry(dataType);
    }

    @GetMapping("/report/byEmployeeCount/{reportType}")
    public ReportDTO reportByEmployeeCount(@PathVariable(name = "reportType") String reportType) {
        return reportingServiceClass.reportByEmployeeCount(reportType);
    }

    @GetMapping("/report/byProductQuantity/{reportType}")
    public ReportDTO reportByProductQuantity(@PathVariable(name = "reportType") String reportType) {
        return reportingServiceClass.reportByProductQuantity(reportType);
    }

    @GetMapping("/report/oppsByAccount/{dataType}")
    public ReportDTO reportOppsByAccount(@PathVariable(name = "dataType") String dataType) {
        return reportingServiceClass.reportOppsNumbersByAccount(dataType);
    }



}
