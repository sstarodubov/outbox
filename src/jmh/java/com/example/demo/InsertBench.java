package com.example.demo;


import com.example.demo.db.*;
import com.example.demo.uuidgen.UUIDService;
import org.openjdk.jmh.annotations.*;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2)
@Measurement(iterations = 1)
@Fork(1)
public class InsertBench {

    private ConfigurableApplicationContext context;
    private MyBatisForeach myBatisForeach;
    private SqlBatchTemplateService sqlBatchTemplateService;
    private NativeJdbcBatchInsertService jdbc;
    private NativeJdbcBatchInsertWithAnnotationService jdbcWithAnnotation;
    private UUIDService uuidService;
    private OpenSessionSqlBatchTemplateService openSessionSqlBatchTemplateService;
    private HibernateBatchInsert hibernate;

    private List<Msg> msgs = new ArrayList<>(Common.ALL);
    private List<HibMsg> hibMsgs = new ArrayList<>(Common.ALL);
    UUIDService.Value v4 = new UUIDService.Value(UUID.randomUUID().toString(), 1, "hello");
    UUIDService.Value v7 = new UUIDService.Value(UUIDService.generate().toString(), 1, "hello");

    @Setup(Level.Trial)
    public void setup() {
        context = new SpringApplication(DemoApplication.class)
                .run();
        msgs = Msg.generateRnd(Common.ALL);
        myBatisForeach = context.getBean(MyBatisForeach.class);
        sqlBatchTemplateService = context.getBean(SqlBatchTemplateService.class);
        jdbc = context.getBean(NativeJdbcBatchInsertService.class);
        jdbcWithAnnotation = context.getBean(NativeJdbcBatchInsertWithAnnotationService.class);
        uuidService = context.getBean(UUIDService.class);
        openSessionSqlBatchTemplateService = context.getBean(OpenSessionSqlBatchTemplateService.class);
        hibernate = context.getBean(HibernateBatchInsert.class);
    }

    @Setup(Level.Invocation)
    public void set() {
        hibMsgs = HibMsg.generateRnd(Common.ALL);
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        context.close();
    }


    //@Benchmark
    public boolean uuidV4() {
        return uuidService.insert(new UUIDService.Value(UUID.randomUUID().toString(), 1, "hello"));
    }

    //@Benchmark
    public boolean uuidV7() {
        return uuidService.insert(new UUIDService.Value(UUIDService.generate().toString(), 1, "hello"));
    }

    @Benchmark
    public boolean foreachBatchInsertMyBatis() {
        return myBatisForeach.batchInsert(msgs);
    }

    @Benchmark
    public boolean sqlBatchTemplateMyBatis() {
        return sqlBatchTemplateService.batchInsert(msgs);
    }

    @Benchmark
    public boolean rawJdbcBatch() {
        return jdbc.batchInsert(msgs);
    }

    @Benchmark
    public boolean rawJdbcWithSpringTransactional() {
        return jdbcWithAnnotation.batchInsert(msgs);
    }

    @Benchmark
    public boolean openSessionMybatisBatch() {
        return openSessionSqlBatchTemplateService.batchInsert(msgs);
    }

    @Benchmark
    public boolean hibernateEntityManager() {
        return hibernate.batchInsert(hibMsgs);
    }

    @Benchmark
    public boolean springDataInsert() {
        return hibernate.batchJpaInsert(hibMsgs);
    }
}