package com.theking.theking_backend.service;

import com.theking.theking_backend.entity.Announcement;
import com.theking.theking_backend.repository.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AnnouncementService {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private OperationLogService operationLogService;

    public Page<Announcement> listAll(Pageable pageable) {
        return announcementRepository.findAllByOrderBySortOrderAscCreatedAtDesc(pageable);
    }

    public Page<Announcement> listActive(Pageable pageable) {
        return announcementRepository.findByStatusOrderBySortOrderAscCreatedAtDesc(1, pageable);
    }

    public Optional<Announcement> getById(Long id) {
        return announcementRepository.findById(id);
    }

    public List<Announcement> getActiveForUser() {
        return announcementRepository.findActiveForUser(LocalDateTime.now());
    }

    public List<Announcement> getActiveForAdmin() {
        return announcementRepository.findActiveForAdmin(LocalDateTime.now());
    }

    @Transactional
    public Announcement create(Announcement announcement, Long adminId, String adminAccount) {
        announcement.setStatus(1);
        Announcement saved = announcementRepository.save(announcement);
        operationLogService.log(adminId, adminAccount, "CREATE", "SYSTEM", saved.getId().toString(), "创建公告: " + saved.getTitle());
        return saved;
    }

    @Transactional
    public Announcement update(Long id, Announcement announcement, Long adminId, String adminAccount) {
        Announcement existing = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
        existing.setTitle(announcement.getTitle());
        existing.setContent(announcement.getContent());
        existing.setType(announcement.getType());
        existing.setTarget(announcement.getTarget());
        existing.setSortOrder(announcement.getSortOrder());
        existing.setStartTime(announcement.getStartTime());
        existing.setEndTime(announcement.getEndTime());
        Announcement saved = announcementRepository.save(existing);
        operationLogService.log(adminId, adminAccount, "UPDATE", "SYSTEM", saved.getId().toString(), "更新公告: " + saved.getTitle());
        return saved;
    }

    @Transactional
    public void delete(Long id, Long adminId, String adminAccount) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
        announcementRepository.delete(announcement);
        operationLogService.log(adminId, adminAccount, "DELETE", "SYSTEM", id.toString(), "删除公告: " + announcement.getTitle());
    }

    @Transactional
    public void toggleStatus(Long id, Long adminId, String adminAccount) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
        announcement.setStatus(announcement.getStatus() == 1 ? 0 : 1);
        announcementRepository.save(announcement);
        String action = announcement.getStatus() == 1 ? "上架" : "下架";
        operationLogService.log(adminId, adminAccount, "UPDATE", "SYSTEM", id.toString(), action + "公告: " + announcement.getTitle());
    }
}
