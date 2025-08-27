package com.kunfeng2002.be002.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Địa chỉ ví không được để trống")
    private String address;

    @NotBlank(message = "Thông điệp không được để trống")
    private String message;

    @NotBlank(message = "Chữ ký không được để trống")
    private String signature;
}

