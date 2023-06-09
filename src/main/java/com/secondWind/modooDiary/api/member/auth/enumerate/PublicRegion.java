package com.secondWind.modooDiary.api.member.auth.enumerate;

import com.secondWind.modooDiary.api.member.domain.entity.Member;
import lombok.Getter;

@Getter
public enum PublicRegion {
    SEOUL("서울", "60", "127"),
    BUSAN("부산", "98", "76"),
    INCHEON("인천", "55", "124"),
    GYEONGGI("경기", "60", "120"),
    DAEGU("대구", "89", "90"),
    DAEJEON("대전", "67", "100"),
    SEAJONG("세종", "66", "103"),
    CHOONGBUK("충복", "76", "114"),
    CHOONGNAM("충남", "68", "100"),
    JEONBUK("전북", "63", "89"),
    JEONNAM("전남", "51", "67"),
    GYEONGBUK("경북", "89", "91"),
    GYEONGNAM("경남", "91", "77"),
    JEJU("제주", "52", "38"),
    ULSAN("울산", "102", "84"),
    GWANGJU("광주", "58", "74");

    private String region;
    private final String nx;
    private final String ny;

    public static PublicRegion getDefault() {
        return PublicRegion.SEOUL;
    }

    public static PublicRegion fromString(String region) {
        try {
            return PublicRegion.valueOf(region);
        } catch (IllegalArgumentException e) {
            return getDefault();
        }
    }

    PublicRegion(String region, String nx, String ny) {
        this.region = region;
        this.nx = nx;
        this.ny = ny;
    }

    public static PublicRegion toPublicRegion(Member member) {
        return PublicRegion.valueOf(member.getRegion().toString());
    }

//    @Override
//    public String toString() {
//        return region;
//    }
}
