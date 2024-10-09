package goblin.app.Calendar.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import goblin.app.Calendar.model.dto.request.uCalSaveRequestDto;
import goblin.app.Calendar.model.dto.response.uCalResponseDto;
import goblin.app.Calendar.service.UserCalService;
import goblin.app.FixedSchedule.service.FixedScheduleService;
import goblin.app.User.model.entity.User;
import goblin.app.User.repository.UserRepository;
import goblin.app.User.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/calendar/user")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "개인 일정")
public class UserCalController {

  @Autowired private final UserCalService userCalService;
  @Autowired private final JwtUtil jwtUtil;
  @Autowired private final UserRepository userRepository;
  @Autowired private final FixedScheduleService fixedScheduleService;

  // 일반 스케쥴 등록
  @Operation(summary = "일반 일정 등록", description = "사용자의 새로운 일정을 캘린더에 저장")
  @PostMapping("/save")
  public ResponseEntity<uCalResponseDto> save(
      @RequestBody @Valid uCalSaveRequestDto requestDto,
      @RequestHeader(value = "Authorization", required = false) String bearerToken) {
    try {
      User user = getUserFromToken(bearerToken);
      uCalResponseDto responseDto = userCalService.save(requestDto, user);
      return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    } catch (RuntimeException e) {
      log.error("유저 캘린더 저장 실패: {}", e.getMessage());
      return ResponseEntity.badRequest().body(null);
    }
  }

  // 유저 캘린더 수정
  @Operation(summary = "유저 캘린더 수정", description = "기존 캘린더 일정 수정")
  @PutMapping("/edit")
  public ResponseEntity<uCalResponseDto> edit(
      @RequestBody @Valid uCalSaveRequestDto requestDto, // @Valid 적용
      @RequestHeader(value = "Authorization", required = false) String bearerToken,
      @RequestHeader(value = "Schedule-Id") Long scheduleId) { // 헤더에서 ID 받기
    try {
      User user = getUserFromToken(bearerToken);
      uCalResponseDto responseDto =
          userCalService.edit(scheduleId, requestDto, user); // scheduleId 전달
      return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    } catch (RuntimeException e) {
      log.error("유저 캘린더 수정 실패: {}", e.getMessage());
      return ResponseEntity.badRequest().body(null);
    }
  }

  // 유저 캘린더 삭제
  @Operation(summary = "유저 캘린더 삭제", description = "사용자의 특정 일정을 캘린더에서 삭제")
  @DeleteMapping("/delete/{scheduleId}")
  public ResponseEntity<uCalResponseDto> delete(
      @PathVariable Long scheduleId,
      @RequestHeader(value = "Authorization", required = false) String bearerToken) {
    try {
      User user = getUserFromToken(bearerToken);
      uCalResponseDto responseDto = userCalService.deleteById(scheduleId, user);
      return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    } catch (RuntimeException e) {
      log.error("유저 캘린더 삭제 실패: {}", e.getMessage());
      return ResponseEntity.badRequest().body(null);
    }
  }

  // 특정 달의 캘린더 조회
  @Operation(summary = "월별 캘린더 조회", description = "사용자가 입력한 특정 년도와 월의 일정을 조회")
  @GetMapping("/view-month")
  public ResponseEntity<List<uCalResponseDto>> viewByMonth(
      @RequestParam int year,
      @RequestParam int month,
      @RequestHeader(value = "Authorization", required = false) String bearerToken) {
    try {
      User user = getUserFromToken(bearerToken);
      List<uCalResponseDto> calList = userCalService.viewByMonth(year, month, user);
      return ResponseEntity.status(HttpStatus.OK).body(calList);
    } catch (RuntimeException e) {
      log.error("유저 캘린더 조회 실패: {}", e.getMessage());
      return ResponseEntity.badRequest().body(null);
    }
  }

  // 특정 일의 캘린더 조회
  @Operation(summary = "일별 캘린더 조회", description = "사용자가 입력한 특정 년, 월, 일의 일정을 조회")
  @GetMapping("/view-day")
  public ResponseEntity<List<uCalResponseDto>> viewByDay(
      @RequestParam int year,
      @RequestParam int month,
      @RequestParam int day,
      @RequestHeader(value = "Authorization", required = false) String bearerToken) {
    try {
      User user = getUserFromToken(bearerToken);
      List<uCalResponseDto> calList = userCalService.viewByDay(year, month, day, user);
      return ResponseEntity.status(HttpStatus.OK).body(calList);
    } catch (RuntimeException e) {
      log.error("유저 캘린더 조회 실패: {}", e.getMessage());
      return ResponseEntity.badRequest().body(null);
    }
  }

  // 개인 스케줄 검색 (고정X)
  @Operation(summary = "개인 스케줄 검색 (고정X)", description = "키워드를 통해 사용자의 개인 스케줄을 검색")
  @GetMapping("/search")
  public ResponseEntity<List<uCalResponseDto>> searchSchedules(
      @RequestParam String keyword,
      @RequestHeader(value = "Authorization", required = false) String bearerToken) {
    try {
      User user = getUserFromToken(bearerToken);
      List<uCalResponseDto> response = userCalService.searchSchedules(keyword, user);
      return ResponseEntity.ok(response);
    } catch (RuntimeException e) {
      log.error("고정 스케줄 검색 실패: {}", e.getMessage());
      return ResponseEntity.badRequest().body(null);
    }
  }

  // JWT 토큰에서 User 객체 추출하는 메서드
  private User getUserFromToken(String bearerToken) {
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      String token = bearerToken.substring(7);
      Claims claims = jwtUtil.getAllClaimsFromToken(token);
      String loginId = claims.getId();

      return userRepository
          .findByLoginId(loginId)
          .orElseThrow(() -> new RuntimeException("User not found"));
    }
    throw new RuntimeException("Authorization token is missing or invalid");
  }
}
