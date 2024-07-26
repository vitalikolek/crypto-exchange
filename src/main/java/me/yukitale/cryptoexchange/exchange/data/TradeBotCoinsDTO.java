package me.yukitale.cryptoexchange.exchange.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TradeBotCoinsDTO {

    private String firstCoinSymbol;
    private Double firstCoinAmount;
    private String secondCoinSymbol;
    private Double secondCoinAmount;
}
