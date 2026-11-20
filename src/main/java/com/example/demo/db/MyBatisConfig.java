package com.example.demo.db;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MyBatisConfig {

    @Bean
    @Primary
    public SqlSessionTemplate simpleSqlSessionTemplate(
            SqlSessionFactory sqlSessionFactory) {

        return new SqlSessionTemplate(
                sqlSessionFactory,
                ExecutorType.SIMPLE
        );
    }

    @Bean
    public SqlSessionTemplate batchSqlSessionTemplate(
            SqlSessionFactory sqlSessionFactory) {

        return new SqlSessionTemplate(
                sqlSessionFactory,
                ExecutorType.BATCH
        );
    }


}
