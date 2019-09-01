package com.ib.traderaccounts.jms;

import com.ib.traderaccounts.messages.TraderInformationReq;
import com.ib.traderaccounts.messages.TraderState;
import com.ib.traderaccounts.model.Account;
import com.ib.traderaccounts.model.Execution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * Class responsible for sending messages to the external systems. This is currently using ActiveMQ because that comes
 * with SpringBoot and I do not have my own MQ installation so this would need some small work to plug it into MQ I expect
 */
@Component
public class JmsSender extends JmsBase{

    final static Logger logger = Logger.getLogger(JmsSender.class.getName());

    @Autowired
    JmsTemplate jmsTemplate;

    public JmsSender(JmsTemplate jmsTemplate) {
        super();
        this.jmsTemplate = jmsTemplate;
    }

    public void sendTraderInformationRequest(String accountId) {
        TraderInformationReq traderInformationReq = new TraderInformationReq();
        traderInformationReq.setAccountId(accountId);
        logger.info("Sending " + traderInformationReq );
        jmsTemplate.convertAndSend("jms.traderinforeqmessage.endpoint", traderInformationReq);
        logger.info("Senting " + traderInformationReq );
    }

    public void sendTraderState(Account theAcount, Execution theExecution) {
        TraderState traderState = new TraderState();
        traderState.setAccountId(theExecution.getAccountId());
        traderState.setCompanyId(theAcount.getCompanyId());
        traderState.setBalance(theAcount.getBalance());
        logger.info("Sending " + traderState );
        jmsTemplate.convertAndSend("jms.traderstate.endpoint", traderState);
        logger.info("Senting " + traderState );
    }

}