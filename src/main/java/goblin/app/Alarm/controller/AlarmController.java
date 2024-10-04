package goblin.app.Alarm.controller;

import goblin.app.Alarm.model.dto.FcmMessageDto;
import goblin.app.Alarm.model.dto.FCMResponseDto;
import goblin.app.Alarm.model.dto.FcmResponseDto;
import goblin.app.Alarm.service.FCMService;
import goblin.app.User.model.entity.User;
import goblin.app.User.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/fcm")
public class getTokenController {

    @Autowired
    private FCMService fcmService;

    // FCM TOKEN 서버 저장 API
    @PostMapping("/send")
    public ResponseEntity<FcmResponseDto> sendFcmToken(
            @RequestHeader(value = "Authorization", required = false) String bearerToken,
            @Valid @RequestBody FcmMessageDto requestDto) {
        try {
            // JWT 토큰에서 User 객체 추출
            User user = getUserFromToken(bearerToken);

            // 서비스 메서드를 호출하여 토큰을 저장
            FcmResponseDto responseDto = fcmService.getToken(user, requestDto);
            return ResponseEntity.ok(responseDto);  // 성공 시 200 OK와 함께 응답 반환
        } catch (RuntimeException e) {
            log.error("FCM 토큰 저장 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RuntimeException("Error: " + e.getMessage());
        }
    }

    // JWT 토큰에서 User 객체 추출하는 메서드
    private User getUserFromToken(String bearerToken) {
        String loginId = null;

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            JwtUtil jwtUtil;
            Claims claims = jwtUtil.getAllClaimsFromToken(token);

            // JWT에서 loginId를 추출하는 부분
            loginId = claims.getId(); // 보통 sub 필드에 loginId가 저장됨

            log.info("JWT Claims: {}", claims); // claims 전체 구조 확인
            log.info("추출된 로그인 ID: {}", loginId);
        }

        // 로그인 ID가 추출되었는지 확인하고 없으면 예외 처리
        if (loginId == null) {
            throw new RuntimeException("Authorization token is missing or invalid");
        }

        return userRepository
                .findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


}
