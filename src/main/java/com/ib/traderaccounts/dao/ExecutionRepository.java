package com.ib.traderaccounts.dao;

import com.ib.traderaccounts.model.Execution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

/**
 * DAO layer into the DB for the Execution table/class
 */
@Component
public class ExecutionRepository{

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Execution> findByAccountId(String acccountId) {
        String sql = "select * from executions where accountid=?";
        RowMapper<Execution> rowMapper = new ExecutionRowMapper();
        List<Execution> theExecutions = jdbcTemplate.query(sql, rowMapper, acccountId);
        return ( theExecutions );
    }

    public List<Execution> findAll() {
        String sql = "select * from executions";
        RowMapper<Execution> rowMapper = new ExecutionRowMapper();
        List<Execution> theExecutions = jdbcTemplate.query(sql, rowMapper);
        return ( theExecutions );
    }


    public List<Execution> findLastTenBySymbol(String symbol) {
        String sql = "select top(9) * from executions where symbol=? order by currtimestamp";
        RowMapper<Execution> rowMapper = new ExecutionRowMapper();
        List<Execution> theExecutions = jdbcTemplate.query(sql, rowMapper, symbol);
        return ( theExecutions );
    }

    public int insert(Execution theExecution) {
        return jdbcTemplate.update("insert into executions (accountid, symbol, price, quantity, currtimestamp) "
                        + "values(?,  ?, ?, ?, ?)",
                new Object[] { theExecution.getAccountId(), theExecution.getSymbol(), theExecution.getPrice(),
                        theExecution.getQuantity(),new Timestamp(System.currentTimeMillis())});
    }

    /**
     * Method used for testing only. This should not be called by the real application. It is deprecated for this reason
     */
    @Deprecated
    public int deleteAllExecutions() {
        return jdbcTemplate.update("delete from EXECUTIONS where 1= 1");
    }
}