package goblin.app.FixedSchedule.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import goblin.app.FixedSchedule.model.entity.FixedSchedule;
import goblin.app.Group.model.entity.Group;
import goblin.app.User.model.entity.User;

public interface FixedScheduleRepository extends JpaRepository<FixedSchedule, Long> {

  @Query("SELECT fs FROM FixedSchedule fs JOIN FETCH fs.dayOfWeek WHERE fs.user = :user")
  List<FixedSchedule> findByUser(@Param("user") User user);

  List<FixedSchedule> findByGroup(Group group);

  // 그룹과 일정 ID로 조회
  Optional<FixedSchedule> findByIdAndGroup_GroupId(Long id, Long groupId);
}
