package com.sbank.admin.common.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class MainDBConfig {
	@Primary
	@Bean(name="mainConfig")
	@ConfigurationProperties(prefix="maindb")
	public HikariConfig config() {
		return new HikariConfig();
	}

	@Primary
	@Bean(name="mainDBDataSource")
	public DataSource dataSource() {
		return new HikariDataSource(config());
	}

	@Primary
	@Bean(name="mainTransactionManager")
	public PlatformTransactionManager transactionManager(@Qualifier("mainDBDataSource") DataSource objDataSource) {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(objDataSource);
		return transactionManager;
	}
}