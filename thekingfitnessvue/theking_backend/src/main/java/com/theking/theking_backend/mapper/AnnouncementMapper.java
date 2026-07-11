package com.theking.theking_backend.mapper;

import com.theking.theking_backend.entity.Announcement;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface AnnouncementMapper {

    @Select("SELECT * FROM announcements WHERE id = #{id}")
    Optional<Announcement> findById(Long id);

    @Select("SELECT * FROM announcements ORDER BY sort_order ASC, created_at DESC LIMIT #{offset}, #{pageSize}")
    List<Announcement> findAllOrdered(@Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM announcements")
    long countAll();

    @Select("SELECT * FROM announcements WHERE status = #{status} ORDER BY sort_order ASC, created_at DESC LIMIT #{offset}, #{pageSize}")
    List<Announcement> findByStatusOrdered(@Param("status") Integer status, @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM announcements WHERE status = #{status}")
    long countByStatus(Integer status);

    @Insert("INSERT INTO announcements (title, content, type, target, status, sort_order, start_time, end_time, created_at, updated_at) " +
            "VALUES (#{title}, #{content}, #{type}, #{target}, #{status}, #{sortOrder}, #{startTime}, #{endTime}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Announcement announcement);

    @Update("UPDATE announcements SET " +
            "title = #{title}, content = #{content}, type = #{type}, target = #{target}, " +
            "status = #{status}, sort_order = #{sortOrder}, start_time = #{startTime}, end_time = #{endTime}, updated_at = #{updatedAt} " +
            "WHERE id = #{id}")
    int update(Announcement announcement);

    @Delete("DELETE FROM announcements WHERE id = #{id}")
    int deleteById(Long id);

    // 复杂动态查询在 XML 中实现
    List<Announcement> findActiveForUser(@Param("now") LocalDateTime now);

    List<Announcement> findActiveForAdmin(@Param("now") LocalDateTime now);
}
