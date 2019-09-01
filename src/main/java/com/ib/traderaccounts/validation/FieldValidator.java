package com.ib.traderaccounts.validation;

import com.ib.traderaccounts.messages.TraderExecution;
import com.ib.traderaccounts.messages.TraderInformationResp;
import org.springframework.stereotype.Component;

/**
 * Class responsible for validating the fields from the external systems. Exceptions are thrown
 * if the fields do not match the specification
 */
@Component
public class FieldValidator {
    public void validateTraderExecution(TraderExecution traderExecution) throws IllegalArgumentException {
        // Check the fields which really matter here
        if ( !traderExecution.getAccountId().matches("\\d{4,7}") ) {
            throw new IllegalArgumentException("AccountId Invalid Format :" + traderExecution.getAccountId());
        }
        if ( !traderExecution.getSymbolId().matches("[\\w]{3,7}|[\\w]{3}/[\\w]{3}") ) {
            throw new IllegalArgumentException("SymbolId Invalid Format :" + traderExecution.getAccountId());
        }
    }

    public void validateTraderInformationResp(TraderInformationResp msg) throws IllegalArgumentException {
        // Check the fields which really matter here
        if ( !msg.getAccountId().matches("\\d{4,7}") ) {
            throw new IllegalArgumentException("AccountId Invalid Format :" + msg.getAccountId());
        }
        if ( !msg.getCompanyId().matches("\\w{5,10}") ) {
            throw new IllegalArgumentException("CompanyId Invalid Format :" + msg.getAccountId());
        }
    }
}
