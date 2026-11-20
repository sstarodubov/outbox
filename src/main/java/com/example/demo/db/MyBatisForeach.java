package com.example.demo.db;

import com.example.demo.Msg;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class MyBatisForeach {

    @Mapper
    interface Repo {
        @Insert("""
                    <script>
                        INSERT INTO test_table2 (f2, f3, f4, f5)
                        VALUES
                         <foreach collection="list" item="msg" separator=",">
                                    (
                                    #{msg.f2},
                                    #{msg.f3},
                                    #{msg.f4},
                                    #{msg.f5}
                                    )
                        </foreach>
                    </script>
                """)
        public void insert(List<Msg> list);
    }

    @Autowired
    Repo repo;

    @Transactional
    public boolean batchInsert(List<Msg> list) {

        for (int i = 0; i < list.size(); i += Common.BATCH_SIZE) {
            List<Msg> batch = list.subList(
                    i,
                    Math.min(i + Common.BATCH_SIZE, list.size())
            );

            repo.insert(batch);
        }

        return true;
    }
}
