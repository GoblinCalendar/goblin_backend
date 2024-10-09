package goblin.app.Calendar.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goblin.app.Calendar.model.dto.request.uCalSaveRequestDto;
import goblin.app.Calendar.model.dto.response.uCalResponseDto;
import goblin.app.Calendar.model.entity.UserCalRepository;
import goblin.app.Calendar.model.entity.UserCalendar;
import goblin.app.Common.exception.CustomException;
import goblin.app.Common.exception.ErrorCode;
import goblin.app.Group.model.dto.GroupHelper;
import goblin.app.Group.model.entity.Group;
import goblin.app.User.model.entity.User;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCalService {

  private final UserCalRepository userCalRepository;
  private final GroupHelper groupHelper;

  // 일반 스케쥴 등록
  @Transactional
  public uCalResponseDto save(uCalSaveRequestDto requestDto, User currentUser) {
    // "개인" 그룹을 조회하거나 생성
    Group personalGroup = groupHelper.getOrCreatePersonalGroup(currentUser);

    LocalDateTime startTime = requestDto.getStartTime(); // 변환된 startTime
    LocalDateTime endTime = requestDto.getEndTime(); // 변환된 endTime

    // 개인 일정을 개인 그룹과 연동하여 생성
    UserCalendar userCalendar =
        UserCalendar.builder()
            .title(requestDto.getTitle())
            .note(requestDto.getNote())
            .user(currentUser)
            .startTime(startTime)
            .endTime(endTime)
            .group(personalGroup) // 그룹에 "개인" 그룹 추가
            .build();

    userCalRepository.save(userCalendar);

    return new uCalResponseDto(userCalendar);
  }

  // 일반 스케쥴 수정
  @Transactional
  public uCalResponseDto edit(Long scheduleId, uCalSaveRequestDto requestDto, User currentUser) {
    UserCalendar userCalendar =
        userCalRepository
            .findById(scheduleId)
            .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));

    // 작성자인지 체크
    validateUser(userCalendar, currentUser);

    // AM/PM 시간을 LocalDateTime으로 변환
    LocalDateTime startTime = requestDto.getStartTime(); // 변환된 startTime
    LocalDateTime endTime = requestDto.getEndTime(); // 변환된 endTime

    // 일정 수정
    userCalendar.update(scheduleId, requestDto.getTitle(), startTime, endTime);

    return new uCalResponseDto(userCalendar);
  }

  // 개인 일반 스케줄 삭제 (hard delete)
  @Transactional
  public uCalResponseDto deleteById(Long id, User currentUser) {
    UserCalendar userCalendar =
        userCalRepository
            .findById(id)
            .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));

    // 작성자인지 체크
    validateUser(userCalendar, currentUser);

    userCalRepository.delete(userCalendar);
    return new uCalResponseDto(userCalendar);
  }

  // 개인 스케줄 월별 조회 (3개까지만 조회)
  @Transactional
  public List<uCalResponseDto> viewByMonth(int year, int month, User user) {
    // "개인" 그룹을 조회
    Group personalGroup = groupHelper.getOrCreatePersonalGroup(user);

    // 개인 그룹에 속한 일정 조회
    List<UserCalendar> scheduleList =
        userCalRepository.findByYearAndMonthAndGroup(year, month, user, personalGroup);

    return scheduleList.stream().map(uCalResponseDto::new).collect(Collectors.toList());
  }

  // 개인 스케줄 일별 조회
  @Transactional
  public List<uCalResponseDto> viewByDay(int year, int month, int day, User user) {
    // "개인" 그룹을 조회
    Group personalGroup = groupHelper.getOrCreatePersonalGroup(user);

    // 개인 그룹에 속한 일정 조회
    List<UserCalendar> scheduleList =
        userCalRepository.findByDayAndGroup(year, month, day, user, personalGroup);

    return scheduleList.stream().map(uCalResponseDto::new).collect(Collectors.toList());
  }

  // 개인 스케줄 검색 기능
  @Transactional
  public List<uCalResponseDto> searchSchedules(String keyword, User currentUser) {
    List<UserCalendar> scheduleList =
        userCalRepository.findByTitleContainingAndUserAndDeletedFalse(keyword, currentUser);
    return scheduleList.stream().map(uCalResponseDto::new).collect(Collectors.toList());
  }

  // 작성자 검증 메서드
  private void validateUser(UserCalendar userCalendar, User currentUser) {
    log.info(
        "userCalendar 작성자: {}, 현재 사용자: {}",
        userCalendar.getUser().getLoginId(),
        currentUser.getLoginId());

    if (!userCalendar.getUser().getLoginId().equals(currentUser.getLoginId())) {
      throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS); // 권한 없음 예외 처리
    }
  }

  // 연도와 월 검증 및 기본값 설정
  private int[] validateYearAndMonth(int year, int month) {
    int currentYear = LocalDate.now().getYear();
    int currentMonth = LocalDate.now().getMonthValue();

    year = (year <= 0) ? currentYear : year;
    month = (month <= 0 || month > 12) ? currentMonth : month;

    return new int[] {year, month};
  }
}
