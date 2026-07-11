package com.theking.theking_backend.mapper;

import com.theking.theking_backend.entity.SystemSetting;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface SystemSettingMapper {

    @Select("SELECT * FROM system_settings WHERE id = #{id}")
    Optional<SystemSetting> findById(Long id);

    @Select("SELECT * FROM system_settings WHERE setting_key = #{settingKey}")
    Optional<SystemSetting> findBySettingKey(String settingKey);

    @Select("SELECT * FROM system_settings WHERE category = #{category} ORDER BY setting_key ASC")
    List<SystemSetting> findByCategoryOrderBySettingKeyAsc(String category);

    @Select("SELECT * FROM system_settings ORDER BY category ASC, setting_key ASC")
    List<SystemSetting> findAllOrdered();

    @Insert("INSERT INTO system_settings (setting_key, setting_value, category, description, created_at, updated_at) " +
            "VALUES (#{settingKey}, #{settingValue}, #{category}, #{description}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SystemSetting systemSetting);

    @Update("UPDATE system_settings SET " +
            "setting_key = #{settingKey}, setting_value = #{settingValue}, category = #{category}, description = #{description}, updated_at = #{updatedAt} " +
            "WHERE id = #{id}")
    int update(SystemSetting systemSetting);

    @Delete("DELETE FROM system_settings WHERE id = #{id}")
    int deleteById(Long id);
}
