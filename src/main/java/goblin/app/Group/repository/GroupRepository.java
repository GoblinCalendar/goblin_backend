package goblin.app.Group.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import goblin.app.Group.model.entity.Group;
import goblin.app.User.model.entity.User;

public interface GroupRepository extends JpaRepository<Group, Long> {

  @Query("SELECT g FROM Group g WHERE g.createdBy = :user AND g.deleted = false")
  List<Group> findAllByUser(User user);

  // 삭제되지 않은 그룹 중 유저가 속한 그룹 조회 (GroupMember 테이블 조인)
  @Query(
      "SELECT g FROM Group g JOIN GroupMember gm ON g.groupId = gm.groupId WHERE gm.user = :user AND g.deleted = false")
  List<Group> findAllByUserAsMember(User user);

  // 삭제되지 않은 특정 그룹만 조회
  @Query("SELECT g FROM Group g WHERE g.groupId = :groupId AND g.deleted = false")
  Optional<Group> findByIdAndNotDeleted(Long groupId);

  boolean existsByGroupName(String groupName);
  // 그룹명을 기반으로 그룹을 조회하는 메서드
  Optional<Group> findByGroupName(String groupName);
}
