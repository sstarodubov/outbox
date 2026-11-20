package com.example.demo.uuidgen;

import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.uuid.Generators;

import java.util.UUID;

@Component
public class UUIDService {

    public record Value(String id, int f1, String f2) {}

    @Mapper
    interface Repo {

        @Insert("""
                insert into test_table6 (id, f1, f2) values (
                    #{id}::uuid,
                    #{f1},
                    #{f2} 
                ) 
                """)
        void insert(Value v);
    }

    @Autowired
    Repo repo;

    private static final TimeBasedEpochGenerator GENERATOR =
            Generators.timeBasedEpochGenerator();

    public static UUID generate() {
        return GENERATOR.generate();
    }

    public boolean insert(Value v) {
        repo.insert(v);
        return true;
    }
}
