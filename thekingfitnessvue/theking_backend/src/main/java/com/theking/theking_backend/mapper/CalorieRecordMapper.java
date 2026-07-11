package com.theking.theking_backend.mapper;

import com.theking.theking_backend.entity.CalorieRecord;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CalorieRecordMapper {

    @Select("SELECT * FROM calorie_records WHERE id = #{id}")
    CalorieRecord findById(Long id);

    @Select("SELECT * FROM calorie_records WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<CalorieRecord> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Select("SELECT * FROM calorie_records WHERE user_id = #{userId} AND created_at > #{after} ORDER BY created_at DESC")
    List<CalorieRecord> findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(@Param("userId") Long userId, @Param("after") LocalDateTime after);

    @Insert("INSERT INTO calorie_records (user_id, weight, height, age, gender, activity, bmr, tdee, recommendation, created_at) " +
            "VALUES (#{userId}, #{weight}, #{height}, #{age}, #{gender}, #{activity}, #{bmr}, #{tdee}, #{recommendation}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CalorieRecord record);

    @Update("UPDATE calorie_records SET " +
            "user_id = #{userId}, weight = #{weight}, height = #{height}, age = #{age}, " +
            "gender = #{gender}, activity = #{activity}, bmr = #{bmr}, tdee = #{tdee}, recommendation = #{recommendation} " +
            "WHERE id = #{id}")
    int update(CalorieRecord record);

    @Delete("DELETE FROM calorie_records WHERE id = #{id}")
    int deleteById(Long id);
}
