package me.yukitale.cryptoexchange.exchange.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterInvRequest extends RegisterRequest {

    @NotBlank
    private String fullName;

    @NotBlank
    private String phone;
}
