package me.yukitale.cryptoexchange.exchange.controller.other;

import me.yukitale.cryptoexchange.exchange.model.user.UserSupportDialog;
import me.yukitale.cryptoexchange.exchange.repository.user.UserSupportDialogRepository;
import me.yukitale.cryptoexchange.panel.admin.model.payments.PaymentSettings;
import me.yukitale.cryptoexchange.panel.admin.repository.payments.PaymentSettingsRepository;
import me.yukitale.cryptoexchange.panel.worker.model.FastPump;
import me.yukitale.cryptoexchange.panel.worker.model.StablePump;
import me.yukitale.cryptoexchange.panel.worker.repository.FastPumpRepository;
import me.yukitale.cryptoexchange.utils.JsonUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import me.yukitale.cryptoexchange.exchange.model.Coin;
import me.yukitale.cryptoexchange.exchange.model.user.User;
import me.yukitale.cryptoexchange.exchange.repository.CoinRepository;
import me.yukitale.cryptoexchange.exchange.service.CoinService;
import me.yukitale.cryptoexchange.exchange.service.UserService;
import me.yukitale.cryptoexchange.panel.admin.model.other.AdminLegalSettings;
import me.yukitale.cryptoexchange.panel.admin.model.other.AdminSettings;
import me.yukitale.cryptoexchange.panel.admin.repository.other.AdminLegalSettingsRepository;
import me.yukitale.cryptoexchange.panel.admin.repository.other.AdminSettingsRepository;
import me.yukitale.cryptoexchange.panel.common.model.DepositCoin;
import me.yukitale.cryptoexchange.panel.common.service.DepositCoinService;
import me.yukitale.cryptoexchange.panel.worker.model.Domain;
import me.yukitale.cryptoexchange.panel.worker.model.Worker;
import me.yukitale.cryptoexchange.panel.worker.model.settings.other.WorkerLegalSettings;
import me.yukitale.cryptoexchange.panel.worker.repository.DomainRepository;
import me.yukitale.cryptoexchange.panel.worker.repository.StablePumpRepository;
import me.yukitale.cryptoexchange.panel.worker.service.WorkerService;
import me.yukitale.cryptoexchange.utils.MyDecimal;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@PreAuthorize("hasRole('ROLE_USER') || hasRole('ROLE_WORKER') || hasRole('ROLE_ADMIN') || hasRole('ROLE_SUPPORTER')")
public class ExchangeController {
    
    @Autowired
    private AdminSettingsRepository adminSettingsRepository;

    @Autowired
    private AdminLegalSettingsRepository adminLegalSettingsRepository;

    @Autowired
    private PaymentSettingsRepository paymentSettingsRepository;

    @Autowired
    private CoinRepository coinRepository;

    @Autowired
    private FastPumpRepository fastPumpRepository;

    @Autowired
    private StablePumpRepository stablePumpRepository;

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private UserSupportDialogRepository userSupportDialogRepository;

    @Autowired
    private DepositCoinService depositCoinService;

    @Autowired
    private UserService userService;

    @Autowired
    private WorkerService workerService;

    @Autowired
    private CoinService coinService;

    @GetMapping(value = "aml-kyc-policy")
    public String amlController(Model model, Authentication authentication, HttpServletRequest request, @RequestHeader("host") String host) {
        userService.createAction(authentication, request, "Go to the /aml-kyc-policy");

        addDomainInfoAttribute(model, host);
        addPaymentSettings(model);

        User user = addUserAttribute(model, authentication);

        String aml;

        Worker worker = user.getWorker();

        if (worker != null) {
            WorkerLegalSettings workerLegalSettings = worker.getLegalSettings();
            aml = workerLegalSettings.getAml();
        } else {
            AdminLegalSettings adminLegalSettings = adminLegalSettingsRepository.findFirst();
            aml = adminLegalSettings.getAml();
        }

        String domainName = (String) model.getAttribute("site_name");
        String domainUrl = (String) model.getAttribute("site_domain");

        aml = aml.replace("{domain_url}", domainUrl).replace("{domain_name}", domainName);

        model.addAttribute("aml", aml);

        return "aml-kyc-policy";
    }

    @GetMapping(value = "bugbounty")
    public String bugBountyController(Model model, Authentication authentication, HttpServletRequest request, @RequestHeader("host") String host) {
        userService.createAction(authentication, request, "Go to the /bugbounty");

        addDomainInfoAttribute(model, host);
        addPaymentSettings(model);

        addUserAttribute(model, authentication);

        return "bugbounty";
    }

    @GetMapping(value = "cookies-policy")
    public String cookiesPolicyController(Model model, Authentication authentication, HttpServletRequest request, @RequestHeader("host") String host) {
        userService.createAction(authentication, request, "Go to the /cookies-policy");

        addDomainInfoAttribute(model, host);
        addPaymentSettings(model);

        addUserAttribute(model, authentication);

        return "cookies-policy";
    }

