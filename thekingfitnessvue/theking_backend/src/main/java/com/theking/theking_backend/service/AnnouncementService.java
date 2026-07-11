package com.theking.theking_backend.service;

import com.theking.theking_backend.common.PageResult;
import com.theking.theking_backend.entity.Announcement;
import com.theking.theking_backend.mapper.AnnouncementMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Autowired
    private OperationLogService operationLogService;

    public PageResult<Announcement> listAll(int page, int size) {
        int offset = (page - 1) * size;
        List<Announcement> list = announcementMapper.findAllOrdered(offset, size);
        long total = announcementMapper.countAll();
        return PageResult.success(list, total, page, size);
    }

    public PageResult<Announcement> listActive(int page, int size) {
        int offset = (page - 1) * size;
        List<Announcement> list = announcementMapper.findByStatusOrdered(1, offset, size);
        long total = announcementMapper.countByStatus(1);
        return PageResult.success(list, total, page, size);
    }

    public Optional<Announcement> getById(Long id) {
        return announcementMapper.findById(id);
    }

    public List<Announcement> getActiveForUser() {
        return announcementMapper.findActiveForUser(LocalDateTime.now());
    }

    public List<Announcement> getActiveForAdmin() {
        return announcementMapper.findActiveForAdmin(LocalDateTime.now());
    }

    @Transactional
    public Announcement create(Announcement announcement, Long adminId, String adminAccount) {
        announcement.setStatus(1);
        LocalDateTime now = LocalDateTime.now();
        announcement.setCreatedAt(now);
        announcement.setUpdatedAt(now);
        announcementMapper.insert(announcement);
        operationLogService.log(adminId, adminAccount, "CREATE", "SYSTEM", announcement.getId().toString(), "创建公告: " + announcement.getTitle());
        return announcement;
    }

    @Transactional
    public Announcement update(Long id, Announcement announcement, Long adminId, String adminAccount) {
        Announcement existing = announcementMapper.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
        existing.setTitle(announcement.getTitle());
        existing.setContent(announcement.getContent());
        existing.setType(announcement.getType());
        existing.setTarget(announcement.getTarget());
        existing.setSortOrder(announcement.getSortOrder());
        existing.setStartTime(announcement.getStartTime());
        existing.setEndTime(announcement.getEndTime());
        existing.setUpdatedAt(LocalDateTime.now());
        announcementMapper.update(existing);
        operationLogService.log(adminId, adminAccount, "UPDATE", "SYSTEM", existing.getId().toString(), "更新公告: " + existing.getTitle());
        return existing;
    }

    @Transactional
    public void delete(Long id, Long adminId, String adminAccount) {
        Announcement announcement = announcementMapper.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
        announcementMapper.deleteById(id);
        operationLogService.log(adminId, adminAccount, "DELETE", "SYSTEM", id.toString(), "删除公告: " + announcement.getTitle());
    }

    @Transactional
    public void toggleStatus(Long id, Long adminId, String adminAccount) {
        Announcement announcement = announcementMapper.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
        announcement.setStatus(announcement.getStatus() == 1 ? 0 : 1);
        announcement.setUpdatedAt(LocalDateTime.now());
        announcementMapper.update(announcement);
        String action = announcement.getStatus() == 1 ? "上架" : "下架";
        operationLogService.log(adminId, adminAccount, "UPDATE", "SYSTEM", id.toString(), action + "公告: " + announcement.getTitle());
    }
}
