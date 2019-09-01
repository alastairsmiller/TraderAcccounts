package com.ib.traderaccounts.dispatcher;

import com.ib.traderaccounts.businessobjects.ExecutionHandler;
import com.ib.traderaccounts.dao.AccountRepository;
import com.ib.traderaccounts.dao.ExecutionRepository;
import com.ib.traderaccounts.jms.JmsSender;
import com.ib.traderaccounts.model.Execution;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static junit.framework.TestCase.assertTrue;

/**
 * Test that the Dispatcher puts the Execution on the corrent queue and so distributes the
 * Executions evenly around.
 */
public class ExecutionDispatcherTest {

    @Test
    public void testDisptachExecution() {
        // Set up objects
        ExecutionHandler executionHandler = Mockito.mock(ExecutionHandler.class);
        AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
        ExecutionRepository executionRepository = Mockito.mock(ExecutionRepository.class);
        ExecutionDispatcherHelper executionDispatcherHelper = Mockito.mock(ExecutionDispatcherHelper.class);
        JmsSender jmsSender = Mockito.mock(JmsSender.class);
        Thread thread  = Mockito.mock(Thread.class);
        BlockingQueue<Execution> executionQueue = Mockito.mock(BlockingQueue.class);

        Mockito.when(executionDispatcherHelper.createNewExecutionHandler()).thenReturn(executionHandler);

        ExecutionDispatcher executionDispatcher = new ExecutionDispatcher(executionDispatcherHelper);
        executionDispatcher.setDispatchersNumbersStr("10");
        executionDispatcher.setDispatchersQueueLengthStr("10");
        executionDispatcher.init();

        // Create an Execution with Account ending in 0
        Execution theExe = new Execution("12340", "CSGN", BigDecimal.valueOf(100), BigDecimal.valueOf(1000));
        executionDispatcher.dispatchExecution(theExe);
        List<BlockingQueue<Execution>> theDispatchers = executionDispatcher.getMyDispatchers();
        BlockingQueue<Execution> theQueue = theDispatchers.get(0);
        assertTrue(theQueue.contains(theExe));

        // Create an Execution with Account ending in 1
        theExe = new Execution("12341", "CSGN", BigDecimal.valueOf(100), BigDecimal.valueOf(1000));
        executionDispatcher.dispatchExecution(theExe);
        theDispatchers = executionDispatcher.getMyDispatchers();
        theQueue = theDispatchers.get(1);
        assertTrue(theQueue.contains(theExe));

        // Create an Execution with Account ending in 4
        theExe = new Execution("12344", "CSGN", BigDecimal.valueOf(100), BigDecimal.valueOf(1000));
        executionDispatcher.dispatchExecution(theExe);
        theDispatchers = executionDispatcher.getMyDispatchers();
        theQueue = theDispatchers.get(4);
        assertTrue(theQueue.contains(theExe));

        // Create an Execution with Account ending in 6
        theExe = new Execution("12346", "CSGN", BigDecimal.valueOf(100), BigDecimal.valueOf(1000));
        executionDispatcher.dispatchExecution(theExe);
        theDispatchers = executionDispatcher.getMyDispatchers();
        theQueue = theDispatchers.get(6);
        assertTrue(theQueue.contains(theExe));

        // Create an Execution with Account ending in 9
        theExe = new Execution("12349", "CSGN", BigDecimal.valueOf(100), BigDecimal.valueOf(1000));
        executionDispatcher.dispatchExecution(theExe);
        theDispatchers = executionDispatcher.getMyDispatchers();
        theQueue = theDispatchers.get(9);
        assertTrue(theQueue.contains(theExe));
    }
}
