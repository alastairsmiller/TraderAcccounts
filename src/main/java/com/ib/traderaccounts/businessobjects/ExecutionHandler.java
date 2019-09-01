package com.ib.traderaccounts.businessobjects;

import com.ib.traderaccounts.dao.AccountRepository;
import com.ib.traderaccounts.dao.ExecutionRepository;
import com.ib.traderaccounts.jms.JmsSender;
import com.ib.traderaccounts.model.Account;
import com.ib.traderaccounts.model.Execution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

/**
 * There are X number of HandleExecutions configured. Each thread listens to its own blocking Queue. Each
 * blocking queue will always receive executions for a specific account so each thread always handles the same account
 * This is to ensure that any given Execution for a specific AccountId is handled sequentially in order
 * to save any Syncronisation in the code
 */
@Component
@Scope(value = "prototype")
public class ExecutionHandler implements Runnable {

    final static Logger logger = Logger.getLogger(ExecutionHandler.class.getName());

    // Queue from which to take the executions as they arrive
    private BlockingQueue<Execution> executionQueue;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ExecutionRepository executionRepository;
    @Autowired
    private JmsSender jmsSender;

    public void setExecutionQueue(BlockingQueue<Execution> executionQueue) {
        this.executionQueue = executionQueue;
    }

    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void setExecutionRepository(ExecutionRepository executionRepository) {
        this.executionRepository = executionRepository;
    }

    public void setJmsSender(JmsSender jmsSender) {
        this.jmsSender = jmsSender;
    }

    @Override
    public void run() {
        try {
            while( true ) {
                Execution theExecution = executionQueue.take();
                logger.info("ExecutionHandler received Execution :" +theExecution.toString());
                processExecution(theExecution);
            }
        } catch (InterruptedException ex) {
            logger.info("ExecutionHandler Interrupted :" + ex.toString());
        } catch ( Throwable th ) {
            // Catch Throwable so we can tell why this thread died (if it did)
            logger.severe("ExecutionHandler Caught throwable :"+ th.toString());
        }
    }

    /**
     * Method to process this execution. We don't need to worry about synchronisation because the ExecutionDispatcher
     * maintains a thread affinity based on the AccountId. So each specific Account is always handled by the same thread
     */
    private void processExecution(Execution theExecution) {
        // Do we know this account already
        Account theAccount = accountRepository.findByAccountId(theExecution.getAccountId());
        if ( theAccount == null ) {
            logger.info("This is a new account. Account Id: "+theExecution.getAccountId());
            // Insert the Account placeholder (default the initial balance to 1K)
            theAccount = new Account(theExecution.getAccountId(), BigDecimal.valueOf(1000));
            accountRepository.insert(theAccount);
            // Send off a request to get the Company Id for this accountId
            jmsSender.sendTraderInformationRequest(theExecution.getAccountId());
        }
        // Modify the balance using this execution and fire off a traderstate message
        BigDecimal newBalance = calculateNewBalance(theAccount, theExecution);
        logger.info("Account has new balance :" + newBalance.toString());
        // Update or create the account and create a new Execution in the DB
        accountRepository.updateBalance(theAccount.getAccountId(), newBalance);
        // Finally store the execution itself and send out the trader state to the UI
        executionRepository.insert(theExecution);
        jmsSender.sendTraderState(theAccount, theExecution);

    }

    /**
     * This really should take into account the Side of the trade, Buy or Sell but it doesn't which will make the figures
     * look really odd
     */
    private BigDecimal calculateNewBalance(Account theAccount, Execution theExecution) {
        // Get the last 9 Executions related to this instrument
        List<Execution> previousExecutions = executionRepository.findLastTenBySymbol(theExecution.getSymbol());
        // Add the execution we are handling to make it up to the last 10 executions
        previousExecutions.add(theExecution);

        BigDecimal sum = previousExecutions.stream().map(Execution::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal average = sum.divide(new BigDecimal(previousExecutions.size()), RoundingMode.HALF_EVEN);
        return ( theAccount.getBalance().subtract(average).setScale(2, RoundingMode.HALF_EVEN) );
    }

}
