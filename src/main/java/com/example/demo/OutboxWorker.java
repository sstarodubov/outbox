package com.example.demo;

import org.apache.ibatis.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class OutboxWorker {

    @Mapper
    public interface OutboxMapper {
        @Select("""
                update outbox_offsets
                set available_after = #{nowPlusLockTimeout}
                from (
                    select partition 
                    from outbox_offsets
                    where available_after < now() 
                    order by available_after
                    limit 1
                    for update skip locked
                ) locked_offsets
                
                where 
                outbox_offsets.partition = locked_offsets.partition
                returning outbox_offsets.*
                """)
        @ConstructorArgs({
                @Arg(column = "partition", javaType = int.class),
                @Arg(column = "last_processed_transaction_id", javaType = long.class),
                @Arg(column = "last_processed_id", javaType = long.class)
        })
        Offset findPartition(LocalDateTime nowPlusLockTimeout);

        @Select("""
                select * 
                from outbox_messages
                where partition = #{partition} and  
                 transaction_id >= #{lastProcessedTransactionId}::text::xid8 and
                 (
                     transaction_id > #{lastProcessedTransactionId}::text::xid8 or 
                     transaction_id = #{lastProcessedTransactionId}::text::xid8 and id > #{lastProcessedId}
                 ) and 
                 transaction_id < pg_snapshot_xmin(pg_current_snapshot())
                 order by transaction_id, id 
                 limit 100 
                """)
        @ConstructorArgs({
                @Arg(column = "topic", javaType = String.class),
                @Arg(column = "payload", javaType = byte[].class),
                @Arg(column = "id", javaType = long.class),
                @Arg(column = "transaction_id", javaType = long.class),
        })
        List<Message> findMessages(Offset offset);

        @Update("""
                update outbox_offsets
                set 
                 available_after = now(),
                 last_processed_transaction_id = #{lastMsgTransactionId}::text::xid8,
                 last_processed_id = #{lastMsgId}
                 where partition = #{partition} 
                """)
        void updatePartitionHappy(long lastMsgTransactionId, long lastMsgId, int partition);

        @Update("""
                 update outbox_offsets
                 set 
                 available_after = #{availableAfter} 
                 where partition = #{partition} 
                """)
        void updatePartitionSad(int partition, LocalDateTime availableAfter);
    }

    public record Message(String topic, byte[] payload, long id, long transactionId) {
    }

    public record Offset(int partition, long lastProcessedTransactionId, long lastProcessedId) {
    }

    @Autowired
    OutboxMapper outboxMapper;

    @Autowired
    KafkaTemplate<String, byte[]> outboxKafkaTemplate;

    final long lockTimeout = 5;
    final int loopTimeout = 5;

    final AtomicBoolean isRunning = new AtomicBoolean(true);

    public void start() {
        Thread.ofPlatform()
                .name("outbox-thread")
                .daemon(false)
                .start(this::loop);

        Runtime.getRuntime().addShutdownHook(Thread.ofVirtual().unstarted(() -> {
                    System.out.println("graceful shoutdown");
                    isRunning.set(false);
                })
        );
    }

    void loop() {
        System.out.println("start loop");
        while (isRunning.get()) {
            final Offset offset = getAvailablePartitionAndLock(LocalDateTime.now().plusMinutes(lockTimeout));
            if (offset == null) {
                sleep5Sec();
                continue;
            }

            final List<Message> messages = getMessagesFromPartition(offset);
            if (!messages.isEmpty() && sendBatchToKafka(messages)) {
                updatePartition(messages.getLast(), offset.partition());
            } else {
                updatePartition(offset.partition());
            }
        }
    }

    void updatePartition(final Message msg, final int partition) {
        outboxMapper.updatePartitionHappy(msg.transactionId(), msg.id(), partition);
    }

    void updatePartition(final int partition) {
        outboxMapper.updatePartitionSad(partition, LocalDateTime.now().plusSeconds(loopTimeout));
    }

    boolean sendBatchToKafka(final List<Message> messages) {
        try {
            return outboxKafkaTemplate.executeInTransaction(op -> {
                for (final var msg : messages) {
                    op.send(msg.topic(), msg.payload());
                }
                return true;
            });
        } catch (Exception e) {
            System.out.println("cannot execute kafka tx");
            e.printStackTrace();
            return false;
        }

    }

    List<Message> getMessagesFromPartition(final Offset offset) {
        return outboxMapper.findMessages(offset);
    }

    Offset getAvailablePartitionAndLock(final LocalDateTime lockTimeout) {
        return outboxMapper.findPartition(lockTimeout);
    }

    void sleep5Sec() {
        try {
            Thread.sleep(loopTimeout * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
