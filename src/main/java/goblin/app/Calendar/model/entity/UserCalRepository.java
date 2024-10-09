package goblin.app.Calendar.model.entity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import goblin.app.Group.model.entity.Group;
import goblin.app.User.model.entity.User;

public interface UserCalRepository extends JpaRepository<UserCalendar, Long> {
  @Query(
      "SELECT u FROM UserCalendar u WHERE FUNCTION('YEAR', u.startTime) = :year "
          + "AND FUNCTION('MONTH', u.startTime) = :month "
          + "AND u.user = :user")
  List<UserCalendar> findByYearAndMonth(
      @Param("year") int year, @Param("month") int month, @Param("user") User user);

  @Query(
      "SELECT u FROM UserCalendar u WHERE FUNCTION('YEAR', u.startTime) = :year "
          + "AND FUNCTION('MONTH', u.startTime) = :month "
          + "AND FUNCTION('DAY', u.startTime) = :day "
          + "AND u.user = :user")
  List<UserCalendar> findByDay(
      @Param("year") int year,
      @Param("month") int month,
      @Param("day") int day,
      @Param("user") User user);

  List<UserCalendar> findByTitleContainingAndUser(String title, User user);
  // 제목에 키워드를 포함하고, 해당 유저의 일정 중에서 삭제되지 않은 것만 검색
  List<UserCalendar> findByTitleContainingAndUserAndDeletedFalse(String title, User user);

  @Query(
      "SELECT u FROM UserCalendar u WHERE FUNCTION('YEAR', u.startTime) = :year "
          + "AND FUNCTION('MONTH', u.startTime) = :month "
          + "AND u.user = :user "
          + "AND u.group = :group")
  List<UserCalendar> findByYearAndMonthAndGroup(
      @Param("year") int year,
      @Param("month") int month,
      @Param("user") User user,
      @Param("group") Group group);

  @Query(
      "SELECT u FROM UserCalendar u WHERE FUNCTION('YEAR', u.startTime) = :year "
          + "AND FUNCTION('MONTH', u.startTime) = :month "
          + "AND FUNCTION('DAY', u.startTime) = :day "
          + "AND u.user = :user "
          + "AND u.group = :group")
  List<UserCalendar> findByDayAndGroup(
      @Param("year") int year,
      @Param("month") int month,
      @Param("day") int day,
      @Param("user") User user,
      @Param("group") Group group);
}
