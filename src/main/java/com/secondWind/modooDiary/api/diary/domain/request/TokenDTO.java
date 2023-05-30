package com.secondWind.modooDiary.api.diary.domain.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenDTO {

    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Long refreshTokenExpiresIn;

    @Builder
    public TokenDTO(String grantType, String accessToken, String refreshToken, Long refreshTokenExpiresIn) {
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.refreshTokenExpiresIn = refreshTokenExpiresIn;
    }
}
