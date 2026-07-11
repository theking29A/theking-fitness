package com.theking.theking_backend.mapper;

import com.theking.theking_backend.entity.PlanExercise;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PlanExerciseMapper {

    @Select("SELECT * FROM plan_exercises WHERE id = #{id}")
    Optional<PlanExercise> findById(Long id);

    @Select("SELECT * FROM plan_exercises WHERE plan_id = #{planId} ORDER BY day_number ASC, order_index ASC")
    List<PlanExercise> findByPlanIdOrderByDayNumberAscOrderIndexAsc(Long planId);

    @Select("SELECT * FROM plan_exercises WHERE plan_id = #{planId} AND day_number = #{dayNumber} ORDER BY order_index ASC")
    List<PlanExercise> findByPlanIdAndDayNumberOrderByOrderIndexAsc(@Param("planId") Long planId, @Param("dayNumber") Integer dayNumber);

    @Select("SELECT COUNT(*) FROM plan_exercises WHERE plan_id = #{planId}")
    long countByPlanId(Long planId);

    @Insert("INSERT INTO plan_exercises (plan_id, exercise_id, day_number, sets, reps, rest_seconds, order_index, created_at) " +
            "VALUES (#{planId}, #{exerciseId}, #{dayNumber}, #{sets}, #{reps}, #{restSeconds}, #{orderIndex}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PlanExercise planExercise);

    @Update("UPDATE plan_exercises SET plan_id = #{planId}, exercise_id = #{exerciseId}, day_number = #{dayNumber}, " +
            "sets = #{sets}, reps = #{reps}, rest_seconds = #{restSeconds}, order_index = #{orderIndex} WHERE id = #{id}")
    int update(PlanExercise planExercise);

    @Delete("DELETE FROM plan_exercises WHERE id = #{id}")
    int deleteById(Long id);

    @Delete("DELETE FROM plan_exercises WHERE plan_id = #{planId}")
    int deleteByPlanId(Long planId);
}
