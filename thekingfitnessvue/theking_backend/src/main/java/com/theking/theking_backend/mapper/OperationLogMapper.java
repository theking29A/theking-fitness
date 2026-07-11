package com.theking.theking_backend.mapper;

import com.theking.theking_backend.entity.OperationLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OperationLogMapper {

    @Insert("INSERT INTO operation_logs (admin_id, admin_account, operation_type, target_type, target_id, detail, ip_address, user_agent, created_at) " +
            "VALUES (#{adminId}, #{adminAccount}, #{operationType}, #{targetType}, #{targetId}, #{detail}, #{ipAddress}, #{userAgent}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OperationLog operationLog);

    @Select("SELECT * FROM operation_logs WHERE id = #{id}")
    OperationLog selectById(Long id);

    @Select("SELECT * FROM operation_logs ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}")
    List<OperationLog> selectAll(@Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM operation_logs")
    long countAll();

    @Select("SELECT * FROM operation_logs WHERE admin_id = #{adminId} ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}")
    List<OperationLog> selectByAdminId(@Param("adminId") Long adminId, @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM operation_logs WHERE admin_id = #{adminId}")
    long countByAdminId(Long adminId);

    @Select("SELECT * FROM operation_logs WHERE operation_type = #{operationType} ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}")
    List<OperationLog> selectByOperationType(@Param("operationType") String operationType, @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT * FROM operation_logs WHERE target_type = #{targetType} ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}")
    List<OperationLog> selectByTargetType(@Param("targetType") String targetType, @Param("offset") int offset, @Param("pageSize") int pageSize);

    // 复杂动态查询在 XML 中实现
    List<OperationLog> selectWithFilters(@Param("adminId") Long adminId,
                                         @Param("operationType") String operationType,
                                         @Param("targetType") String targetType,
                                         @Param("startTime") java.time.LocalDateTime startTime,
                                         @Param("endTime") java.time.LocalDateTime endTime,
                                         @Param("offset") int offset,
                                         @Param("pageSize") int pageSize);

    long countWithFilters(@Param("adminId") Long adminId,
                          @Param("operationType") String operationType,
                          @Param("targetType") String targetType,
                          @Param("startTime") java.time.LocalDateTime startTime,
                          @Param("endTime") java.time.LocalDateTime endTime);

    // 统计查询在 XML 中实现（GROUP BY）
    List<java.util.Map<String, Object>> countByOperationTypeSince(@Param("startTime") java.time.LocalDateTime startTime);

    List<java.util.Map<String, Object>> countByTargetTypeSince(@Param("startTime") java.time.LocalDateTime startTime);
}
