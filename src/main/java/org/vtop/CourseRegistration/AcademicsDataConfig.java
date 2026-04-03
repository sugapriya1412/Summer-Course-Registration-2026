package org.vtop.CourseRegistration;

import java.sql.SQLException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaDialect;
 
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef="entityManagerFactory",
		transactionManagerRef="transactionManager")
public class AcademicsDataConfig
{	
    @Primary
	@Bean
	PlatformTransactionManager transactionManager() throws SQLException
	{
		JpaTransactionManager txManager = new JpaTransactionManager();
        JpaDialect jpaDialect = new HibernateJpaDialect();
        
        txManager.setEntityManagerFactory(entityManagerFactory().getObject());
        txManager.setJpaDialect(jpaDialect);
        
		return txManager;
	}
	
	@Bean
	LocalContainerEntityManagerFactoryBean entityManagerFactory() throws SQLException
	{
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();		
		vendorAdapter.setShowSql(false);
		vendorAdapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQLDialect");
		
		LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
		factoryBean.setDataSource(academicsdatasource());
		factoryBean.setJpaVendorAdapter(vendorAdapter);
		factoryBean.setPackagesToScan("org.vtop");
		factoryBean.setPersistenceUnitName("academics");
		return factoryBean;
	}
		
	@Bean
    public HikariDataSource academicsdatasource() throws SQLException
	{
		HikariConfig dataSrcConfig = new HikariConfig();
		 dataSrcConfig.setUsername("reguser");
	     dataSrcConfig.setPassword("regus#user1511");
	      dataSrcConfig.setJdbcUrl("jdbc:postgresql://172.16.0.172:5432/vtop");
        dataSrcConfig.setDriverClassName("org.postgresql.Driver");
        dataSrcConfig.setMinimumIdle(1);
        dataSrcConfig.setMaximumPoolSize(1);
        dataSrcConfig.setConnectionTestQuery("SELECT 1");
        dataSrcConfig.setConnectionTimeout(30000);
        dataSrcConfig.setIdleTimeout(300000);
        HikariDataSource dataSource = new HikariDataSource(dataSrcConfig);
        return dataSource;
	}
	

}