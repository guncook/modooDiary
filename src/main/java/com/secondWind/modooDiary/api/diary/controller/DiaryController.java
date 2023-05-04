package com.secondWind.modooDiary.api.diary.controller;

import com.secondWind.modooDiary.api.diary.domain.request.SearchDiary;
import com.secondWind.modooDiary.api.diary.domain.request.UpdateDiaryRequest;
import com.secondWind.modooDiary.api.diary.domain.request.WriteDiaryRequest;
import com.secondWind.modooDiary.api.diary.domain.response.DiaryResponse;
import com.secondWind.modooDiary.api.diary.service.DiaryService;
import com.secondWind.modooDiary.common.component.WeatherSubscriber;
import com.secondWind.modooDiary.common.result.ResponseHandler;
import com.secondWind.modooDiary.common.result.WeatherResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "diary", description = "일기 API")
@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
@Slf4j
public class DiaryController {

    private final DiaryService diaryService;

    @Operation(summary = "일기 조회 API")
    @GetMapping("")
    public ResponseEntity<?> getDiaries(SearchDiary searchDiary) {
        Page<DiaryResponse> contents = diaryService.getDiaries(searchDiary);
        return ResponseHandler.generate()
                .data(contents)
                .status(HttpStatus.OK)
                .build();
    }

    @Operation(summary = "일기 작성 API")
    @PostMapping("")
    public ResponseEntity<?> writeDiary(@RequestBody WriteDiaryRequest writeDiaryRequest) {
        Long diaryId = diaryService.writeDiary(writeDiaryRequest);

        return ResponseHandler.generate()
                .data(diaryId)
                .status(HttpStatus.CREATED)
                .build();
    }

    @Operation(summary = "일기 수정 API")
    @PatchMapping("/{diary_id}")
    public ResponseEntity<?> updateDiary(@PathVariable(value = "diary_id") Long diaryId, @Valid @RequestBody UpdateDiaryRequest updateDiaryRequest) {
        Long updatedDiaryId = diaryService.updateDiary(diaryId, updateDiaryRequest);

        return ResponseHandler.generate()
                .data(updatedDiaryId)
                .status(HttpStatus.OK)
                .build();
    }

    @Operation(summary = "일기 삭제 API")
    @DeleteMapping("/{diary_id}")
    public ResponseEntity<?> deleteDiary(@PathVariable(value = "diary_id") Long diaryId) {
        diaryService.deleteDiary(diaryId);

        return ResponseHandler.generate()
                .data(null)
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
