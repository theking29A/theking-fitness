package com.theking.theking_backend.mapper;

import com.theking.theking_backend.entity.UserActivity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserActivityMapper {

    @Insert("INSERT INTO user_activities (user_id, activity_type, target_id, ip_address, created_at) " +
            "VALUES (#{userId}, #{activityType}, #{targetId}, #{ipAddress}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserActivity userActivity);

    @Select("SELECT * FROM user_activities WHERE id = #{id}")
    UserActivity selectById(Long id);

    @Select("SELECT * FROM user_activities ORDER BY created_at DESC")
    List<UserActivity> selectAll();

    // 统计查询在 XML 中实现
    Long countDailyActiveUsers(@Param("startTime") java.time.LocalDateTime startTime,
                               @Param("endTime") java.time.LocalDateTime endTime);

    Long countActiveUsersSince(@Param("startTime") java.time.LocalDateTime startTime);

    List<java.util.Map<String, Object>> getDailyActiveUsers(@Param("startTime") java.time.LocalDateTime startTime,
                                                            @Param("endTime") java.time.LocalDateTime endTime);

    List<java.util.Map<String, Object>> countByActivityTypeSince(@Param("startTime") java.time.LocalDateTime startTime);

    Long countUsersByActivityType(@Param("startTime") java.time.LocalDateTime startTime,
                                  @Param("activityType") String activityType);
}
