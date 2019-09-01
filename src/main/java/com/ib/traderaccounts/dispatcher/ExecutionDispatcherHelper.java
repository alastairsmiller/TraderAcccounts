package com.ib.traderaccounts.dispatcher;

import com.ib.traderaccounts.businessobjects.ExecutionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Helper class for the Execution Dispatcher. This class is really only needed to enable a decent test of the Dispatcher itself
 */
@Component
public class ExecutionDispatcherHelper {

    @Autowired
    private ApplicationContext context;

    /**
     * Returns a handle to the ExecutionHandler
     */
    public ExecutionHandler createNewExecutionHandler() {
        ExecutionHandler theHandler = (ExecutionHandler) context.getBean(ExecutionHandler.class);
        return (theHandler);
    }

    /**
     * Starts the ExecutionHandler which then reads for the specific queue associated with it
     */
    public void startExecutionHandler(ExecutionHandler theHandler) {
        new Thread(theHandler).start();
    }
}
