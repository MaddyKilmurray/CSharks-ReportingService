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
}
