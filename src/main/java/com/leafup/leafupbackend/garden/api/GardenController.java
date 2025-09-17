package com.leafup.leafupbackend.garden.api;

import com.github.giwoong01.springapicommon.template.RspTemplate;
import com.leafup.leafupbackend.garden.api.docs.GardenControllerDocs;
import com.leafup.leafupbackend.garden.api.dto.response.WeeklyGardenResDto;
import com.leafup.leafupbackend.garden.application.WeeklyGardenService;
import com.leafup.leafupbackend.global.annotation.AuthenticatedEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/garden")
public class GardenController implements GardenControllerDocs {

    private final WeeklyGardenService weeklyGardenService;

    @Override
    @GetMapping("/weekly")
    public ResponseEntity<RspTemplate<WeeklyGardenResDto>> getWeeklyGardenStatus(@AuthenticatedEmail String email) {
        return RspTemplate.<WeeklyGardenResDto>builder()
                .statusCode(HttpStatus.OK)
                .message("주간 텃밭 현황 조회 성공")
                .data(weeklyGardenService.getWeeklyGardenStatus(email))
                .build()
                .toResponseEntity();
    }

    @PostMapping("/harvest")
    public ResponseEntity<RspTemplate<Void>> harvestFromGarden(@AuthenticatedEmail String email) {
        weeklyGardenService.harvestFromGarden(email);
        return RspTemplate.<Void>builder()
                .statusCode(HttpStatus.OK)
                .message("열매 수확 성공")
                .build()
                .toResponseEntity();
    }

}
