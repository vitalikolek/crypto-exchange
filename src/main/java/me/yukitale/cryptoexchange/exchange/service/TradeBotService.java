package me.yukitale.cryptoexchange.exchange.service;

import lombok.AllArgsConstructor;
import me.yukitale.cryptoexchange.exchange.data.TradeBotDTO;
import me.yukitale.cryptoexchange.exchange.data.TradeBotStatus;
import me.yukitale.cryptoexchange.exchange.data.UserProfit;
import me.yukitale.cryptoexchange.exchange.model.Coin;
import me.yukitale.cryptoexchange.exchange.model.user.User;
import me.yukitale.cryptoexchange.exchange.model.user.UserTradeBotOrder;
import me.yukitale.cryptoexchange.exchange.payload.request.TradeBotOrderRequest;
import me.yukitale.cryptoexchange.exchange.repository.CoinRepository;
import me.yukitale.cryptoexchange.exchange.repository.user.UserBalanceRepository;
import me.yukitale.cryptoexchange.exchange.repository.user.UserRepository;
import me.yukitale.cryptoexchange.exchange.repository.user.UserTradeBotOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TradeBotService {

    private UserTradeBotOrderRepository userTradeBotOrderRepository;
    private CoinRepository coinRepository;
    private UserService userService;
    private UserRepository userRepository;
    private CoinService coinService;
    private UserBalanceRepository userBalanceRepository;

    private static final double MIN_CHANGE = -20.00;
    private static final double MAX_CHANGE = 99.99;
    private final Random random = new Random();

    public ResponseEntity<UserTradeBotOrder> generateTradeBotOrder(Authentication authentication, TradeBotOrderRequest request) {
        User user = userService.getUser(authentication);
        if(!user.isTradeBotWorking()) return new ResponseEntity<>(HttpStatusCode.valueOf(400));

        Optional<Coin> firstCoinOpt = coinRepository.findBySymbol(request.getFirstCoinSymbol());
        Optional<Coin> secondCoinOpt = coinRepository.findBySymbol(request.getSecondCoinSymbol());

        if (firstCoinOpt.isEmpty() || secondCoinOpt.isEmpty()) return null;

        Coin firstCoin = firstCoinOpt.get();
        Coin secondCoin = secondCoinOpt.get();

        if (userService.getBalance(user, firstCoin) == 0 || userService.getBalance(user, secondCoin) == 0) return new ResponseEntity<>(HttpStatusCode.valueOf(400));

        double changeInPercent = generateRandomNumber();
        double firstCoinPrice = coinService.getPrice(firstCoin);
        double secondCoinPrice = coinService.getPrice(secondCoin);

        userService.addBalance(user, request.getFirstCoinSymbol(), request.getFirstCoinAmount() * changeInPercent / 100);
        userService.addBalance(user, request.getSecondCoinSymbol(), request.getSecondCoinAmount() * changeInPercent / 100);

        UserTradeBotOrder order = new UserTradeBotOrder();
        order.setDate(new Date());
        order.setFirstCoin(firstCoin);
        order.setFirstCoinAmount(request.getFirstCoinAmount());
        order.setSecondCoin(secondCoin);
        order.setSecondCoinAmount(request.getSecondCoinAmount());
        order.setChangeInPercent(changeInPercent);
        order.setAmountInUSDT((firstCoinPrice * request.getFirstCoinAmount()) + (secondCoinPrice * request.getSecondCoinAmount()));
        order.setProfitInUSDT((firstCoinPrice * request.getFirstCoinAmount() * changeInPercent / 100) +
                (secondCoinPrice * request.getSecondCoinAmount() * changeInPercent / 100));
        order.setUser(user);
        userTradeBotOrderRepository.save(order);

        userService.setTradeBotWorking(user, true);

        user.setTradeBotProfit(user.getTradeBotProfit() + order.getProfitInUSDT());
        userRepository.save(user);

        return new ResponseEntity<>(order, HttpStatusCode.valueOf(200));
    }

    public List<UserTradeBotOrder> getTradeBotOrders(Authentication authentication) {
        User user = userService.getUser(authentication);
        return userTradeBotOrderRepository.findTop10ByUserOrderByIdDesc(user);
    }

    public void generateNumbersForAbsentTime(User user) {
        if(!user.isTradeBotWorking()) return;

        Optional<UserTradeBotOrder> lastGeneratedOrderOpt = userTradeBotOrderRepository.findTopByUserOrderByDateDesc(user);
        if (lastGeneratedOrderOpt.isEmpty()) return;

        UserTradeBotOrder lastGeneratedOrder = lastGeneratedOrderOpt.get();
        Date currentTime = new Date();
        Calendar nextGenerationTime = Calendar.getInstance();
        nextGenerationTime.setTime(lastGeneratedOrder.getDate());

        while (nextGenerationTime.getTime().before(currentTime)) {
            nextGenerationTime.add(Calendar.SECOND, random.nextInt(46) + 15);
            if (nextGenerationTime.getTime().after(currentTime)) break;

            double changeInPercent = generateRandomNumber();
            double firstCoinPrice = coinService.getPrice(lastGeneratedOrder.getFirstCoin());
            double secondCoinPrice = coinService.getPrice(lastGeneratedOrder.getSecondCoin());

            userService.addBalance(user, lastGeneratedOrder.getFirstCoin().getSymbol(),
                    lastGeneratedOrder.getFirstCoinAmount() * changeInPercent / 100);
            userService.addBalance(user, lastGeneratedOrder.getSecondCoin().getSymbol(),
                    lastGeneratedOrder.getSecondCoinAmount() * changeInPercent / 100);

            UserTradeBotOrder order = new UserTradeBotOrder();
            order.setDate(nextGenerationTime.getTime());
            order.setFirstCoin(lastGeneratedOrder.getFirstCoin());
            order.setFirstCoinAmount(lastGeneratedOrder.getFirstCoinAmount());
            order.setSecondCoin(lastGeneratedOrder.getSecondCoin());
            order.setSecondCoinAmount(lastGeneratedOrder.getSecondCoinAmount());
            order.setChangeInPercent(changeInPercent);
            order.setAmountInUSDT((firstCoinPrice * lastGeneratedOrder.getFirstCoinAmount()) + (secondCoinPrice * lastGeneratedOrder.getSecondCoinAmount()));
            order.setProfitInUSDT((firstCoinPrice * lastGeneratedOrder.getFirstCoinAmount() * changeInPercent / 100) +
                    (secondCoinPrice * lastGeneratedOrder.getSecondCoinAmount() * changeInPercent / 100));
            order.setUser(user);
            userTradeBotOrderRepository.save(order);
        }
    }

    private double generateRandomNumber() {
        double randomNumber = MIN_CHANGE + (MAX_CHANGE - MIN_CHANGE) * random.nextDouble();
        return Math.round(randomNumber * 100.0) / 100.0;
    }

    public TradeBotDTO getBotDTO(Authentication authentication) {
        User user = userService.getUser(authentication);
        Date startedAt = user.getTradeBotStarted();
        TradeBotStatus status = user.isTradeBotWorking() ? TradeBotStatus.STARTED: TradeBotStatus.STOPPED;
        double profit = user.getTradeBotProfit();

        return new TradeBotDTO(startedAt, status, profit);
    }

    public List<UserProfit> getRandomProfits() {
        int userCount = 20;
        return userService.findRandomUsers(userCount).stream()
                .map(user -> new UserProfit(user.getUsername(), generateRandomNumber()))
                .collect(Collectors.toList());
    }

    public TradeBotDTO startGenerating(Authentication authentication) {
        User user = userService.getUser(authentication);
        user.setTradeBotWorking(true);
        user.setTradeBotStarted(new Date());

        userRepository.save(user);

        return getBotDTO(authentication);
    }

    public void stopGenerating(Authentication authentication) {
        User user = userService.getUser(authentication);
        user.setTradeBotWorking(false);
        user.setTradeBotStarted(null);
        user.setTradeBotProfit(0);

        userRepository.save(user);
    }

    public double getCoinBalance(Authentication authentication, String coinSymbol) {
        User user = userService.getUser(authentication);
        Coin coin = coinRepository.findBySymbol(coinSymbol)
                .orElseThrow(IllegalArgumentException::new);

        return userService.getBalance(user, coin);
    }
}