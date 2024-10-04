package goblin.app.Alarm.model.dto;

import lombok.*;

// 모바일에서 전달받은 객체

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FcmResponseDto {
    private String token;

    private String title;

    private String body;


    @Builder(toBuilder = true)
    public FcmResponseDto(String token, String title, String body) {
        this.token = token;
        this.title = title;
        this.body = body;
    }
}