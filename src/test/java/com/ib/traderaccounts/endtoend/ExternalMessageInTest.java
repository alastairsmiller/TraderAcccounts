package com.ib.traderaccounts.endtoend;

import com.ib.traderaccounts.dao.AccountRepository;
import com.ib.traderaccounts.dao.ExecutionRepository;
import com.ib.traderaccounts.messages.TraderExecution;
import com.ib.traderaccounts.messages.TraderInformationReq;
import com.ib.traderaccounts.messages.TraderInformationResp;
import com.ib.traderaccounts.messages.TraderState;
import com.ib.traderaccounts.model.Account;
import com.ib.traderaccounts.model.Execution;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Class to test the app by sending message over the JMSTemplae into the system and also responding to
 * any JMSMessages received by the listeners set up in this test class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Scope(value="singlton")
public class ExternalMessageInTest {

    final static Logger logger = Logger.getLogger(ExternalMessageInTest.class.getName());

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ExecutionRepository executionRepository;
    @Autowired
    private JmsTemplate jmsTemplate;

    private String accountId;
    private static String companyId;
    private String symbolOne;
    private String symbolTwo;
    private BigDecimal price;
    private BigDecimal quantity;

    private AtomicInteger traderStateMessages = new AtomicInteger(0);
    private AtomicInteger traderInformationRequests= new AtomicInteger(0);
    private int numberOfExecutionMessages = 10;

    @Before
    public void onSetUp() {
        System.setProperty("java.util.logging.config.file", ClassLoader.getSystemResource("logger.properties").getPath());
        accountId = "12340";
        companyId = "Company1";
        symbolOne = "CSGN";
        symbolTwo = "UBSA";
        price = BigDecimal.valueOf(10.24);
        quantity = BigDecimal.valueOf(150);
        // Delete any data in the DB to stop an interference during this test with pervious test data
        accountRepository.deleteAllAccounts();
        executionRepository.deleteAllExecutions();
    }

    @Test
    public void testOneExternalMessage() {

        TraderExecution traderExecution = new TraderExecution();
        traderExecution.setAccountId(accountId);
        traderExecution.setSymbolId(symbolOne);
        traderExecution.setPrice(price);
        traderExecution.setQuantity(quantity);

        // Send a message into the app
        logger.info("Sending a test execution message. AccountId: "+traderExecution.getAccountId());
        jmsTemplate.convertAndSend("jms.traderexecutionmessage.endpoint", traderExecution);
        logger.info("Sent a test execution message. AccountId: "+traderExecution.getAccountId());
        // Wait for a short while to let the app do its thing
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Check we get the correct messages
        Assert.assertEquals(1, traderInformationRequests.get());
        Assert.assertEquals(1, traderStateMessages.get());

        // Check the data in the DB
        Account theAccount = accountRepository.findByAccountId(accountId);
        List<Execution> theExecutions = executionRepository.findByAccountId(accountId);
        Assert.assertEquals(theAccount.getCompanyId(), companyId);
        BigDecimal balance = BigDecimal.valueOf(1000).subtract(price).setScale(2);
        assertTrue(theAccount.getBalance().compareTo(balance) == 0);
        Assert.assertEquals(theExecutions.size(), 1);
        Execution theExe = theExecutions.get(0);
        Assert.assertEquals(theExe.getSymbol(), symbolOne);
        assertTrue(theExe.getPrice().compareTo(price)== 0);
        assertTrue(theExe.getQuantity().compareTo(quantity)==0);
    }

    @Test
    public void testManyExternalMesages() {
        TraderExecution traderExecution = new TraderExecution();
        traderExecution.setSymbolId("CSGN");
        traderExecution.setPrice(BigDecimal.valueOf(10.24));
        traderExecution.setQuantity(BigDecimal.valueOf(150));

        // Send a nuumber of messages, 10 executions bassed on 9 acccounts
        for ( int i = 0; i < numberOfExecutionMessages; i++) {
            if ( i == 5 ) {
                // Set Symbol 2 for the next 5 and leave the account ID for this Execution to test it
                traderExecution.setSymbolId(symbolTwo);
            } else {
                traderExecution.setAccountId("1234" + i);
            }
            traderExecution.setPrice(price.add(BigDecimal.valueOf(i)));
            logger.info("Sending a test execution message. AccountId: "+traderExecution.getAccountId());
            jmsTemplate.convertAndSend("jms.traderexecutionmessage.endpoint", traderExecution);
            logger.info("Sent a test execution message. AccountId: "+traderExecution.getAccountId());
        }
        // Wait for the app to catch and up finish its work
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Check the contents of the DB
        List<Account> theAccounts = accountRepository.findAll();
        List<Execution> theExecutions = executionRepository.findAll();
        // Check that we have 9 acocunts and 10 executions
        assertEquals(9, theAccounts.size());
        assertEquals(10, theExecutions.size());

    }

    @JmsListener(destination = "jms.traderinforeqmessage.endpoint")
    public void receiveMessage(TraderInformationReq msg)
    {
        logger.info("Monster Received TraderInformationReq " + msg.toString() );
        int traderReqs = traderInformationRequests.incrementAndGet();
        logger.info("Numer of Trader Requests is now " + traderReqs);
        TraderInformationResp traderInformationResp = new TraderInformationResp();
        traderInformationResp.setAccountId(msg.getAccountId());
        traderInformationResp.setCompanyId(companyId);
        logger.info("Sending an info response message." + traderInformationResp.toString());
        jmsTemplate.convertAndSend("jms.traderinforespmessage.endpoint", traderInformationResp);
        logger.info("Sent an info response message.");

    }

    @JmsListener(destination = "jms.traderstate.endpoint")
    public void receiveMessage(TraderState msg)
    {
        logger.info("Received TraderState " + msg.toString() );
        traderStateMessages.incrementAndGet();
    }

}