    @GetMapping(value = "cross-rates")
    public String crossRatesController(Model model, Authentication authentication, HttpServletRequest request, @RequestHeader("host") String host) {
        userService.createAction(authentication, request, "Go to the /cross-rates");

        addDomainInfoAttribute(model, host);
        addPaymentSettings(model);

        addUserAttribute(model, authentication);

        return "cross-rates";
    }

    @GetMapping(value = "fees")
    public String feesController(Model model, Authentication authentication, HttpServletRequest request, @RequestHeader("host") String host) {
        userService.createAction(authentication, request, "Go to the /fees");

        addDomainInfoAttribute(model, host);
        addPaymentSettings(model);

        User user = addUserAttribute(model, authentication);

        List<? extends DepositCoin> depositCoins = depositCoinService.getDepositCoins(user).stream().filter(DepositCoin::isEnabled).collect(Collectors.toList());

        model.addAttribute("deposit_coins", depositCoins);

        model.addAttribute("coin_service", coinService);

        return "fees";
    }

    @GetMapping(value = "heat-map")
    public String heatMapController(Model model, Authentication authentication, HttpServletRequest request, @RequestHeader("host") String host) {
        userService.createAction(authentication, request, "Go to the /heat-map");

        addDomainInfoAttribute(model, host);
        addPaymentSettings(model);

        addUserAttribute(model, authentication);

        return "heat-map";
    }

    @GetMapping(value = "indices")
    public String indicesController(Model model, Authentication authentication, HttpServletRequest request, @RequestHeader("host") String host) {
        userService.createAction(authentication, request, "Go to the /indices");

        addDomainInfoAttribute(model, host);
        addPaymentSettings(model);

        addUserAttribute(model, authentication);

        return "indices";
    }

    @GetMapping(value = "law")
    public String lawController(Model model, Authentication authentication, HttpServletRequest request, @RequestHeader("host") String host) {
        userService.createAction(authentication, request, "Go to the /law");

        addDomainInfoAttribute(model, host);
        addPaymentSettings(model);

        addUserAttribute(model, authentication);

        return "law";
    }

    @GetMapping(value = "market-crypto")
    public String marketCryptoController(Model model, Authentication authentication, HttpServletRequest request, @RequestHeader("host") String host) {
        userService.createAction(authentication, request, "Go to the /market-crypto");

        addDomainInfoAttribute(model, host);
        addPaymentSettings(model);

        addUserAttribute(model, authentication);

        return "market-crypto";
    }

    @GetMapping(value = "market-screener")
    public String marketScreenerController(Model model, Authentication authentication, HttpServletRequest request, @RequestHeader("host") String host) {
        userService.createAction(authentication, request, "Go to the /market-screener");

        addDomainInfoAttribute(model, host);
        addPaymentSettings(model);

        addUserAttribute(model, authentication);

        return "market-screener";
    }

    @GetMapping(value = "privacy-notice")
    public String privacyNoticeController(Model model, Authentication authentication, HttpServletRequest request, @RequestHeader("host") String host) {
        userService.createAction(authentication, request, "Go to the /privacy-notice");

        addDomainInfoAttribute(model, host);
        addPaymentSettings(model);

        addUserAttribute(model, authentication);

        return "privacy-notice";
    }

    @GetMapping(value = "regulatory")
    public String regulatoryController(Model model, Authentication authentication, HttpServletRequest request, @RequestHeader("host") String host) {
        userService.createAction(authentication, request, "Go to the /regulatory");

        addDomainInfoAttribute(model, host);
        addPaymentSettings(model);

        addUserAttribute(model, authentication);

        return "regulatory";
    }

    @GetMapping(value = "risk")
    public String riskController(Model model, Authentication authentication, HttpServletRequest request, @RequestHeader("host") String host) {
        userService.createAction(authentication, request, "Go to the /risk");

        addDomainInfoAttribute(model, host);
        addPaymentSettings(model);

        addUserAttribute(model, authentication);

        return "risk";
    }

    @GetMapping(value = "technical-analysis")
    public String technicalAnalysisController(Model model, Authentication authentication, HttpServletRequest request, @RequestHeader("host") String host) {
        userService.createAction(authentication, request, "Go to the /technical-analysis");

        addDomainInfoAttribute(model, host);
        addPaymentSettings(model);

        addUserAttribute(model, authentication);

        return "technical-analysis";
    }

