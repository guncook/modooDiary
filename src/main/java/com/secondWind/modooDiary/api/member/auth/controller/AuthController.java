package com.secondWind.modooDiary.api.member.auth.controller;

import com.secondWind.modooDiary.api.diary.domain.request.MemberLoginDTO;
import com.secondWind.modooDiary.api.diary.domain.request.TokenDTO;
import com.secondWind.modooDiary.api.member.auth.domain.dto.*;
import com.secondWind.modooDiary.api.member.auth.service.AuthService;
import com.secondWind.modooDiary.api.member.auth.service.EmailService;
import com.secondWind.modooDiary.common.exception.code.MemberErrorCode;
import com.secondWind.modooDiary.common.result.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Tag(name = "auth", description = "인증 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin("*")
public class AuthController {
    private final String BASIC_PREFIX = "Basic ";

    @Value("${google.client.id}")
    private String googleClientId;

    @Value("${google.client.pw}")
    private String googleClientPw;

    private final AuthService authService;
    private final EmailService emailService;

    private Map<String, String> confirmEmail;

    @PostConstruct
    public void postConstruct() {
        this.confirmEmail = new ConcurrentHashMap<>();
    }

    @Operation(summary = "회원가입 API")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @Valid @RequestBody MemberJoinDTO memberJoinDTO) {

        if (authorization != null) {
            String authBasic = authorization.substring((BASIC_PREFIX.length()));

            String decodedAuthBasic = new String(Base64.getDecoder().decode(authBasic), StandardCharsets.UTF_8);

            String[] authUserInfo = decodedAuthBasic.split(":");

            String email = authUserInfo[0];
            String password = authUserInfo[1];

            memberJoinDTO.setEmail(email);
            memberJoinDTO.setPassword(password);

            MemberResponseDTO nickName = authService.signup(memberJoinDTO);

            return ResponseHandler.generate()
                    .data(nickName)
                    .status(HttpStatus.CREATED)
                    .build();
        } else {
            return ResponseHandler.failResultGenerate()
                    .errorMessage(MemberErrorCode.ENTERED_ID_AND_PASSWORD.getMessage())
                    .errorCode(MemberErrorCode.ENTERED_ID_AND_PASSWORD.getCode())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    @Operation(summary = "로그인 API")
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestHeader(value = "isAdmin", required = false) Boolean isAdmin,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
            ) {

        if (authorization != null && !authorization.isBlank()) {
            String authBasic = authorization.substring(BASIC_PREFIX.length());

            String decodeAuthBasic = new String(Base64.getDecoder().decode(authBasic), StandardCharsets.UTF_8);
            String[] authUserInfo = decodeAuthBasic.split(":");

            String email = authUserInfo[0];
            String password = authUserInfo[1];

            if (isAdmin == null) {
                isAdmin = false;
            }

            MemberLoginDTO memberLoginDTO = MemberLoginDTO.of()
                    .email(email)
                    .password(password)
                    .isAdmin(isAdmin)
                    .build();

            TokenDTO tokenDTO = authService.login(memberLoginDTO);

            return ResponseHandler.generate()
                    .data(tokenDTO)
                    .status(HttpStatus.OK)
                    .build();
        } else {
            return ResponseHandler.failResultGenerate()
                    .errorCode(MemberErrorCode.ENTERED_ID_AND_PASSWORD.getCode())
                    .errorMessage(MemberErrorCode.ENTERED_ID_AND_PASSWORD.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    @Operation(summary = "토큰 재발급 API")
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@Valid @RequestBody TokenRequestDTO tokenRequestDTO) {
        TokenDTO tokenDTO = authService.reissue(tokenRequestDTO);

        return ResponseHandler.generate()
                .data(tokenDTO)
                .status(HttpStatus.OK)
                .build();
    }

    @Operation(summary = "로그아웃 API")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@Valid @RequestBody TokenRequestDTO tokenRequestDTO) {
        authService.logout(tokenRequestDTO);

        return ResponseHandler.generate()
                .data(null)
                .status(HttpStatus.OK)
                .build();
    }

    @Operation(summary = "비밀번호 변경 API")
    @PatchMapping("/password")
    public ResponseEntity<?> updatePassword(
            @Valid @RequestBody PasswordUpdateRequest passwordUpdateRequest) {
        authService.updatePassword(passwordUpdateRequest);

        return ResponseHandler.generate()
                .data(null)
                .status(HttpStatus.OK)
                .build();
    }

    @Operation(summary = "구글 로그인 API")
    @PostMapping("/oauth2/google")
    public String loginUrlGoogle() {
        String reqUrl = "https://accounts.google.com/o/oauth2/v2/auth?client_id=" + googleClientId
                + "&redirect_uri=http://localhost:8080/api/auth/oauth2/google&response_type=code&scope=email%20profile%20openid&access_type=offline";
        return reqUrl;
    }

    @GetMapping("/oauth2/google")
    public String loginGoogle(@RequestParam(value = "code") String authCode){
        RestTemplate restTemplate = new RestTemplate();
        GoogleRequest googleOAuthRequestParam = GoogleRequest
                .builder()
                .clientId(googleClientId)
                .clientSecret(googleClientPw)
                .code(authCode)
                .redirectUri("http://localhost:8080/api/auth/oauth2/google")
                .grantType("authorization_code").build();
        ResponseEntity<GoogleResponse> resultEntity = restTemplate.postForEntity("https://oauth2.googleapis.com/token",
                googleOAuthRequestParam, GoogleResponse.class);
        String jwtToken=resultEntity.getBody().getId_token();
        Map<String, String> map=new HashMap<>();
        map.put("id_token",jwtToken);
        ResponseEntity<GoogleInfResponse> resultEntity2 = restTemplate.postForEntity("https://oauth2.googleapis.com/tokeninfo",
                map, GoogleInfResponse.class);
        String email=resultEntity2.getBody().getEmail();
        return email;
    }
}
