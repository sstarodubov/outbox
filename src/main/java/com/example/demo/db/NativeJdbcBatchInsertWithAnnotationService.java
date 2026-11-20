package com.example.demo.db;

import com.example.demo.Msg;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;


@Component
public class NativeJdbcBatchInsertWithAnnotationService {

    private static final String INSERT_SQL = """
        INSERT INTO test_table2 (f2, f3, f4, f5)
        VALUES (?, ?, ?, ?)
        """;

    private final DataSource dataSource;

    public NativeJdbcBatchInsertWithAnnotationService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Transactional
    public boolean batchInsert(List<Msg> msgs) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = connection.prepareStatement(INSERT_SQL)) {
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

            return true;

        } catch (SQLException e) {
            throw new RuntimeException("Batch insert failed", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }
}
