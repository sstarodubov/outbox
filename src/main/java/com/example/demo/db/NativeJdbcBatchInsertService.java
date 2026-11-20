package com.example.demo.db;

import com.example.demo.Msg;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component
public class NativeJdbcBatchInsertService {

    private static final String INSERT_SQL = """
            INSERT INTO test_table2 (f2, f3, f4, f5)
            VALUES (?, ?, ?, ?)
            """;

    private final DataSource dataSource;

    public NativeJdbcBatchInsertService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean batchInsert(List<Msg> msgs) {

        try (Connection connection = dataSource.getConnection()) {

            boolean originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement ps =
                             connection.prepareStatement(INSERT_SQL)) {

                    int count = 0;

                    for (Msg msg : msgs) {

                        ps.setObject(1, msg.f2());
                        ps.setObject(2, msg.f3());
                        ps.setObject(3, msg.f4());
                        ps.setObject(4, msg.f5());

                        ps.addBatch();

                        if (++count % Common.BATCH_SIZE == 0) {
                            ps.executeBatch();
                        }
                    }

                    if (count % Common.BATCH_SIZE != 0) {
                        ps.executeBatch();
                    }
                }

                connection.commit();
                return true;
            } catch (SQLException | RuntimeException e) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    e.addSuppressed(rollbackException);
                }

                throw new RuntimeException("Batch insert failed", e);

            } finally {
                try {
                    connection.setAutoCommit(originalAutoCommit);
                } catch (SQLException ignored) {
                    // Connection will be returned to the pool.
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database connection failed", e);
        }
    }
}
