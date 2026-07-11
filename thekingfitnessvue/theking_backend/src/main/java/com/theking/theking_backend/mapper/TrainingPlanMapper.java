package com.theking.theking_backend.mapper;

import com.theking.theking_backend.entity.TrainingPlan;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface TrainingPlanMapper {

    @Select("SELECT * FROM training_plans WHERE id = #{id}")
    Optional<TrainingPlan> findById(Long id);

    @Select("SELECT * FROM training_plans WHERE status = #{status} ORDER BY sort_order ASC")
    List<TrainingPlan> findByStatusOrderBySortOrderAsc(Integer status);

    @Select("SELECT * FROM training_plans WHERE goal = #{goal} AND level = #{level} AND status = #{status}")
    List<TrainingPlan> findByGoalAndLevelAndStatus(String goal, String level, Integer status);

    @Select("SELECT * FROM training_plans WHERE goal = #{goal} AND status = #{status}")
    List<TrainingPlan> findByGoalAndStatus(String goal, Integer status);

    @Select("<script>" +
            "SELECT * FROM training_plans WHERE status = 1 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (name LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%'))" +
            "</if>" +
            "</script>")
    List<TrainingPlan> searchByKeyword(String keyword);

    @Select("SELECT COUNT(*) FROM training_plans WHERE status = #{status}")
    long countByStatus(Integer status);

    @Select("SELECT COUNT(*) FROM training_plans")
    long count();

    @Insert("INSERT INTO training_plans (name, description, goal, level, duration_weeks, days_per_week, " +
            "cover_image, estimated_time_min, status, sort_order, created_at, updated_at) " +
            "VALUES (#{name}, #{description}, #{goal}, #{level}, #{durationWeeks}, #{daysPerWeek}, " +
            "#{coverImage}, #{estimatedTimeMin}, #{status}, #{sortOrder}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TrainingPlan plan);

    @Update("UPDATE training_plans SET name = #{name}, description = #{description}, goal = #{goal}, " +
            "level = #{level}, duration_weeks = #{durationWeeks}, days_per_week = #{daysPerWeek}, " +
            "cover_image = #{coverImage}, estimated_time_min = #{estimatedTimeMin}, " +
            "sort_order = #{sortOrder}, updated_at = NOW() WHERE id = #{id}")
    int update(TrainingPlan plan);

    @Update("UPDATE training_plans SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Delete("DELETE FROM training_plans WHERE id = #{id}")
    int deleteById(Long id);
}
