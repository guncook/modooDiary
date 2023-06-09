package com.secondWind.modooDiary.api.diary.repository;

import com.secondWind.modooDiary.api.diary.domain.request.SearchDiary;
import com.secondWind.modooDiary.api.diary.domain.response.DiaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

public interface DiaryCustomRepository {
    Page<DiaryResponse> searchDiary(SearchDiary searchDiary, PageRequest pageRequest);

    Optional<DiaryResponse> getDiary(Long diaryId);
}
