package com.example.demo.db;

import com.example.demo.Msg;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class SqlBatchTemplateService {

    @Mapper
    interface Repo {
        @Insert("""
                    <script>
                        INSERT INTO test_table2 (f2, f3, f4, f5)
                        VALUES
                                    (
                                    #{f2},
                                    #{f3},
                                    #{f4},
                                    #{f5}
                                    )
                    </script>
                """)
        public void insert(Msg msg);
    }

    private final SqlSessionTemplate batchSqlSessionTemplate;

    @Autowired
    Repo repo;

    private final Repo repoBatch;

    public SqlBatchTemplateService(
            @Qualifier("batchSqlSessionTemplate")
            SqlSessionTemplate batchSqlSessionTemplate, Repo repo) {
        this.batchSqlSessionTemplate = batchSqlSessionTemplate;
        this.repoBatch = batchSqlSessionTemplate.getMapper(Repo.class);
    }

    @Transactional
    public boolean batchInsert(List<Msg> msgs) {

        for (int i = 0; i < msgs.size(); i++) {
            repoBatch.insert(msgs.get(i));

            if ((i + 1) % Common.BATCH_SIZE == 0) {
                batchSqlSessionTemplate.flushStatements();
            }
        }
        batchSqlSessionTemplate.flushStatements();

        return true;
    }
}
