package com.ib.traderaccounts.dao;

import com.ib.traderaccounts.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * DAO layer into the DB for the Account table/class
 */
@Component
public class AccountRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public Account findByAccountId(String accountId) {
        String sql = "select * from accounts where accountid=?";
        RowMapper<Account> rowMapper = new AccountRowMapper();
        List<Account> theAccounts = jdbcTemplate.query(sql,  new Object[] { accountId, }, rowMapper);
        if ( theAccounts.size() == 0 ) {
            return( null );
        } else {
            return (theAccounts.get(0));
        }
    }

    public List<Account> findAll() {
        String sql = "select * from accounts";
        RowMapper<Account> rowMapper = new AccountRowMapper();
        List<Account> theAccounts = jdbcTemplate.query(sql, rowMapper);
        return ( theAccounts );
    }

    public int insert(Account theAccount) {
        return jdbcTemplate.update("insert into accounts (accountid, balance) " + "values(?,  ?)",
                new Object[] { theAccount.getAccountId(), theAccount.getBalance() });
    }

    public int updateCompanyId(String accountId, String companyId) {
        return jdbcTemplate.update("update accounts  set companyid = ? where accountid=?",
                new Object[] {companyId, accountId });
    }

    public int updateBalance(String accountId, BigDecimal balance) {
        return jdbcTemplate.update("update accounts  set balance = ? where accountid=?",
                new Object[] {balance, accountId });
    }

    /**
     * Method used for testing only. This should not be called by the real application. It is deprecated for this reason
     */
    @Deprecated
    public int deleteAllAccounts() {
        return jdbcTemplate.update("delete from ACCOUNTS where 1= 1");
    }
}