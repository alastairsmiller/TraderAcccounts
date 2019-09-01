package com.ib.traderaccounts.messages;

/**
 * Class to hold the data required for a Trader Information Response message
 */
public class TraderInformationResp {
    private String accountId;
    private String companyId;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getCompanyId() { return companyId; }

    public void setCompanyId(String companyId) { this.companyId = companyId; }

    public String toString() {
        return new String("TraderInformationResp :").concat(getAccountId())
                .concat(" CompanyId :").concat(getCompanyId());
    }
}
