package com.theking.theking_backend.mapper;

import com.theking.theking_backend.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM users WHERE id = #{id}")
    Optional<User> findById(Long id);

    @Select("SELECT * FROM users WHERE account = #{account}")
    Optional<User> findByAccount(String account);

    @Select("SELECT * FROM users WHERE email = #{email}")
    Optional<User> findByEmail(String email);

    @Select("SELECT COUNT(*) > 0 FROM users WHERE account = #{account}")
    boolean existsByAccount(String account);

    @Select("SELECT COUNT(*) > 0 FROM users WHERE email = #{email}")
    boolean existsByEmail(String email);

    @Select("SELECT COUNT(*) FROM users")
    long count();

    @Select("SELECT COUNT(*) FROM users WHERE role = #{role}")
    long countByRole(User.Role role);

    @Select("SELECT COUNT(*) FROM users WHERE created_at >= #{dateTime}")
    long countByCreatedAtGreaterThanEqual(java.time.LocalDateTime dateTime);

    @Select("SELECT COUNT(*) FROM users WHERE created_at BETWEEN #{start} AND #{end}")
    long countByCreatedAtBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);

    @Select("SELECT * FROM users ORDER BY id DESC LIMIT #{offset}, #{size}")
    List<User> findAllWithPagination(@Param("offset") int offset, @Param("size") int size);

    @Select("<script>" +
            "SELECT * FROM users " +
            "<where>" +
            "  <if test='keyword != null and keyword != \"\"'>" +
            "    AND (account LIKE CONCAT('%', #{keyword}, '%') OR nickname LIKE CONCAT('%', #{keyword}, '%'))" +
            "  </if>" +
            "</where>" +
            "ORDER BY id DESC " +
            "LIMIT #{offset}, #{size}" +
            "</script>")
    List<User> searchByKeyword(@Param("keyword") String keyword, @Param("offset") int offset, @Param("size") int size);

    @Select("<script>" +
            "SELECT COUNT(*) FROM users " +
            "<where>" +
            "  <if test='keyword != null and keyword != \"\"'>" +
            "    AND (account LIKE CONCAT('%', #{keyword}, '%') OR nickname LIKE CONCAT('%', #{keyword}, '%'))" +
            "  </if>" +
            "</where>" +
            "</script>")
    long countByKeyword(@Param("keyword") String keyword);

    @Insert("INSERT INTO users (account, password, nickname, email, role, created_at) " +
            "VALUES (#{account}, #{password}, #{nickname}, #{email}, #{role}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);

    @Update("UPDATE users SET " +
            "account = #{account}, " +
            "password = #{password}, " +
            "nickname = #{nickname}, " +
            "email = #{email}, " +
            "role = #{role} " +
            "WHERE id = #{id}")
    void update(User user);

    @Delete("DELETE FROM users WHERE id = #{id}")
    void deleteById(Long id);
}
