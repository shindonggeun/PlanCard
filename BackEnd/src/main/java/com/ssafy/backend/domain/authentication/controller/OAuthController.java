package com.ssafy.backend.domain.authentication.controller;

import com.ssafy.backend.domain.authentication.dto.OAuthLoginResponse;
import com.ssafy.backend.domain.authentication.dto.OAuthSignupRequestDto;
import com.ssafy.backend.domain.authentication.service.OAuthService;
import com.ssafy.backend.global.common.dto.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/oauth")
public class OAuthController {
    private final OAuthService oauthService;

    @GetMapping("/login/{provider}")
    public ResponseEntity<Message<OAuthLoginResponse>> loginOauth(@RequestParam String code, @PathVariable String provider) {
        OAuthLoginResponse loginResponse = oauthService.loginOauth(code, provider);
        return ResponseEntity.ok().body(Message.success(loginResponse));
    }

    @PostMapping("/signup")
    public ResponseEntity<Message<Void>> signupOauth(@RequestBody OAuthSignupRequestDto signupRequestDto) {
        oauthService.signupOauth(signupRequestDto);
        return ResponseEntity.ok().body(Message.success());
    }
}
