package goblin.app.FixedSchedule.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import goblin.app.Common.exception.CustomException;
import goblin.app.FixedSchedule.model.dto.FixedScheduleRequestDTO;
import goblin.app.FixedSchedule.model.dto.FixedScheduleResponseDTO;
import goblin.app.FixedSchedule.service.FixedScheduleService;
import goblin.app.User.model.entity.User;
import goblin.app.User.repository.UserRepository;
import goblin.app.User.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/fixed")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "고정 일정")
public class FixedScheduleController {

  private final FixedScheduleService fixedScheduleService;
  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;

  // 고정 일정 생성
  @PostMapping("/create")
  @Operation(summary = "고정 일정 생성", description = "고정 일정 등록")
  public ResponseEntity<FixedScheduleResponseDTO> createFixedSchedule(
      @RequestBody @Valid FixedScheduleRequestDTO requestDto,
      @RequestHeader(value = "Authorization", required = false) String bearerToken) {
    try {
      User user = getUserFromToken(bearerToken);
      FixedScheduleResponseDTO responseDto =
          fixedScheduleService.createFixedSchedule(requestDto, user);
      return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    } catch (CustomException e) {
      log.error("고정 일정 생성 실패: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
  }

  // 사용자의 고정 일정 조회
  @Operation(summary = "사용자의 고정 일정 조회", description = "로그인한 사용자의 고정 일정 리스트를 조회")
  @GetMapping("/user")
  public ResponseEntity<?> getUserFixedSchedules(
      @RequestHeader(value = "Authorization", required = true) String bearerToken) {
    try {
      String loginId = extractLoginId(bearerToken);
      List<FixedScheduleResponseDTO> schedules =
          fixedScheduleService.getUserFixedSchedules(loginId);
      return ResponseEntity.ok(schedules);
    } catch (RuntimeException e) {
      log.error("고정 일정 조회 실패: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  // 고정 일정 시간 및 요일 업데이트
  @Operation(summary = "고정 일정 시간 및 요일 업데이트", description = "기존 고정 일정의 요일, 시작 및 종료 시간을 업데이트")
  @PutMapping("/{scheduleId}/update")
  public ResponseEntity<?> updateFixedSchedule(
      @PathVariable Long scheduleId,
      @RequestBody FixedScheduleRequestDTO updateRequest,
      @RequestHeader(value = "Authorization", required = false) String bearerToken) {
    try {
      String loginId = extractLoginId(bearerToken);
      fixedScheduleService.updateFixedSchedule(scheduleId, updateRequest, loginId);
      return ResponseEntity.ok("고정 일정이 업데이트되었습니다.");
    } catch (RuntimeException e) {
      log.error("고정 일정 업데이트 실패: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  // JWT 토큰에서 loginId 추출
  private String extractLoginId(String bearerToken) {
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      String token = bearerToken.substring(7);
      Claims claims = jwtUtil.getAllClaimsFromToken(token);
      return claims.getId(); // 토큰에서 loginId 추출
    }
    return null;
  }

  // 고정 일정 삭제
  @Operation(summary = "고정 일정 삭제", description = "기존 고정 일정을 삭제")
  @DeleteMapping("/delete/{scheduleId}")
  public ResponseEntity<?> deleteFixedSchedule(
      @PathVariable Long scheduleId,
      @RequestHeader(value = "Authorization", required = true) String bearerToken) {
    try {
      String loginId = extractLoginId(bearerToken);
      fixedScheduleService.deleteFixedSchedule(scheduleId, loginId);
      return ResponseEntity.ok("고정 일정이 삭제되었습니다.");
    } catch (RuntimeException e) {
      log.error("고정 일정 삭제 실패: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @GetMapping("/group/{groupId}")
  @Operation(summary = "그룹별 고정 일정 조회", description = "해당 그룹의 고정 일정을 조회")
  public ResponseEntity<List<FixedScheduleResponseDTO>> getSchedulesByGroup(
      @PathVariable Long groupId,
      @RequestHeader(value = "Authorization", required = true) String bearerToken) {

    String loginId = extractLoginId(bearerToken);
    List<FixedScheduleResponseDTO> schedules =
        fixedScheduleService.getSchedulesByGroup(groupId, loginId);
    return ResponseEntity.ok(schedules);
  }

  @PutMapping("/{scheduleId}/group/{groupId}/toggle-public")
  @Operation(summary = "고정 일정 공개 여부 변경", description = "그룹 내 고정 일정의 공개 여부를 전환")
  public ResponseEntity<String> togglePublicStatus(
      @PathVariable Long scheduleId,
      @PathVariable Long groupId,
      @RequestHeader(value = "Authorization", required = true) String bearerToken) {

    String loginId = extractLoginId(bearerToken);
    fixedScheduleService.togglePublicStatus(scheduleId, groupId, loginId);
    return ResponseEntity.ok("공개 여부가 전환되었습니다.");
  }

  //  @GetMapping("/personal/month")
  //  @Operation(summary = "개인 고정 일정 월별 조회", description = "사용자의 개인 고정 일정을 월별로 조회")
  //  public ResponseEntity<List<FixedScheduleResponseDTO>> getPersonalFixedSchedulesByMonth(
  //      @RequestParam int year,
  //      @RequestParam int month,
  //      @RequestHeader(value = "Authorization", required = true) String bearerToken) {
  //
  //    User user = getUserFromToken(bearerToken);
  //    List<FixedScheduleResponseDTO> schedules =
  //        fixedScheduleService.getPersonalFixedSchedulesByMonth(year, month, user);
  //    return ResponseEntity.ok(schedules);
  //  }
  //
  //  @GetMapping("/personal/day")
  //  @Operation(summary = "개인 고정 일정 일별 조회", description = "사용자의 개인 고정 일정을 일별로 조회")
  //  public ResponseEntity<List<FixedScheduleResponseDTO>> getPersonalFixedSchedulesByDay(
  //      @RequestParam int year,
  //      @RequestParam int month,
  //      @RequestParam int day,
  //      @RequestHeader(value = "Authorization", required = true) String bearerToken) {
  //
  //    User user = getUserFromToken(bearerToken);
  //    List<FixedScheduleResponseDTO> schedules =
  //        fixedScheduleService.getPersonalFixedSchedulesByDay(year, month, day, user);
  //    return ResponseEntity.ok(schedules);
  //  }

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
