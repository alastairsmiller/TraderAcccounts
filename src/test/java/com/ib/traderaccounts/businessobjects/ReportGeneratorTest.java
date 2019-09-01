package com.ib.traderaccounts.businessobjects;

import com.ib.traderaccounts.dao.AccountRepository;
import com.ib.traderaccounts.dao.ExecutionRepository;
import com.ib.traderaccounts.model.Account;
import com.ib.traderaccounts.model.Execution;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

public class ReportGeneratorTest {

    private List<Account> accounts;
    private List<Execution> executions;

    @Before
    public void onSetUp() {
        createTestData();
    }

    @Test
    public void testReportGenerator()
    {
        // Set up objects
        AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
        ExecutionRepository executionRepository = Mockito.mock(ExecutionRepository.class);

        Mockito.when(accountRepository.findAll()).thenReturn(accounts);
        Mockito.when(executionRepository.findAll()).thenReturn(executions);

        ReportGenerator theGenerator = new ReportGenerator(accountRepository, executionRepository);
        theGenerator.setReportFilePath("C:\\Users\\alast\\Documents\\");
        try {
            theGenerator.call();
        } catch ( Exception ex ) {
            fail("Exception thrown :"+ex.getMessage());
        }
        // I could mock out the PrintWriter and write a load of Mock checks but in reality it is probably better to check the contents
        // of that file by hand or I would be here for hours checking the file line by line.
    }

    /**
     * Private helper method to create the test data required. I have used 3 accounts which belong
     * to 3 companies. One company ha no executions yet.
     */
    private void createTestData() {
        accounts = new ArrayList<>();
        executions = new ArrayList<>();

        BigDecimal balance = BigDecimal.valueOf(5000);
        for ( int i = 0; i < 3; i++ ) {
            balance = balance.add(BigDecimal.valueOf(i));
            Account theAccount = new Account("1234" + i, balance);
            if (i < 2){
                theAccount.setCompanyId("Company" + i);
            } else {
                theAccount.setCompanyId("Company" + 1);
            }
            accounts.add(theAccount);
        }
        String accountId;
        String symbol;
        BigDecimal quantity = BigDecimal.valueOf(300);
        BigDecimal price = BigDecimal.valueOf(20.16);
        for ( int i = 0; i < 10; i++ ) {
            if( i<5 ) {
                accountId = "12340";
                symbol = "CSGN";
            } else {
                accountId = "12341";
                symbol = "IBNA";
            }
            quantity = quantity.add(BigDecimal.valueOf(i));
            price = price.add(BigDecimal.valueOf(i));
            Execution theExecution = new Execution(accountId, symbol, quantity, price);
            executions.add(theExecution);
        }
    }
}
