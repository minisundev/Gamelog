package com.best11.gamelog.user.controller;

import com.best11.gamelog.CommonResponseDto;
import com.best11.gamelog.user.dto.PasswordRequestDto;
import com.best11.gamelog.user.dto.SignupRequestDto;
import com.best11.gamelog.user.dto.UserRequestDto;
import com.best11.gamelog.feed.dto.PostResponseDto;
import com.best11.gamelog.feed.dto.PostUpdateRequestDto;
import com.best11.gamelog.user.dto.*;
import com.best11.gamelog.user.entity.User;
import com.best11.gamelog.user.jwt.JwtUtil;
import com.best11.gamelog.user.security.UserDetailsImpl;
import com.best11.gamelog.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<CommonResponseDto> signup(@Valid @RequestBody SignupRequestDto signupRequestDto, BindingResult bindingResult) {
        // Validation 예외 처리
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if (fieldErrors.size() > 0) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
            }
            throw new IllegalArgumentException("error");
        }
        // 회원 가입 시도
        try {
            userService.signup(signupRequestDto);
        }
        // 실패 메시지 반환
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new CommonResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
        // 성공 메시지 반환
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(new CommonResponseDto("회원가입 성공", HttpStatus.CREATED.value()));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<CommonResponseDto> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        try {
            userService.login(loginRequestDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new CommonResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }

        jwtUtil.addJwtToCookie(jwtUtil.createToken(loginRequestDto.getUserId()), response);


        return ResponseEntity.ok().body(new CommonResponseDto("로그인 성공", HttpStatus.OK.value()));
    }

    // 이름 변경
    @PatchMapping("/username")
    public ResponseEntity<CommonResponseDto> updateUsername(@RequestBody UserRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        userService.updateUsername(requestDto, userDetails.getUser());

        return ResponseEntity.status(HttpStatus.OK.value()).body(new CommonResponseDto("이름 변경이 완료되었습니다.", HttpStatus.OK.value()));
    }

    // 한 줄 소개 변경
    @PatchMapping("/description")
    public ResponseEntity<CommonResponseDto> updateDescription(@RequestBody UserRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        userService.updateDescription(requestDto, userDetails.getUser());

        return ResponseEntity.status(HttpStatus.OK.value()).body(new CommonResponseDto("한 줄 소개 변경이 완료되었습니다.", HttpStatus.OK.value()));
    }

    // 비밀번호 변경
    @PatchMapping("/password")
    public ResponseEntity<CommonResponseDto> updatePassword(@RequestBody PasswordRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.updatePassword(requestDto, userDetails.getUser());

        return ResponseEntity.status(HttpStatus.OK.value()).body(new CommonResponseDto("비밀번호 변경이 완료되었습니다.", HttpStatus.OK.value()));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getUserProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        UserProfileDto responseDto = userService.getProfile(user);
        responseDto.setPosts(userService.getPosts(user));
        return ResponseEntity.ok(responseDto);
    }
}
