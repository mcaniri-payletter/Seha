package com.sbank.admin.common.database;

import com.sbank.admin.common.exception.GlobalException;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**---------------------------------------------------------------------
 * ■AssistDBMybatisConfig ■payletter ■2018. 9. 28.
----------------------------------------------------------------------**/
@Configuration
public class AssistDBMybatisConfig extends AbstractMybatisConfig {
    /**---------------------------------------------------------------------
     * ■dbSqlSessionFactory ■payletter ■2017. 4. 18.
     ----------------------------------------------------------------------**/
    @Override
    @Bean(name="assistDBSqlSessionFactory")
    public SqlSessionFactory dbSqlSessionFactory(@Qualifier("assistDBDataSource") DataSource objDataSource) throws Exception {
        SqlSessionFactoryBean objSqlSessionFactoryBean = new SqlSessionFactoryBean();

        configureSqlSessionFactory(objSqlSessionFactoryBean, objDataSource);
        return objSqlSessionFactoryBean.getObject();
    }

    /**---------------------------------------------------------------------
     * ■dbSqlSession ■payletter ■2017. 4. 18.
     ----------------------------------------------------------------------**/
    @Override
    @Bean(name="assistDBSqlSession")
    public SqlSession dbSqlSession(@Qualifier("assistDBSqlSessionFactory") SqlSessionFactory objSqlSessionFactory) throws GlobalException {
        return new SqlSessionTemplate(objSqlSessionFactory);
    }

    /**---------------------------------------------------------------------
     * ■Batch용 SqlSession ■payletter ■2018. 03. 07.
     ----------------------------------------------------------------------**/
    @Primary
    @Bean(name="assistDBSqlSessionBatch")
    public SqlSession dbSqlSessionBatch(@Qualifier("assistDBSqlSessionFactory") SqlSessionFactory objSqlSessionFactory) throws GlobalException {
        return new SqlSessionTemplate(objSqlSessionFactory, ExecutorType.BATCH);
    }
}
