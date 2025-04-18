package me.yukitale.cryptoexchange.exchange.data;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TradeBotDTO {

    private Date startedAt;
    private TradeBotStatus status;
    private Double profit;
    private String firstCoinSymbol;
    private Double firstCoinAmount;
    private String secondCoinSymbol;
    private Double secondCoinAmount;
}
