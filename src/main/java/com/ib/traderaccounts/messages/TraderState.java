package com.ib.traderaccounts.messages;

import java.math.BigDecimal;

/**
 * Class to hold the data required for a Trader State message
 */
public class TraderState {
    private String accountId;
    private String companyId;
    private BigDecimal balance;

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

    public String toString() {
        return new String("TraderState :").concat(" AccountId :").concat(getAccountId())
                .concat(" CompanyId :").concat(getCompanyId()==null ? "null" : getCompanyId()).concat(" Balance :")
                .concat(getBalance().toString());
    }

    @Override
    public boolean equals(Object other) {
        if ( other!= null && (other instanceof TraderState) && accountId.equals(((TraderState)other).accountId)
                && (companyId.equals(((TraderState)other).companyId))
                && balance.compareTo(((TraderState)other).balance) == 0){
            return true;
        } else {
            return false;
        }
    }
}
