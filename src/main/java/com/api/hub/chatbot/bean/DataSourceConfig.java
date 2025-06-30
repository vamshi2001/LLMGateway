package com.api.hub.chatbot.bean;
import java.util.Properties;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

    @Value("${db.url}")
    private String dbUrl;

    @Value("${db.username}")
    private String dbUsername;

    @Value("${db.password}")
    private String dbPassword;

    @Value("${db.initial-size}")
    private int initialSize;

    @Value("${db.max-active}")
    private int maxActive;

    @Value("${db.min-idle}")
    private int minIdle;

    @Value("${db.max-idle}")
    private int maxIdle;

    @Value("${db.max-wait}")
    private int maxWait;

    @Value("${db.remove-abandoned}")
    private boolean removeAbandoned;

    @Value("${db.remove-abandoned-timeout}")
    private int removeAbandonedTimeout;

    @Value("${db.test-on-borrow}")
    private boolean testOnBorrow;

    @Value("${db.validation-query}")
    private String validationQuery;

    @Bean
    public DataSource dataSource() {
        PoolProperties p = new PoolProperties();
        p.setUrl(dbUrl);
        p.setDriverClassName("com.mysql.cj.jdbc.Driver");
        p.setUsername(dbUsername);
        p.setPassword(dbPassword);

        // Pool Configuration
        p.setInitialSize(initialSize);
        p.setMaxActive(maxActive);
        p.setMinIdle(minIdle);
        p.setMaxIdle(maxIdle);
        p.setMaxWait(maxWait);
        p.setRemoveAbandoned(removeAbandoned);
        p.setRemoveAbandonedTimeout(removeAbandonedTimeout);
        p.setTestOnBorrow(testOnBorrow);
        p.setValidationQuery(validationQuery);

        DataSource dataSource = new DataSource();
        dataSource.setPoolProperties(p);
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.api.hub.chatbot.entity");  // Scan your entity classes
        em.setPersistenceProviderClass(HibernatePersistenceProvider.class);

        // Hibernate properties
        Properties props = new Properties();
        props.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        props.put("hibernate.show_sql", "true");
        props.put("hibernate.hbm2ddl.auto", "validate");

        em.setJpaProperties(props);
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        return em;
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }
}
