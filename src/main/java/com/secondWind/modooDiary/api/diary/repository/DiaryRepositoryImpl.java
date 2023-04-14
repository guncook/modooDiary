package com.secondWind.modooDiary.api.diary.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.secondWind.modooDiary.api.diary.domain.request.SearchDiary;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.secondWind.modooDiary.api.diary.domain.response.DiaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.secondWind.modooDiary.api.diary.domain.entity.QDiary.diary;
import static com.secondWind.modooDiary.api.member.domain.entity.QMember.member;

@Repository
@RequiredArgsConstructor
public class DiaryRepositoryImpl implements DiaryCustomRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<DiaryResponse> searchDiary(SearchDiary searchDiary, PageRequest pageRequest) {
        List<DiaryResponse> content = queryFactory.select(Projections.constructor(DiaryResponse.class,
                        diary.id,
                        member.nickName,
                        diary.title,
                        diary.weather,
                        diary.content))
                .from(diary)
                .innerJoin(diary.member, member)
                .where(memberIdEq(searchDiary.getMemberId()),
                        diaryTitleLike(searchDiary.getTitle()),
                        diaryWeatherEq(searchDiary.getWeather()))
                .orderBy(diary.id.desc())
                .distinct()
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(diary.count())
                .from(diary)
                .where(diary.isDeleted.eq(0));

        return PageableExecutionUtils.getPage(content, pageRequest, countQuery::fetchOne);
    }

    private BooleanExpression diaryWeatherEq(String weather) {
        return StringUtils.hasText(weather)? diary.weather.eq(weather) : null;
    }

    private BooleanExpression diaryTitleLike(String title) {
        return StringUtils.hasText(title)? diary.title.contains(title) : null;
    }

    private BooleanExpression memberIdEq(Long memberId) {
        return memberId != null && memberId > 0? member.id.eq(memberId) : null;
    }
}