package com.ib.traderaccounts.businessobjects;

import com.ib.traderaccounts.dao.AccountRepository;
import com.ib.traderaccounts.dao.ExecutionRepository;
import com.ib.traderaccounts.jms.JmsSender;
import com.ib.traderaccounts.model.Account;
import com.ib.traderaccounts.model.Execution;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import static junit.framework.TestCase.fail;
import static org.mockito.Mockito.*;

public class ExecutionHandlerTest {

    private AccountRepository accountRepository;
    private ExecutionRepository executionRepository;
    private JmsSender jmsSender;

    private ExecutionHandler theHandler;

    @Before
    public void onSetUp() {
        accountRepository = Mockito.mock(AccountRepository.class);
        executionRepository = Mockito.mock(ExecutionRepository.class);
        jmsSender = Mockito.mock(JmsSender.class);

        theHandler = new ExecutionHandler();
        theHandler.setAccountRepository(accountRepository);
        theHandler.setExecutionRepository(executionRepository);
        theHandler.setJmsSender(jmsSender);
        theHandler.setExecutionQueue(new ArrayBlockingQueue<>(10));
    }

    @Test
    public void testExecutionHandlerNewAccountId()
    {
        String accountId = "12340";
        String symbol = "CSGN";
        BigDecimal quantity = BigDecimal.valueOf(100);
        BigDecimal price = BigDecimal.valueOf(20.10);
        BigDecimal balance = BigDecimal.valueOf(1000);
        Account theAccount = new Account(accountId, balance);
        Execution theExe = new Execution(accountId, symbol, quantity, price);

        Mockito.when(accountRepository.findByAccountId(accountId)).thenReturn(null);
        Mockito.when(executionRepository.findLastTenBySymbol(symbol)).thenReturn(new ArrayList<Execution>());

        // Call to the application code comes from here
        processExecution(theHandler,theExe);

        verify(accountRepository, times(1)).insert(theAccount);
        verify(jmsSender, times(1)).sendTraderInformationRequest(accountId);
        balance = BigDecimal.valueOf(979.90).setScale(2);
        verify(accountRepository, times(1)).updateBalance(accountId, balance);
        verify(executionRepository, times(1)).insert(theExe);
        verify(jmsSender, times(1)).sendTraderState(theAccount,theExe);
    }

    @Test
    public void testExecutionHandlerExistingAccountId()
    {
        String accountId = "12340";
        String symbol = "CSGN";
        BigDecimal quantity = BigDecimal.valueOf(100);
        BigDecimal price = BigDecimal.valueOf(20.10);
        BigDecimal balance = BigDecimal.valueOf(1000);

        Account theAccount = new Account(accountId, balance);
        Execution firstExe = new Execution(accountId, symbol, quantity, price);
        price = BigDecimal.valueOf(50.47);
        Execution secondExe = new Execution(accountId, symbol, quantity, price);
        price = BigDecimal.valueOf(25.55);
        Execution thirdExe = new Execution(accountId, symbol, quantity, price);
        List<Execution> theExecutions = new ArrayList<Execution>();
        theExecutions.add(secondExe);
        theExecutions.add(thirdExe);

        Mockito.when(accountRepository.findByAccountId(accountId)).thenReturn(theAccount);
        Mockito.when(executionRepository.findLastTenBySymbol(symbol)).thenReturn(theExecutions);

        processExecution(theHandler, firstExe);

        verify(accountRepository, never()).insert(theAccount);
        verify(jmsSender, never()).sendTraderInformationRequest(accountId);
        balance = BigDecimal.valueOf(967.96).setScale(2);
        verify(accountRepository, times(1)).updateBalance(accountId, balance);
        verify(executionRepository, times(1)).insert(firstExe);
        verify(jmsSender, times(1)).sendTraderState(theAccount,firstExe);
    }

    /**
     * Use reflection to be able to call the private method in the ExecutionHandler
     */
    private void processExecution(ExecutionHandler theHandler, Execution theExecution) {
        try {
            Method method = ExecutionHandler.class.getDeclaredMethod("processExecution", Execution.class);
            method.setAccessible(true);
            method.invoke(theHandler, theExecution);
        } catch ( Exception ex ) {
            fail("Exception thrown trying to call processExecution :" + ex.getMessage());
        }
    }
}
