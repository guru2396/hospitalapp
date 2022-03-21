package com.hospital.hospitalapp.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "centralEntityManagerFactory",
basePackages = {"com.hospital.hospitalapp.central.repository"},
transactionManagerRef = "centralTransactionManager")
public class CentralDbConfig {

    @Bean(name = "centralDataSource")
    @ConfigurationProperties(prefix = "central.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "centralEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean
    barEntityManagerFactory(
            EntityManagerFactoryBuilder entityManagerFactoryBuilder,
            @Qualifier("centralDataSource") DataSource dataSource
    ) {
        return
                entityManagerFactoryBuilder
                        .dataSource(dataSource)
                        .packages("com.hospital.hospitalapp.central.entity")
                        .persistenceUnit("com.hospital.hospitalapp.central")
                        .build();
    }
    @Bean(name = "centralTransactionManager")
    public PlatformTransactionManager barTransactionManager(
            @Qualifier("centralEntityManagerFactory") EntityManagerFactory
                    barEntityManagerFactory
    ) {
        return new JpaTransactionManager(barEntityManagerFactory);
    }
}
