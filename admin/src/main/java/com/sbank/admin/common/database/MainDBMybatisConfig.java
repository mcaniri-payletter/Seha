package com.sbank.admin.common.database;

import com.sbank.admin.common.exception.GlobalException;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**---------------------------------------------------------------------
 * ■MainDBMybatisConfig ■payletter ■2018. 9. 28.
 ----------------------------------------------------------------------**/
@Configuration
public class MainDBMybatisConfig extends AbstractMybatisConfig {
    private static final Logger logger = LoggerFactory.getLogger(MainDBMybatisConfig.class);

    /**---------------------------------------------------------------------
     * ■dbSqlSessionFactory ■payletter ■2017. 4. 18.
     ----------------------------------------------------------------------**/
    @Override
    @Primary
    @Bean(name="mainDBSqlSessionFactory")
    public SqlSessionFactory dbSqlSessionFactory(@Qualifier("mainDBDataSource") DataSource objDataSource) throws Exception {
        SqlSessionFactoryBean objSqlSessionFactoryBean = new SqlSessionFactoryBean();

        configureSqlSessionFactory(objSqlSessionFactoryBean, objDataSource);

        if (logger.isDebugEnabled()) {
            logger.debug(objSqlSessionFactoryBean.toString());
        }

        return objSqlSessionFactoryBean.getObject();
    }

    /**---------------------------------------------------------------------
     * ■dbSqlSession ■payletter ■2017. 4. 18.
     ----------------------------------------------------------------------**/
    @Primary
    @Bean(name="mainDBSqlSession")
    public SqlSession dbSqlSession(@Qualifier("mainDBSqlSessionFactory") SqlSessionFactory objSqlSessionFactory) throws GlobalException {
        return new SqlSessionTemplate(objSqlSessionFactory);
    }

    /**---------------------------------------------------------------------
     * ■Batch용 SqlSession ■payletter ■2018. 03. 07.
     ----------------------------------------------------------------------**/
    @Primary
    @Bean(name="mainDBSqlSessionBatch")
    public SqlSession dbSqlSessionBatch(@Qualifier("mainDBSqlSessionFactory") SqlSessionFactory objSqlSessionFactory) throws GlobalException {
        return new SqlSessionTemplate(objSqlSessionFactory, ExecutorType.BATCH);
    }
}
