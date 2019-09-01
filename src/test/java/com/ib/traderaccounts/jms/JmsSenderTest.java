package com.ib.traderaccounts.jms;

import com.ib.traderaccounts.messages.TraderInformationReq;
import com.ib.traderaccounts.messages.TraderState;
import com.ib.traderaccounts.model.Account;
import com.ib.traderaccounts.model.Execution;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.jms.core.JmsTemplate;

import java.math.BigDecimal;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class JmsSenderTest {

    @Test
    public void testSendTraderInformationRequest() {
        JmsTemplate jmsTemplate = Mockito.mock(JmsTemplate.class);

        String accountId = "1234";
        TraderInformationReq traderInformationReq = new TraderInformationReq();
        traderInformationReq.setAccountId(accountId);

        JmsSender jmsSender = new JmsSender(jmsTemplate);
        jmsSender.sendTraderInformationRequest(accountId);

        verify(jmsTemplate, times(1)).convertAndSend("jms.traderinforeqmessage.endpoint",traderInformationReq);
    }

    @Test
    public void testSendTraderState() {
        JmsTemplate jmsTemplate = Mockito.mock(JmsTemplate.class);

        Execution theExe = new Execution("1234", "CSGN", BigDecimal.valueOf(100), BigDecimal.valueOf(50));
        Account theAccount = new Account("1224", BigDecimal.valueOf(1000));
        theAccount.setCompanyId("Company");
        TraderState traderState = new TraderState();
        traderState.setAccountId(theExe.getAccountId());
        traderState.setCompanyId(theAccount.getCompanyId());
        traderState.setBalance(theAccount.getBalance());

        JmsSender jmsSender = new JmsSender(jmsTemplate);
        jmsSender.sendTraderState(theAccount, theExe);

        verify(jmsTemplate, times(1)).convertAndSend("jms.traderstate.endpoint", traderState);
    }

}
