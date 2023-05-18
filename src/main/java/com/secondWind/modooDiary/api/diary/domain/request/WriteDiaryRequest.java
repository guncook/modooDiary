package com.secondWind.modooDiary.api.diary.domain.request;

import com.secondWind.modooDiary.api.diary.domain.entity.Diary;
import com.secondWind.modooDiary.api.member.domain.entity.Member;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class WriteDiaryRequest {
    @Min(value = 1, message = "회원 아이디가 필요합니다.")
    private Long memberId;

    @Length(min = 1, message = "제목이 필요합니다.")
    private String title;

    private String weather;

    private String content;

    public static Diary createDiary(WriteDiaryRequest writeDiaryRequest, Member member) {
        return Diary.createDiaryBuilder()
                .title(writeDiaryRequest.title)
                .weather(writeDiaryRequest.weather)
                .content((writeDiaryRequest.content != null && !writeDiaryRequest.content.isBlank()) ? writeDiaryRequest.content : "제곧내")
                .member(member)
                .build();
    }
}
