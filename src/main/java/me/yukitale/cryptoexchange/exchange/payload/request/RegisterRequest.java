package me.yukitale.cryptoexchange.exchange.payload.request;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank
    private String username;

    private String email;

    @NotBlank
    private String phone;

    @NotBlank
    private String password;

    private String captcha;

    private String promocode;

    private String ref;
}
