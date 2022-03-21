package com.hospital.hospitalapp.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "EhrEntityManagerFactory",
basePackages = {"com.hospital.hospitalapp.ehr.repository"},
transactionManagerRef = "transactionManager")
public class EhrDbConfig {

    @Primary
    @Bean(name = "EhrDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "EhrEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean
    barEntityManagerFactory(
            EntityManagerFactoryBuilder entityManagerFactoryBuilder,
            @Qualifier("EhrDataSource") DataSource dataSource
    ) {
        return
                entityManagerFactoryBuilder
                        .dataSource(dataSource)
                        .packages("com.hospital.hospitalapp.ehr.entity")
                        .persistenceUnit("com.hospital.hospitalapp.ehr")
                        .build();
    }

    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager barTransactionManager(
            @Qualifier("EhrEntityManagerFactory") EntityManagerFactory
                    barEntityManagerFactory
    ) {
        return new JpaTransactionManager(barEntityManagerFactory);
    }
}
