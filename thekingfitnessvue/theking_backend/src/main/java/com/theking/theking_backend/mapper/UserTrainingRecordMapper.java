package com.theking.theking_backend.mapper;

import com.theking.theking_backend.entity.UserTrainingRecord;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserTrainingRecordMapper {

    @Select("SELECT * FROM user_training_records WHERE id = #{id}")
    UserTrainingRecord findById(Long id);

    @Select("SELECT * FROM user_training_records WHERE user_id = #{userId} ORDER BY completed_at DESC LIMIT #{offset}, #{pageSize}")
    List<UserTrainingRecord> findByUserIdOrderByCompletedAtDesc(@Param("userId") Long userId, @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT * FROM user_training_records WHERE user_id = #{userId} AND completed_at BETWEEN #{start} AND #{end} ORDER BY completed_at DESC")
    List<UserTrainingRecord> findByUserIdAndCompletedAtBetweenOrderByCompletedAtDesc(
            @Param("userId") Long userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Select("SELECT COUNT(*) FROM user_training_records WHERE user_id = #{userId}")
    Long countByUserId(Long userId);

    @Select("SELECT COALESCE(SUM(calories_burned), 0) FROM user_training_records WHERE user_id = #{userId}")
    Integer sumCaloriesByUserId(Long userId);

    @Select("SELECT COALESCE(SUM(duration_seconds), 0) FROM user_training_records WHERE user_id = #{userId}")
    Integer sumDurationByUserId(Long userId);

    List<Map<String, Object>> getDailyStatsByUserId(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime);

    List<Map<String, Object>> getFavoriteExercisesByUserId(Long userId);

    @Insert("INSERT INTO user_training_records (user_id, plan_id, exercise_id, day_number, sets_completed, reps_completed, duration_seconds, calories_burned, notes, completed_at, created_at) " +
            "VALUES (#{userId}, #{planId}, #{exerciseId}, #{dayNumber}, #{setsCompleted}, #{repsCompleted}, #{durationSeconds}, #{caloriesBurned}, #{notes}, #{completedAt}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserTrainingRecord record);

    @Update("UPDATE user_training_records SET " +
            "user_id = #{userId}, plan_id = #{planId}, exercise_id = #{exerciseId}, day_number = #{dayNumber}, " +
            "sets_completed = #{setsCompleted}, reps_completed = #{repsCompleted}, duration_seconds = #{durationSeconds}, " +
            "calories_burned = #{caloriesBurned}, notes = #{notes}, completed_at = #{completedAt} " +
            "WHERE id = #{id}")
    int update(UserTrainingRecord record);

    @Delete("DELETE FROM user_training_records WHERE id = #{id}")
    int deleteById(Long id);
}
