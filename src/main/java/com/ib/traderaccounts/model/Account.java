package com.ib.traderaccounts.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "ACCOUNTS")
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "accountid")
    @Id
    private String accountId;

    @Column(name = "companyid")
    private String companyId;

    @Column(name = "balance")
    private BigDecimal balance;

    public Account(String accountid, BigDecimal balance) {
        this.accountId = accountid;
        this.balance = balance;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object other) {
        if ( other!= null && (other instanceof Account) && accountId.equals(((Account)other).accountId)
                && ((companyId == null && ((Account)other).companyId == null ) || (companyId.equals(((Account)other).companyId)))
                && balance.compareTo(((Account)other).balance) == 0) {
            return true;
        } else {
            return false;
        }
    }
}
