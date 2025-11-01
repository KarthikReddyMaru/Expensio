package com.cashigo.expensio.controller.summary;

import com.cashigo.expensio.common.documentation.StandardErrorResponses;
import com.cashigo.expensio.dto.ReportDto;
import com.cashigo.expensio.dto.Response;
import com.cashigo.expensio.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/summary/report")
@RequiredArgsConstructor
@StandardErrorResponses
@Tag(name = "Reports")
public class ReportSummaryController {

    private final ReportService reportService;

    @GetMapping(params = {"month", "year"})
    @Operation(summary = "Get monthly transaction report") @ApiResponse(responseCode = "200")
    public ResponseEntity<Response<ReportDto>> getReportOfMonthYear(int month, int year) {
        Response<ReportDto> response = new Response<>();
        ReportDto report = reportService.getReportOf(month, year);
        response.setData(report);
        return ResponseEntity.ok(response);
    }

}