    @GetMapping(value = "terms")
    public String termsController(Model model, Authentication authentication, HttpServletRequest request, @RequestHeader("host") String host) {
        userService.createAction(authentication, request, "Go to the /terms");

        addDomainInfoAttribute(model, host);
        addPaymentSettings(model);

        User user = addUserAttribute(model, authentication);

        String terms;

        Worker worker = user.getWorker();

        if (worker != null) {
            WorkerLegalSettings workerLegalSettings = worker.getLegalSettings();
            terms = workerLegalSettings.getTerms();
        } else {
            AdminLegalSettings adminLegalSettings = adminLegalSettingsRepository.findFirst();
            terms = adminLegalSettings.getTerms();
        }

        String domainName = (String) model.getAttribute("site_name");
        String domainUrl = (String) model.getAttribute("site_domain");

        terms = terms.replace("{domain_url}", domainUrl).replace("{domain_name}", domainName);

        model.addAttribute("terms", terms);

        return "terms";
    }

    @GetMapping(value = "/trade-bot")
    public String tradeBotController(Model model, Authentication authentication, HttpServletRequest request, @RequestParam(value = "currency", required = false) String coinSymbol, @RequestHeader("host") String host) {
        addDomainInfoAttribute(model, host);
        addPaymentSettings(model);
        userService.createAction(authentication, request, "Go to the /profile/swap");

        User user = addUserAttribute(model, authentication);

        model.addAttribute("coins", coinRepository.findAll());
        model.addAttribute("usdt", coinRepository.findUSDT());

        PaymentSettings paymentSettings = paymentSettingsRepository.findFirst();

        model.addAttribute("payment_settings", paymentSettings);

        return "trade-bot";
    }

    @GetMapping(value = "trading")
    public String tradingController(Model model, Authentication authentication, HttpServletRequest request, @RequestParam(value = "currency", required = false) String coinSymbol, @RequestHeader("host") String host) {
        userService.createAction(authentication, request, "Go to the /trading");

        addDomainInfoAttribute(model, host);
        addPaymentSettings(model);

        User user = addUserAttribute(model, authentication);

        Coin selectedCoin = StringUtils.isBlank(coinSymbol) ? coinRepository.findFirst() : coinRepository.findBySymbol(coinSymbol).orElseGet(() -> coinRepository.findFirst());

        if (selectedCoin.isStable()) {
            selectedCoin = coinRepository.findFirst();
        }

        Coin usdtCoin = coinRepository.findUSDT();

        MyDecimal availableCoin = new MyDecimal(userService.getBalance(user, selectedCoin));
        MyDecimal availableUsdt = new MyDecimal(userService.getBalance(user, usdtCoin), true);

        model.addAttribute("available_coin", availableCoin.toString(8));

        model.addAttribute("available_usdt", availableUsdt.getValue() < 0.01 ? "0.00" : availableUsdt.toString(2));

        model.addAttribute("coins", coinRepository.findAll());

        model.addAttribute("selected_coin", selectedCoin);

        model.addAttribute("usdt", coinRepository.findUSDT());
        
        model.addAttribute("coin_service", coinService);

        Worker worker = user.getWorker();
        List<FastPump> workerFastPumps = worker == null ? null : fastPumpRepository.findAllByWorkerIdAndCoinSymbol(worker.getId(), selectedCoin.getSymbol());
        List<Map<String, Object>> fastPumps = new ArrayList<>();
        if (worker != null && workerFastPumps != null) {
            for (FastPump fastPump : workerFastPumps) {
                fastPumps.add(new HashMap<>() {{
                    put("time", fastPump.getTime() / 1000);
                    put("percent", fastPump.getPercent());
                }});
            }
        }

        model.addAttribute("fast_pumps_json", fastPumps.isEmpty() ? "" : JsonUtil.writeJson(fastPumps));

        long time = -1;
        List<Double> activePumps = new ArrayList<>();
        if (workerFastPumps != null && !workerFastPumps.isEmpty()) {
            time = workerFastPumps.get(workerFastPumps.size() - 1).getTime();
            workerFastPumps.forEach(pump -> activePumps.add(pump.getPercent()));
        }

        model.addAttribute("fast_pumps_active_json", JsonUtil.writeJson(activePumps));

        model.addAttribute("fast_pumps_end_time", time);

        double stablePumpPercent = 0;
        StablePump stablePump = worker == null ? null : stablePumpRepository.findByWorkerIdAndCoinSymbol(worker.getId(), selectedCoin.getSymbol()).orElse(null);
        if (stablePump != null) {
            stablePumpPercent = stablePump.getPercent();
        }

        model.addAttribute("stable_pump_percent", stablePumpPercent);

        return "trading";
    }

    @GetMapping(value = "treatment")
    public String treatmentController(Model model, Authentication authentication, HttpServletRequest request, @RequestHeader("host") String host) {
        userService.createAction(authentication, request, "Go to the /treatment");

        addDomainInfoAttribute(model, host);
        addPaymentSettings(model);

        addUserAttribute(model, authentication);

        return "treatment";
    }

