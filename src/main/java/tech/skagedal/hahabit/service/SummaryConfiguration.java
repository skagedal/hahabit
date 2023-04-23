package tech.skagedal.hahabit.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("hahabit.summary")
public record SummaryConfiguration(int days) {
}
