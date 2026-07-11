package com.theking.theking_backend.mapper;

import com.theking.theking_backend.entity.Exercise;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ExerciseMapper {

    @Select("SELECT * FROM exercises WHERE id = #{id}")
    Optional<Exercise> findById(Long id);

    @Select("SELECT * FROM exercises WHERE status = #{status} ORDER BY sort_order ASC")
    List<Exercise> findByStatusOrderBySortOrderAsc(Integer status);

    @Select("SELECT * FROM exercises WHERE category = #{category} AND status = #{status} ORDER BY sort_order ASC")
    List<Exercise> findByCategoryAndStatusOrderBySortOrderAsc(String category, Integer status);

    @Select("SELECT * FROM exercises WHERE category = #{category} AND status = #{status}")
    List<Exercise> findByCategoryAndStatus(String category, Integer status);

    @Select("<script>" +
            "SELECT * FROM exercises WHERE status = 1 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (name LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%') OR target_muscles LIKE CONCAT('%', #{keyword}, '%'))" +
            "</if>" +
            "</script>")
    List<Exercise> searchByKeyword(String keyword);

    @Select("SELECT category, COUNT(*) as cnt FROM exercises WHERE status = 1 GROUP BY category")
    List<java.util.Map<String, Object>> countByCategory();

    @Select("SELECT COUNT(*) FROM exercises WHERE status = #{status}")
    long countByStatus(Integer status);

    @Select("SELECT COUNT(*) FROM exercises")
    long count();

    @Insert("INSERT INTO exercises (name, description, category, target_muscles, difficulty, equipment, " +
            "video_url, image_url, tips, calories_per_min, duration_seconds, status, sort_order, created_at, updated_at) " +
            "VALUES (#{name}, #{description}, #{category}, #{targetMuscles}, #{difficulty}, #{equipment}, " +
            "#{videoUrl}, #{imageUrl}, #{tips}, #{caloriesPerMin}, #{durationSeconds}, #{status}, #{sortOrder}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Exercise exercise);

    @Update("UPDATE exercises SET name = #{name}, description = #{description}, category = #{category}, " +
            "target_muscles = #{targetMuscles}, difficulty = #{difficulty}, equipment = #{equipment}, " +
            "video_url = #{videoUrl}, image_url = #{imageUrl}, tips = #{tips}, " +
            "calories_per_min = #{caloriesPerMin}, duration_seconds = #{durationSeconds}, " +
            "sort_order = #{sortOrder}, updated_at = NOW() WHERE id = #{id}")
    int update(Exercise exercise);

    @Update("UPDATE exercises SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Delete("DELETE FROM exercises WHERE id = #{id}")
    int deleteById(Long id);
}
