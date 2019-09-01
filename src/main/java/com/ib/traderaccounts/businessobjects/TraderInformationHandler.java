package com.ib.traderaccounts.businessobjects;

import com.ib.traderaccounts.model.Account;
import com.ib.traderaccounts.dao.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class to handle the trader information data when it comes back from the external system
 */
@Component
public class TraderInformationHandler {

    @Autowired
    private AccountRepository accountRepository;

    public TraderInformationHandler(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void handleTraderInformation(String accountId, String companyId) {
        // Get the specific account from the DB just to check its there (should be though)
        Account traderAcount = accountRepository.findByAccountId(accountId);
        if ( traderAcount != null ) {
            accountRepository.updateCompanyId(accountId, companyId);
        }
    }
}
