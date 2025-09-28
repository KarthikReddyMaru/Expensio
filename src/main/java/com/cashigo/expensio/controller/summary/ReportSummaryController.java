package com.cashigo.expensio.controller.summary;

import com.cashigo.expensio.dto.ReportDto;
import com.cashigo.expensio.dto.Response;
import com.cashigo.expensio.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/summary/report")
@RequiredArgsConstructor
public class ReportSummaryController {

    private final ReportService reportService;

    @GetMapping(params = {"month", "year"})
    public ResponseEntity<Response<ReportDto>> getReportOfMonthYear(int month, int year) {
        Response<ReportDto> response = new Response<>();
        ReportDto report = reportService.getReportOf(month, year);
        response.setData(report);
        return ResponseEntity.ok(response);
    }

}
