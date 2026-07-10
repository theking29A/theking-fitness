package com.theking.theking_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SendCodeRequest {
    @NotBlank(message = "账号不能为空")
    private String account;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "类型不能为空")
    @Pattern(regexp = "register|forgot", message = "类型只能是 register 或 forgot")
    private String type;
}
