package me.yukitale.cryptoexchange.exchange.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.yukitale.cryptoexchange.exchange.model.Coin;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

import java.util.Date;

@Entity
@Table(name = "user_trade_bot_orders")
@Getter
@Setter
@NoArgsConstructor
public class UserTradeBotOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(nullable = false)
    private Date date;

    @ManyToOne(fetch = FetchType.EAGER)
    private Coin firstCoin;

    @Column(nullable = false)
    private double firstCoinAmount;

    @ManyToOne(fetch = FetchType.EAGER)
    private Coin secondCoin;

    @Column(nullable = false)
    private double secondCoinAmount;

    @Column(nullable = false)
    private double changeInPercent;

    @Column(nullable = false)
    private double amountInUSDT;

    @Column(nullable = false)
    private double profitInUSDT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
}
