package com.prvz.url_checker.config;

import com.mongodb.MongoClient;
import com.prvz.url_checker.converter.DateToOffsetDateTimeConverter;
import com.prvz.url_checker.converter.OffsetDateTimeToDateConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Arrays;
import java.util.Collection;

@Configuration
@EnableMongoRepositories(basePackages = "com.prvz.url_checker.repository")
@ComponentScan
public class MongoConfig extends AbstractMongoConfiguration {

    @Value("${spring.data.mongodb.host}")
    private String mongoHost;

    @Value("${spring.data.mongodb.port}")
    private int mongoPort;

    @Value("${spring.data.mongodb.database}")
    private String mongoDatabase;

    @Override
    public MongoClient mongoClient() {
        return new MongoClient(mongoHost, mongoPort);
    }

    @Override
    protected String getDatabaseName() {
        return mongoDatabase;
    }

    @Override
    protected Collection<String> getMappingBasePackages() {
        return super.getMappingBasePackages();
    }

    @Override
    public CustomConversions customConversions() {
        return new CustomConversions(CustomConversions.StoreConversions.NONE,
                Arrays.asList(new OffsetDateTimeToDateConverter(), new DateToOffsetDateTimeConverter()));
    }
}
