package me.yukitale.cryptoexchange.exchange.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TradeBotOrderRequest {

    @NotBlank
    String firstCoinSymbol;

    @NotBlank
    double firstCoinAmount;

    @NotBlank
    String secondCoinSymbol;

    @NotBlank
    double secondCoinAmount;
}
