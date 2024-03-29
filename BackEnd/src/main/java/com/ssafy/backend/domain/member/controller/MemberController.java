package com.ssafy.backend.domain.member.controller;

import com.ssafy.backend.domain.member.dto.*;
import com.ssafy.backend.domain.member.service.MemberService;
import com.ssafy.backend.global.common.dto.Message;
import com.ssafy.backend.global.component.email.service.EmailService;
import com.ssafy.backend.global.component.jwt.dto.TokenDto;
import com.ssafy.backend.global.component.jwt.dto.TokenMemberInfoDto;
import com.ssafy.backend.global.component.jwt.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {
    private final MemberService memberService;

    private final JwtService jwtService;

    private final EmailService emailService;

    @PostMapping("/email/send/{memberEmail}")
    public ResponseEntity<Message<Void>> sendEmailCode(@PathVariable String memberEmail) {
        emailService.sendEmailCode(memberEmail);
        return ResponseEntity.ok().body(Message.success());
    }

    @PostMapping("/email/verify/{memberEmail}/{emailCode}")
    public ResponseEntity<Message<Void>> verifyEmailCode(@PathVariable String memberEmail, @PathVariable String emailCode) {
        emailService.verifyEmailCode(memberEmail, emailCode);
        return ResponseEntity.ok().body(Message.success());
    }

    @PostMapping("/signup")
    public ResponseEntity<Message<Void>> signUpMember(@RequestBody MemberSignUpRequestDto signUpRequestDto) {
        memberService.signUpMember(signUpRequestDto);
        return ResponseEntity.ok().body(Message.success());
    }

    @PostMapping("/login")
    public ResponseEntity<Message<MemberLoginResponseDto>> login(@RequestBody MemberLoginRequestDto loginRequestDto,
                                                                 HttpServletResponse response) {
        TokenMemberInfoDto tokenMemberInfoDto = memberService.loginCheckMember(loginRequestDto);
        TokenDto tokenDto = jwtService.issueToken(tokenMemberInfoDto);
        MemberLoginResponseDto loginResponseDto = MemberLoginResponseDto.builder()
                .memberInfo(tokenMemberInfoDto)
                .token(tokenDto)
                .build();
        // JWT 토큰을 쿠키에 저장
        Cookie accessTokenCookie = new Cookie("accessToken", tokenDto.getAccessToken());
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(100000000); // 60분(3600초)으로 설정 (3600)
        response.addCookie(accessTokenCookie);

        return ResponseEntity.ok().body(Message.success(loginResponseDto));
    }

    @PostMapping("/logout")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<Message<Void>> logoutMember(@AuthenticationPrincipal MemberLoginActiveDto loginActiveDto,
                                                      HttpServletResponse response) {
        memberService.logoutMember(loginActiveDto.getEmail());

        // 쿠키 삭제
        Cookie accessTokenCookie = new Cookie("accessToken", null);
        accessTokenCookie.setMaxAge(0);
        accessTokenCookie.setPath("/");
        response.addCookie(accessTokenCookie);
        return ResponseEntity.ok().body(Message.success());
    }

    @PatchMapping("/update/password")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<Message<Void>> updatePasswordMember(@AuthenticationPrincipal MemberLoginActiveDto loginActiveDto,
                                                              @RequestBody MemberPasswordUpdateDto passwordUpdateDto) {
        memberService.updatePasswordMember(loginActiveDto.getId(), passwordUpdateDto);
        return ResponseEntity.ok().body(Message.success());
    }

    @PatchMapping("/update/image/nickname")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<Message<Void>> updateImageAndNicknameMember(@AuthenticationPrincipal MemberLoginActiveDto loginActiveDto,
                                                                      @RequestBody MemberUpdateDto updateDto) {
        memberService.updateImageAndNicknameMember(loginActiveDto.getId(), updateDto);
        return ResponseEntity.ok().body(Message.success());
    }

    @GetMapping("/get")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<Message<MemberGetResponseDto>> getMember(@AuthenticationPrincipal MemberLoginActiveDto loginActiveDto) {
        MemberGetResponseDto memberGetResponseDto = memberService.getMember(loginActiveDto.getId());
        return ResponseEntity.ok().body(Message.success(memberGetResponseDto));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<Message<List<MemberGetResponseDto>>> searchMembersByEmail(@RequestParam String email) {
        List<MemberGetResponseDto> memberSearchList = memberService.searchMembersByEmail(email);
        return ResponseEntity.ok().body(Message.success(memberSearchList));
    }
}
