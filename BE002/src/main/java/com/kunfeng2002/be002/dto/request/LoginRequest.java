package com.kunfeng2002.be002.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @Size(min = 42, max = 42, message = "Address must have length 42")
    private String address;

    @NotBlank(message = "Message must not be null")
    private String message;

    @Size(min = 130, max = 130, message = "Signature must have length 130")
    private String signature;
}

