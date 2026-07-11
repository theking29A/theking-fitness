package com.theking.theking_backend.service;

import com.theking.theking_backend.entity.OperationLog;
import com.theking.theking_backend.mapper.OperationLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OperationLogService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    public void log(Long adminId, String adminAccount, String operationType,
                    String targetType, String targetId, String detail) {
        OperationLog log = new OperationLog();
        log.setAdminId(adminId);
        log.setAdminAccount(adminAccount);
        log.setOperationType(operationType);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setDetail(detail);

        // 尝试获取请求信息
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                log.setIpAddress(getClientIp(request));
                log.setUserAgent(request.getHeader("User-Agent"));
            }
        } catch (Exception e) {
            // 忽略，可能不在请求上下文中
        }

        log.setCreatedAt(LocalDateTime.now());
        operationLogMapper.insert(log);
    }

    public Page<OperationLog> listLogs(Pageable pageable) {
        int offset = (int) pageable.getOffset();
        int pageSize = pageable.getPageSize();
        List<OperationLog> list = operationLogMapper.selectAll(offset, pageSize);
        long total = operationLogMapper.countAll();
        return new PageImpl<>(list, pageable, total);
    }

    public Page<OperationLog> listLogsByAdmin(Long adminId, Pageable pageable) {
        int offset = (int) pageable.getOffset();
        int pageSize = pageable.getPageSize();
        List<OperationLog> list = operationLogMapper.selectByAdminId(adminId, offset, pageSize);
        long total = operationLogMapper.countByAdminId(adminId);
        return new PageImpl<>(list, pageable, total);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果有多层代理，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
