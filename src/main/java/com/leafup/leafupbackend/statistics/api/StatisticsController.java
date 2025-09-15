package com.leafup.leafupbackend.statistics.api;

import com.github.giwoong01.springapicommon.template.RspTemplate;
import com.leafup.leafupbackend.global.annotation.AuthenticatedEmail;
import com.leafup.leafupbackend.statistics.api.docs.StatisticsControllerDocs;
import com.leafup.leafupbackend.statistics.api.dto.response.GlobalStatisticsResDto;
import com.leafup.leafupbackend.statistics.api.dto.response.MyStatisticsResDto;
import com.leafup.leafupbackend.statistics.application.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/statistics")
public class StatisticsController implements StatisticsControllerDocs {

    private final StatisticsService statisticsService;

    @GetMapping("/me")
    public ResponseEntity<RspTemplate<MyStatisticsResDto>> getMyStatistics(@AuthenticatedEmail String email) {
        return RspTemplate.<MyStatisticsResDto>builder()
                .statusCode(HttpStatus.OK)
                .message("나의 탄소 감축량 통계 조회 성공")
                .data(statisticsService.getMyStatistics(email))
                .build()
                .toResponseEntity();
    }

    @GetMapping("/global")
    public ResponseEntity<RspTemplate<GlobalStatisticsResDto>> getGlobalStatistics() {
        return RspTemplate.<GlobalStatisticsResDto>builder()
                .statusCode(HttpStatus.OK)
                .message("전체 탄소 감축량 통계 조회 성공")
                .data(statisticsService.getGlobalStatistics())
                .build()
                .toResponseEntity();
    }

}
