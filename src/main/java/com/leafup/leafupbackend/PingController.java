package com.leafup.leafupbackend;

import com.github.giwoong01.springapicommon.template.RspTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class PingController {

    @GetMapping("/ping")
    public ResponseEntity<RspTemplate<String>> ping() {
        return RspTemplate.<String>builder()
                .statusCode(HttpStatus.OK)
                .message("pong")
                .data("pong")
                .build()
                .toResponseEntity();
    }

}
