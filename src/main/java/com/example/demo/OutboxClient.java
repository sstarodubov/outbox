package com.example.demo;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.kafka.clients.producer.Partitioner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OutboxClient {
    interface Partitioner {
        int next();
    }
    public static class RoundRobinPartitioner implements Partitioner {
        final int partitions;
        int curPartition = 0;

        public RoundRobinPartitioner(int p) {
            this.partitions = p;
        }

        @Override
        public int next() {
            final int p = curPartition;
            curPartition = (curPartition + 1) % partitions;
            return p;
        }
    }

    @Mapper
    interface OutboxClientMapper {

        @Insert("""
                insert into outbox_messages (topic, payload, partition) values (
                    #{topic},
                    #{payload},
                    #{partition}
                )            
                """)
        public void save(String topic, byte[] payload, int partition);
    }

    public static final int partitions = 4;
    final Partitioner partitioner = new RoundRobinPartitioner(partitions);

    @Autowired
    OutboxClientMapper outboxClientMapper;

    public record OutboxMsg(String topic, byte[] payload) {
    }

    public void save(final OutboxMsg msg) {
        final int p = partitioner.next();
        outboxClientMapper.save(msg.topic(), msg.payload(), p);
    }
}
