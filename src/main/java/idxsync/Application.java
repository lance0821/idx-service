package idxsync;

import com.fasterxml.jackson.databind.SerializationFeature;
import idxsync.idx.strategy.PhotoStrategy;
import idxsync.idx.strategy.cached.PhotoStrategyCached;
import idxsync.idx.strategy.filesystem.PhotoStrategyFileSystem;
import idxsync.mapping.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    @Value("${json.output.pretty.print}")
    private boolean isJsonPrettyPrint;

    @Value("${json.output.dates.as.timestamps}")
    private boolean isJsonOutputDatesAsTimestamps;

    @Value("${mappings.path}")
    private String mappingsPath;

    @Value("${email.host}")
    private String emailHost;

    @Value("${email.port}")
    private int emailPort;

    @Value("${email.username}")
    private String emailUsername;

    @Value("${email.password}")
    private String emailPassword;

    @Value("${email.senderAddress}")
    private String emailSenderAddress;

    @Value("${photos.filesystem.enabled}")
    private boolean photosFilesystemEnabled;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public PhotoStrategy photoStrategy() {
        if (photosFilesystemEnabled) return new PhotoStrategyFileSystem();
        else return new PhotoStrategyCached();
    }

    @Bean
    public MailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(emailHost);
        mailSender.setPort(emailPort);
        mailSender.setUsername(emailUsername);
        mailSender.setPassword(emailPassword);

        Properties mailProps = new Properties();
        mailProps.setProperty("mail.smtp.auth", "true");
        mailProps.setProperty("mail.smtp.starttls.enable", "true");
        mailSender.setJavaMailProperties(mailProps);

        return mailSender;
    }

    @Bean
    public SimpleMailMessage syncReportEmailTemplate() {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(emailSenderAddress);
        msg.setSubject("Sync Report");

        return msg;
    }

    @Bean
    public ListingCommercialMapper listingCommercialMapper() {
        return new ListingCommercialMapper(String.format("%slisting-commercial.json", getMappingsPath()));
    }

    @Bean
    public ListingLandMapper listingLandMapper() {
        return new ListingLandMapper(String.format("%slisting-land.json", getMappingsPath()));
    }

    @Bean
    public ListingMultMapper listingMultMapper() {
        return new ListingMultMapper(String.format("%slisting-mult.json", getMappingsPath()));
    }

    @Bean
    public ListingResidentialMapper listingResidentialMapper() {
        return new ListingResidentialMapper(String.format("%slisting-residential.json", getMappingsPath()));
    }

    @Bean
    public OpenHouseMapper openHouseMapper() {
        return new OpenHouseMapper(String.format("%sopenhouse.json", getMappingsPath()));
    }

    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        //indent
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.indentOutput(isJsonPrettyPrint);

        if (isJsonOutputDatesAsTimestamps) {
            builder.featuresToEnable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        }
        else {
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        }

        return builder;
    }

    public String getMappingsPath() {
        return String.format("/mappings/%s/", mappingsPath);
    }
}
