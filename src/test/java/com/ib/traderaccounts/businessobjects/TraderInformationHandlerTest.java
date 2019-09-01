package com.ib.traderaccounts.businessobjects;

import com.ib.traderaccounts.dao.AccountRepository;
import com.ib.traderaccounts.model.Account;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TraderInformationHandlerTest {

    @Test
    public void testTraderInformationHandler() {
        AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
        String accountId = "12345";
        String companyId = "Company";
        BigDecimal balance = BigDecimal.valueOf(1000);

        Mockito.when(accountRepository.findByAccountId(accountId)).thenReturn(new Account(accountId, balance));

        TraderInformationHandler theHandler = new TraderInformationHandler(accountRepository);
        theHandler.handleTraderInformation(accountId, companyId);

        verify(accountRepository, times(1)).findByAccountId(accountId);
        verify(accountRepository, times(1)).updateCompanyId(accountId, companyId);
    }

    @Test
    public void testTraderInformationHandlerUnknownAccount() {
        AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
        String accountId = "12345";
        String companyId = "Company";
        BigDecimal balance = BigDecimal.valueOf(1000);

        Mockito.when(accountRepository.findByAccountId(accountId)).thenReturn(null);

        TraderInformationHandler theHandler = new TraderInformationHandler(accountRepository);
        theHandler.handleTraderInformation(accountId, companyId);

        verify(accountRepository, times(1)).findByAccountId(accountId);
        verify(accountRepository, times(0)).updateCompanyId(accountId, companyId);
    }
}
