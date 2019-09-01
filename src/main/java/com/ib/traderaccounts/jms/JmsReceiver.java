package com.ib.traderaccounts.jms;

import com.ib.traderaccounts.businessobjects.TraderInformationHandler;
import com.ib.traderaccounts.dispatcher.ExecutionDispatcher;
import com.ib.traderaccounts.messages.TraderExecution;
import com.ib.traderaccounts.messages.TraderInformationResp;
import com.ib.traderaccounts.model.Execution;
import com.ib.traderaccounts.validation.FieldValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * Class responsible for receiving messages from the external systems. This is currently using ActiveMQ because that comes
 * with SpringBoot and I do not have my own MQ installation so this would need some small work to plug it into MQ I expect
 */
@Component
public class JmsReceiver extends JmsBase {

    final static Logger logger = Logger.getLogger(JmsReceiver.class.getName());

    @Autowired
    ExecutionDispatcher executionDispatcher;

    @Autowired
    TraderInformationHandler traderInformationHandler;

    @Autowired
    FieldValidator fieldValidator;

    public JmsReceiver(ExecutionDispatcher executionDispatcher, TraderInformationHandler traderInformationHandler, FieldValidator fieldValidator) {
        super();
        this.executionDispatcher = executionDispatcher;
        this.traderInformationHandler = traderInformationHandler;
        this.fieldValidator = fieldValidator;
    }

    @JmsListener(destination = "jms.traderexecutionmessage.endpoint")
    public void receiveMessage(TraderExecution msg)
    {
        logger.info("Received TraderExecution " + msg );
        try {
            // Call validation routines on each of the fields to ensure they are syntactically correct
            fieldValidator.validateTraderExecution(msg);
            Execution execution = new Execution(msg.getAccountId(),msg.getSymbolId(),msg.getQuantity(),msg.getPrice());
            executionDispatcher.dispatchExecution(execution);
        } catch ( Exception ex ) {
            logger.severe("Exception Handling TraderExecution :" + ex.toString());
        }
    }

    @JmsListener(destination = "jms.traderinforespmessage.endpoint")
    public void receiveMessage(TraderInformationResp msg)
    {
        logger.info("Received TraderInformationResp " + msg );
        try {
            fieldValidator.validateTraderInformationResp(msg);
            traderInformationHandler.handleTraderInformation(msg.getAccountId(), msg.getCompanyId());
        } catch ( Exception ex ) {
            logger.severe("Exception Handling TraderInformationResp :" + ex.toString());
        }
    }

}