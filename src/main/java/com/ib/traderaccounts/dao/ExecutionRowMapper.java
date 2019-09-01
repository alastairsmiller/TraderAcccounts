package com.ib.traderaccounts.dao;

import com.ib.traderaccounts.model.Execution;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Strictly speaking in this case I could just use a DefaultRowMapper but I wanted to put this in here
 * because I was changing the DB types and mapping them to different Java types
 */
public class ExecutionRowMapper implements RowMapper<Execution> {

    @Override
    public Execution mapRow(ResultSet row, int rowNum) throws SQLException {
        return( new Execution(row.getString("accountid"), row.getString("symbol"),
                row.getBigDecimal("quantity"), row.getBigDecimal("price")));
    }
}
