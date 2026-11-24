package com.example.demo.db;
import com.example.demo.Msg;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class HibernateBatchInsert {

    @Autowired
    SpringDataRepository springDataRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public boolean batchInsert(List<HibMsg> list) {

        for (int i = 0; i < list.size(); i += Common.BATCH_SIZE) {

            List<HibMsg> batch = list.subList(
                    i,
                    Math.min(i + Common.BATCH_SIZE, list.size())
            );

            for (var msg : batch) {
                entityManager.persist(msg);
            }

            entityManager.flush();

            entityManager.clear();
        }

        return true;
    }


    @Transactional
    public boolean batchJpaInsert(List<HibMsg> list) {
        springDataRepository.saveAllAndFlush(list);
        return true;
    }
}
