package goblin.app.Group.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import goblin.app.Group.model.entity.GroupMember;
import goblin.app.User.model.entity.User;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
  Optional<GroupMember> findByGroupIdAndUser(Long groupId, User user); // User 객체로 검색

  List<GroupMember> findAllByUser(User user); // 유저로 그룹 멤버 조회

  Optional<GroupMember> findByGroupIdAndUser_LoginId(Long groupId, String loginId);

  List<GroupMember> findByGroupId(Long groupId);

  List<GroupMember> findByUser(User user);
}
