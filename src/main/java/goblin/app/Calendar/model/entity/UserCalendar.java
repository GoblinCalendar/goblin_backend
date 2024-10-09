package goblin.app.Calendar.model.entity;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;

import goblin.app.Common.config.BooleanToYNConverter;
import goblin.app.Group.model.entity.Group;
import goblin.app.User.model.entity.User;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_calendar")
public class UserCalendar {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY) // 그룹과 연동
  @JoinColumn(name = "group_id")
  private Group group; // 개인 그룹 추가

  @Column(nullable = false)
  private String title;

  @Column(nullable = true)
  @Lob
  private String note;

  @Column(name = "start_time")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime startTime;

  @Column(name = "end_time")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime endTime;

  @Convert(converter = BooleanToYNConverter.class)
  private Boolean deleted = false;

  // 시간블럭 색상코드
  @Column(name = "color", nullable = false)
  private String color = "CCD7E5";

  @Builder
  public UserCalendar(
      User user,
      String title,
      String note,
      LocalDateTime startTime,
      LocalDateTime endTime,
      Group group) {
    this.user = user;
    this.title = title;
    this.note = note;
    this.startTime = startTime;
    this.endTime = endTime;
    this.group = group; // 그룹 필드 추가
    this.color = "CCD7E5";
  }

  public void update(Long id, String title, LocalDateTime startTime, LocalDateTime endTime) {
    this.id = id;
    this.title = title;
    this.startTime = startTime;
    this.endTime = endTime;
  }
}