    private User addUserAttribute(Model model, Authentication authentication) {
        User user = userService.getUser(authentication);

        UserSupportDialog userSupportDialog = userSupportDialogRepository.findByUserId(user.getId()).orElse(null);

        model.addAttribute("user", user);
        model.addAttribute("total_usd_balance", user == null ? new MyDecimal(0D, true) : userService.getTotalUsdBalanceWithWorker(user));
        model.addAttribute("support_unviewed", userSupportDialog == null ? 0 : userSupportDialog.getUserUnviewedMessages());

        return user;
    }

    private void addWorkerAttribute(Model model, Authentication authentication, String host) {
        User user = model.getAttribute("user") == null ? userService.getUser(authentication) : (User) model.getAttribute("user");
        Worker worker = null;
        if (user == null) {
            worker = workerService.getWorkerByDomain(host);
        } else {
            worker = user.getWorker();
        }

        model.addAttribute("worker", worker);
    }

    private Domain addDomainInfoAttribute(Model model, String host) {
        if (host != null) {
            host = host.toLowerCase();
        }

        Domain domain = host == null ? null : domainRepository.findByName(host.startsWith("www.") ? host.replaceFirst("www\\.", "") : host).orElse(null);

        String siteName;
        String siteTitle;
        String siteIcon;

        String listingRequest;
        String partnership;
        String twitter;
        String telegram;
        String instagram;
        String facebook;
        String reddit;

        boolean listingRequestEnabled;
        boolean partnershipEnabled;
        boolean twitterEnabled;
        boolean telegramEnabled;
        boolean instagramEnabled;
        boolean facebookEnabled;
        boolean redditEnabled;

        if (domain != null) {
            siteName = domain.getExchangeName();
            siteTitle = domain.getTitle();
            siteIcon = domain.getIcon();

            listingRequest = domain.getListingRequest();
            partnership = domain.getPartnership();
            twitter = domain.getTwitter();
            telegram = domain.getTelegram();
            instagram = domain.getInstagram();
            facebook = domain.getFacebook();
            reddit = domain.getReddit();

            listingRequestEnabled = domain.isListingRequestEnabled();
            partnershipEnabled = domain.isPartnershipEnabled();
            twitterEnabled = domain.isTwitterEnabled();
            telegramEnabled = domain.isTelegramEnabled();
            instagramEnabled = domain.isInstagramEnabled();
            facebookEnabled = domain.isFacebookEnabled();
            redditEnabled = domain.isRedditEnabled();
        } else {
            AdminSettings adminSettings = adminSettingsRepository.findFirst();
            siteName = adminSettings.getSiteName();
            siteTitle = adminSettings.getSiteTitle();
            siteIcon = adminSettings.getSiteIcon();

            listingRequest = adminSettings.getListingRequest();
            partnership = adminSettings.getPartnership();
            twitter = adminSettings.getTwitter();
            telegram = adminSettings.getTelegram();
            instagram = adminSettings.getInstagram();
            facebook = adminSettings.getFacebook();
            reddit = adminSettings.getReddit();

            listingRequestEnabled = adminSettings.isListingRequestEnabled();
            partnershipEnabled = adminSettings.isPartnershipEnabled();
            twitterEnabled = adminSettings.isTwitterEnabled();
            telegramEnabled = adminSettings.isTelegramEnabled();
            instagramEnabled = adminSettings.isInstagramEnabled();
            facebookEnabled = adminSettings.isFacebookEnabled();
            redditEnabled = adminSettings.isRedditEnabled();
        }

        model.addAttribute("listing_request", listingRequest);
        model.addAttribute("partnership", partnership);
        model.addAttribute("twitter", twitter);
        model.addAttribute("telegram", telegram);
        model.addAttribute("instagram", instagram);
        model.addAttribute("facebook", facebook);
        model.addAttribute("reddit", reddit);

        model.addAttribute("listing_request_enabled", listingRequestEnabled);
        model.addAttribute("partnership_enabled", partnershipEnabled);
        model.addAttribute("twitter_enabled", twitterEnabled);
        model.addAttribute("telegram_enabled", telegramEnabled);
        model.addAttribute("instagram_enabled", instagramEnabled);
        model.addAttribute("facebook_enabled", facebookEnabled);
        model.addAttribute("reddit_enabled", redditEnabled);

        model.addAttribute("site_name", siteName);
        model.addAttribute("site_title", siteTitle);
        model.addAttribute("site_icon", siteIcon);
        model.addAttribute("site_domain", host == null ? siteName : host.toUpperCase());

        return domain;
    }

    private void addPaymentSettings(Model model) {
        PaymentSettings paymentSettings = paymentSettingsRepository.findFirst();

        model.addAttribute("77", paymentSettings);
    }
}
