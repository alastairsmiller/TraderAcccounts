package com.ib.traderaccounts.dispatcher;

import com.ib.traderaccounts.businessobjects.ExecutionHandler;
import com.ib.traderaccounts.model.Execution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

/**
 * Class which creates a configured number of Execution handlers and BlockingQueues.
 * The class also maintains a Queue (and therefore thread) affinity based on AccountId
 * to ensure that each Execution for a specific account is handled sequentially and not
 * in parallel
 */
@Component
public class ExecutionDispatcher {

    final static Logger logger = Logger.getLogger(ExecutionDispatcher.class.getName());

    @Autowired
    private ExecutionDispatcherHelper dispatcherHelper;

    @Value("${execution.dispatchers}")
    private String dispatchersNumbersStr;
    // Store this as an int also to save working it out time and time again
    private Integer numDispatchers;

    @Value("${execution.dispatchers.queue.length}")
    private String dispatchersQueueLengthStr;

    // The list of blocking queues
    private List<BlockingQueue<Execution>> myDispatchers;

    public ExecutionDispatcher(ExecutionDispatcherHelper dispatcherHelper) {
        this.dispatcherHelper = dispatcherHelper;
    }

    public List<BlockingQueue<Execution>> getMyDispatchers() {
        return ( myDispatchers );
    }

    public void setDispatchersNumbersStr(String dispatchersNumbersStr) {
        this.dispatchersNumbersStr = dispatchersNumbersStr;
    }

    public void setDispatchersQueueLengthStr(String dispatchersQueueLengthStr) {
        this.dispatchersQueueLengthStr = dispatchersQueueLengthStr;
    }

    /**
     * Set up the correct number of blocking queues and associated threads to read from them
     * Uses the DispatcherHelper to create each thread and start each one
     */
    @PostConstruct
    public void init(){
        logger.info("dispatchersNumbersStr is "+dispatchersNumbersStr);
        numDispatchers = Integer.valueOf(dispatchersNumbersStr);
        Integer dispatchersQueueLength = Integer.valueOf(dispatchersQueueLengthStr);

        myDispatchers = new ArrayList<>();
        for ( int i = 0; i < numDispatchers; i++ ) {
            BlockingQueue<Execution> blockingQueue = new ArrayBlockingQueue<>(dispatchersQueueLength);
            ExecutionHandler theHandler = dispatcherHelper.createNewExecutionHandler();
            theHandler.setExecutionQueue(blockingQueue);
            dispatcherHelper.startExecutionHandler(theHandler);
            myDispatchers.add(blockingQueue);
        }
    }


    /**
     * Uses the AccountId to determine which BlockingQueue to add this execution to.
     * Account ID has been validated as a number so its safe. Take the last digit
     * to find out which queue to send the execution to. This keeps all executions
     * for one account in a sequential queue which saves the dreaded synchronisation
     */
    public void dispatchExecution(Execution theExecution) {
        String accountId = theExecution.getAccountId();
        String substring = accountId.substring(accountId.length() - 1);
        Integer dispatchNumber = Integer.valueOf(substring);
        BlockingQueue<Execution> dispatchQueue =  myDispatchers.get(dispatchNumber);
        dispatchQueue.add(theExecution);
    }
}
