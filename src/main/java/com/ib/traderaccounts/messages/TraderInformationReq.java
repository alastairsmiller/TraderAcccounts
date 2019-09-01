package com.ib.traderaccounts.messages;

/**
 * Class to hold the data required for a Trader Information Request message
 */
public class TraderInformationReq {
    private String accountId;

    public String getAccountId() { return accountId; }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String toString() {
        return new String("TraderInformationReq :").concat(getAccountId());
    }

    @Override
    public boolean equals(Object other) {
        return( other!= null && (other instanceof TraderInformationReq) && accountId.equals(((TraderInformationReq)other).accountId));
    }
}
