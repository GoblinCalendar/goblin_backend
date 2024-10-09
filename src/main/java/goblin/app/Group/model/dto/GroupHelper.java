package goblin.app.Group.model.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import goblin.app.Group.model.entity.Group;
import goblin.app.Group.repository.GroupRepository;
import goblin.app.User.model.entity.User;

@Component
public class GroupHelper {

  private final GroupRepository groupRepository;

  @Autowired
  public GroupHelper(GroupRepository groupRepository) {
    this.groupRepository = groupRepository;
  }

  public Group getOrCreatePersonalGroup(User user) {
    return groupRepository
        .findByGroupName("개인")
        .orElseGet(
            () -> {
              Group personalGroup = new Group();
              personalGroup.setGroupName("개인");
              personalGroup.setCreatedBy(user);
              groupRepository.save(personalGroup);
              return personalGroup;
            });
  }
}
