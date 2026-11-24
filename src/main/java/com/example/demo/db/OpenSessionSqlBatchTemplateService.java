package com.example.demo.db;

import com.example.demo.Msg;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class OpenSessionSqlBatchTemplateService {

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

    private final SqlSessionFactory sqlSessionFactory;

    public OpenSessionSqlBatchTemplateService(
            SqlSessionFactory sqlSessionFactory, Repo repo) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    @Autowired
    Repo repo;

    @Transactional
    public boolean batchInsert(List<Msg> msgs) {
        repo.insert(msgs.get(0));

        try(final var session = sqlSessionFactory.openSession(ExecutorType.BATCH, false)) {
            final var repo = session.getMapper(Repo.class);
            for (int i = 0; i < msgs.size(); i++) {
                repo.insert(msgs.get(i));

                if ((i + 1) % Common.BATCH_SIZE == 0) {
                    session.flushStatements();
                }
            }
            session.flushStatements();
        };

        return true;
    }
}
