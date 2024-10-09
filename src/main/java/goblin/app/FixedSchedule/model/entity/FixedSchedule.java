package goblin.app.FixedSchedule.model.entity;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

import goblin.app.Group.model.entity.Group;
import goblin.app.User.model.entity.User;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "fixed_schedules")
public class FixedSchedule {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY) // 그룹과의 관계 설정
  @JoinColumn(name = "group_id")
  private Group group; // 추가된 그룹 필드

  @Column(nullable = false, length = 13)
  private String scheduleName;

  @ElementCollection(targetClass = DayOfWeek.class)
  @CollectionTable(name = "schedule_days", joinColumns = @JoinColumn(name = "schedule_id"))
  @Enumerated(EnumType.STRING)
  @Column(name = "day_of_week")
  private List<DayOfWeek> dayOfWeek;

  @Column(nullable = false)
  private LocalTime startTime;

  @Column(nullable = false)
  private LocalTime endTime;

  @Column(nullable = false, length = 6)
  private String color;

  @Column(name = "is_public")
  private boolean isPublic;

  public void updateTime(LocalTime startTime, LocalTime endTime) {
    this.startTime = startTime;
    this.endTime = endTime;
  }
}
