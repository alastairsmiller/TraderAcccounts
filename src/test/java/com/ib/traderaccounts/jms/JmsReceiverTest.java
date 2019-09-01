package com.ib.traderaccounts.jms;

import com.ib.traderaccounts.businessobjects.TraderInformationHandler;
import com.ib.traderaccounts.dispatcher.ExecutionDispatcher;
import com.ib.traderaccounts.messages.TraderExecution;
import com.ib.traderaccounts.messages.TraderInformationResp;
import com.ib.traderaccounts.model.Execution;
import com.ib.traderaccounts.validation.FieldValidator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class JmsReceiverTest {

    @Before
    public void onSetUp()
    {
        System.setProperty("java.util.logging.config.file", ClassLoader.getSystemResource("logger.properties").getPath());
    }

    @Test
    public void testReceiveExecution() {
        ExecutionDispatcher executionDispatcher = Mockito.mock(ExecutionDispatcher.class);
        TraderInformationHandler traderInformationHandler = Mockito.mock(TraderInformationHandler.class);
        FieldValidator fieldValidator = Mockito.mock(FieldValidator.class);

        TraderExecution theExe = new TraderExecution();
        theExe.setAccountId("1224");
        theExe.setSymbolId("CSGN");
        theExe.setPrice(BigDecimal.valueOf(1234));
        theExe.setQuantity(BigDecimal.valueOf(400));

        JmsReceiver jmsReceiver = new JmsReceiver(executionDispatcher,traderInformationHandler, fieldValidator);
        jmsReceiver.receiveMessage(theExe);

        verify(fieldValidator, times(1)).validateTraderExecution(theExe);
        Execution execution = new Execution(theExe.getAccountId(),theExe.getSymbolId(),theExe.getQuantity(),theExe.getPrice());
        verify(executionDispatcher, times(1)).dispatchExecution(execution);
    }

    @Test
    public void testReceiveTraderInfo() {
        ExecutionDispatcher executionDispatcher = Mockito.mock(ExecutionDispatcher.class);
        TraderInformationHandler traderInformationHandler = Mockito.mock(TraderInformationHandler.class);
        FieldValidator fieldValidator = Mockito.mock(FieldValidator.class);

        TraderInformationResp theResp = new TraderInformationResp();
        theResp.setAccountId("1234");
        theResp.setCompanyId("Company");

        JmsReceiver jmsReceiver = new JmsReceiver(executionDispatcher,traderInformationHandler, fieldValidator);
        jmsReceiver.receiveMessage(theResp);

        verify(fieldValidator, times(1)).validateTraderInformationResp(theResp);

        verify(traderInformationHandler, times(1)).handleTraderInformation(theResp.getAccountId(), theResp.getCompanyId());
    }

}
