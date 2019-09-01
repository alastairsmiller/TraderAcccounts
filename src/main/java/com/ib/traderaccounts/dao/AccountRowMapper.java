package com.ib.traderaccounts.dao;

import com.ib.traderaccounts.model.Account;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Strictly speaking in this case I could just use a DefaultRowMapper but I wanted to put this in here
 * because I was changing the DB types and mapping them to different Java types
 */
public class AccountRowMapper implements RowMapper<Account> {
    @Override
    public Account mapRow(ResultSet row, int rowNum) throws SQLException {
        Account account = new Account(row.getString("accountid"),row.getBigDecimal("balance"));
        account.setCompanyId(row.getString("companyid"));
        return ( account );
    }
}
