package me.yukitale.cryptoexchange.exchange.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterInvRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String email;

    @NotBlank
    private String fullName;

    @NotBlank
    private String phone;

    @NotBlank
    private String password;

    @NotBlank
    private String captcha;

    private String promocode;

    private String ref;
}
