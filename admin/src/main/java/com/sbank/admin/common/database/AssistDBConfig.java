package com.sbank.admin.common.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class AssistDBConfig {
	@Bean(name="assistConfig")
	@ConfigurationProperties(prefix="assistdb")
	public HikariConfig config() {
		return new HikariConfig();
	}

	@Bean(name="assistDBDataSource")
	public DataSource dataSource() {
		return new HikariDataSource(config());
	}

	@Bean(name="assistTransactionManager")
	public PlatformTransactionManager transactionManager(@Qualifier("assistDBDataSource") DataSource objDataSource) {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(objDataSource);
		return transactionManager;
	}
}