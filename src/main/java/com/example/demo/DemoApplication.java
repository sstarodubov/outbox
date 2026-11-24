package com.example.demo;

import com.example.demo.db.*;
import com.example.demo.uuidgen.UUIDService;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.util.List;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Component
	public static class Runner implements ApplicationRunner {

		@Autowired
		MyBatisForeach myBatisForeach;

		@Autowired
		SqlBatchTemplateService sqlBatchTemplateService;

		@Autowired
		List<SqlSessionTemplate> sqlSessionTemplates;

		@Autowired
		NativeJdbcBatchInsertWithAnnotationService nativeJdbcBatchInsertService;

		@Autowired
		NativeJdbcBatchInsertService jdbc;

		@Autowired
		UUIDService uuidService;

		@Autowired
		OpenSessionSqlBatchTemplateService openSessionSqlBatchTemplateService;

		@Autowired
		HibernateBatchInsert hibernateBatchInsert;

		@Override
		public void run(ApplicationArguments args) throws Exception {
		}
	}

}
