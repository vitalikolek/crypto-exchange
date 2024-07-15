package me.yukitale.cryptoexchange.exchange.repository.user;

import me.yukitale.cryptoexchange.exchange.model.user.User;
import me.yukitale.cryptoexchange.exchange.model.user.UserTradeBotOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTradeBotOrderRepository extends JpaRepository<UserTradeBotOrder, Long> {

    List<UserTradeBotOrder> findTop10ByUserOrderByIdDesc(User user);
    Optional<UserTradeBotOrder> findTopByUserOrderByDateDesc(User user);
}
