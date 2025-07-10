package com.api.hub.gateway.spring.beans;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
/**
 * Configuration class for SQL Database connectivity and integration within the Ai-Agent framework.
 * <p>
 * This class conditionally sets up the SQL `DataSource`, `EntityManagerFactory`, `JdbcTemplate`,
 * `NamedParameterJdbcTemplate`, `JpaTransactionManager`, and exception translation bean
 * based on the flags defined in the `application.properties`.
 * </p>
 *
 * <p>
 * Required properties to be set in `application.properties`:
 * </p>
 * <pre>
 * # Enable/Disable SQL DB configuration
 * sql.db.enable=true
 *
 * # Optional individual component toggles
 * sql.db.entitymanager.enable=true
 * sql.db.jdbctemplet.enable=true
 * sql.db.namedjdbctemplet.enable=true
 * sql.db.transactionmanager.enable=true
 * sql.db.exceptiontranslation.enable=true
 *
 * # Hibernate properties
 * sql.db.hibernate.dialect=org.hibernate.dialect.MySQLDialect
 * sql.db.hibernate.show_sql=true
 * sql.db.hibernate.hbm2ddl.auto=validate
 * sql.db.driverclass=com.mysql.cj.jdbc.Driver
 *
 * # DB Connection properties
 * db.url=jdbc:mysql://localhost:3306/yourdb
 * db.username=yourusername
 * db.password=yourpassword
 * db.initial-size=5
 * db.max-active=20
 * db.min-idle=5
 * db.max-idle=10
 * db.max-wait=30000
 * db.remove-abandoned=true
 * db.remove-abandoned-timeout=6000
 * db.test-on-borrow=true
 * db.validation-query=SELECT 1
 * </pre>
 *
 * <p>
 * This configuration is activated only if `sql.db.enable=true`.
 * The other components are conditionally loaded based on their respective flags.
 * </p>
 *
Ai-Agent
 */
@Configuration
@EnableTransactionManagement
@ConditionalOnProperty(name = "sql.db.enable", havingValue = "true")
public class DataSourceConfig {

    /**
     * Injected environment to resolve additional property values.
     */
    @Autowired
    private Environment env;

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

    /**
     * Creates and configures the primary SQL `DataSource` using Tomcat JDBC Pool.
     *
     * @return the configured {@link DataSource}
     */
    @Bean
    public DataSource dataSource() {
        PoolProperties p = new PoolProperties();
        p.setUrl(dbUrl);
        p.setDriverClassName(env.getProperty("sql.db.driverclass"));
        p.setUsername(dbUsername);
        p.setPassword(dbPassword);

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

    /**
     * Sets up the JPA {@link EntityManagerFactory} using Hibernate.
     * Activated when `sql.db.entitymanager.enable=true`.
     *
     * @param dataSource the configured {@link DataSource}
     * @return {@link LocalContainerEntityManagerFactoryBean} for JPA
     
    @Bean
    @ConditionalOnProperty(name = "sql.db.entitymanager.enable", havingValue = "true")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan(env.getProperty("sql.db.entity.packages"));
        em.setPersistenceProviderClass(HibernatePersistenceProvider.class);

        Properties props = new Properties();
        props.put("hibernate.dialect", env.getProperty("sql.db.hibernate.dialect"));
        props.put("hibernate.show_sql", env.getProperty("sql.db.hibernate.show_sql"));
        props.put("hibernate.hbm2ddl.auto", env.getProperty("sql.db.hibernate.hbm2ddl.auto"));

        em.setJpaProperties(props);
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        return em;
    }
*/
    /**
     * Provides {@link JdbcTemplate} for basic SQL operations.
     * Activated when `sql.db.jdbctemplet.enable=true`.
     *
     * @param dataSource the configured {@link DataSource}
     * @return {@link JdbcTemplate} instance
     */
    @Bean
    @ConditionalOnProperty(name = "sql.db.jdbctemplet.enable", havingValue = "true")
    public JdbcTemplate getJDBCTemplet(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * Provides {@link NamedParameterJdbcTemplate} for SQL operations with named parameters.
     * Activated when `sql.db.namedjdbctemplet.enable=true`.
     *
     * @param dataSource the configured {@link DataSource}
     * @return {@link NamedParameterJdbcTemplate} instance
     */
    @Bean
    @ConditionalOnProperty(name = "sql.db.namedjdbctemplet.enable", havingValue = "true")
    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * Configures and enables JPA transaction management.
     * Activated when `sql.db.transactionmanager.enable=true`.
     *
     * @param entityManagerFactory the JPA entity manager factory
     * @return configured {@link JpaTransactionManager}
     
    @Bean
    @ConditionalOnProperty(name = "sql.db.transactionmanager.enable", havingValue = "true")
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
*/
    /**
     * Enables exception translation for Spring-managed repositories.
     * Activated when `sql.db.exceptiontranslation.enable=true`.
     *
     * @return {@link PersistenceExceptionTranslationPostProcessor}
     
    @Bean
    @ConditionalOnProperty(name = "sql.db.exceptiontranslation.enable", havingValue = "true")
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }
    */
    
    @Value("${mongoDB.url}")
	private String mongoDBUrl;
	@Value("${mongoDB.DBName}")
	private String mongoDbName;
	@Value("${mongoDB.maxConnections}")
	private Integer mongoMaxConnnections;
	@Value("${mongoDB.minConnections}")
	private Integer mongoMinConnections;
	@Value("${mongoDB.maxWaitTime}")
	private Integer mongoMaxWaitTime;
	@Value("${mongoDB.connectionTimeOut}")
	private Integer mongoConnectionTimeOut;
	@Value("${mongoDB.readTimeOut}")
	private Integer mongoReadTimeOut;
	@Bean
	public MongoDatabase getMongoConnection() {
		MongoClientSettings settings = MongoClientSettings.builder()
	            .applyConnectionString(new com.mongodb.ConnectionString(mongoDBUrl))
	            .applyToConnectionPoolSettings(builder -> builder
	                .maxSize(mongoMaxConnnections.intValue())             // Maximum connections in the pool
	                .minSize(mongoMinConnections.intValue())             // Minimum idle connections
	                .maxWaitTime(mongoMaxWaitTime.intValue(), java.util.concurrent.TimeUnit.MILLISECONDS) // Wait time for an available connection
	            )
	            .applyToSocketSettings(builder -> builder
	                .connectTimeout(mongoConnectionTimeOut, java.util.concurrent.TimeUnit.SECONDS) // Connection timeout
	                .readTimeout(mongoReadTimeOut, java.util.concurrent.TimeUnit.SECONDS)    // Socket read timeout
	            )
	            .serverApi(ServerApi.builder().version(ServerApiVersion.V1).build()) // Optional for MongoDB Atlas
	            .build();

		 MongoClient mongoClient = MongoClients.create(settings);
		 return mongoClient.getDatabase(mongoDbName);
		  
	}
}