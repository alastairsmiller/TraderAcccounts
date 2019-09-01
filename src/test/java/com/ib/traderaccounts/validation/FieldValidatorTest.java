package com.ib.traderaccounts.validation;

import com.ib.traderaccounts.messages.TraderExecution;
import com.ib.traderaccounts.messages.TraderInformationResp;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;

public class FieldValidatorTest {

    private FieldValidator theValidator;

    @Before
    public void onSetUp() {
        theValidator = new FieldValidator();
    }

    @Test
    public void testTraderExecutionFormat() {
        testBadAccountIdInExecution("123");
        testBadAccountIdInExecution("12345678");
        testGoodAccountIdInExecution("1234");
        testGoodSymbolInExecution("CSGN");
        testGoodSymbolInExecution("XYZ/ABC");
        testBadSymbolInExecution("XYZ/ABCD");
        testBadSymbolInExecution("XYZABCD8");
    }

    @Test
    public void testTraderInformationRespFormat() {
        testBadAccountIdInTraderResp("123");
        testBadAccountIdInTraderResp("12345678");
        testGoodAccountIdInTraderResp("1234");
        testBadCompanyIdInTraderResp("Comp");
        testBadCompanyIdInTraderResp("CompanyABCDEF");
        testGoodCompanyIdInTraderResp("Company1");
    }


    private void testBadSymbolInExecution(String symbol) {
        TraderExecution theExe = new TraderExecution();
        try {
            theExe.setAccountId("1234");
            theExe.setSymbolId(symbol);
            theValidator.validateTraderExecution(theExe);
            fail("Symbol "+symbol+" is invalid but passed validation tests");
        } catch ( IllegalArgumentException iae) {
            // Test passed
        }
    }
    private void testGoodSymbolInExecution(String symbol){
        TraderExecution theExe = new TraderExecution();
        try {
            theExe.setAccountId("1234");
            theExe.setSymbolId(symbol);
            theValidator.validateTraderExecution(theExe);
        } catch ( IllegalArgumentException iae) {
            fail("Symbol "+symbol+" is valid but failed validation tests");
        }
    }
    private void testBadAccountIdInExecution(String accountId) {
        TraderExecution theExe = new TraderExecution();
        try {
            theExe.setAccountId(accountId);
            theValidator.validateTraderExecution(theExe);
            fail("Account "+accountId+" is invalid but passed validation tests");
        } catch ( IllegalArgumentException iae) {
            // Test passed
        }
    }
    private void testGoodAccountIdInExecution(String accountId) {
        TraderExecution theExe = new TraderExecution();
        try {
            theExe.setAccountId(accountId);
            theExe.setSymbolId("CSGN");
            theValidator.validateTraderExecution(theExe);
        } catch ( IllegalArgumentException iae) {
            fail("Account "+accountId+" is valid but failed validation tests");
        }
    }

    private void testBadAccountIdInTraderResp(String accountId) {
        TraderInformationResp theResp = new TraderInformationResp();
        try {
            theResp.setAccountId(accountId);
            theValidator.validateTraderInformationResp(theResp);
            fail("Account "+accountId+" is invalid but passed validation tests");
        } catch ( IllegalArgumentException iae) {
            // Test passed
        }
    }
    private void testGoodAccountIdInTraderResp(String accountId) {
        TraderInformationResp theResp = new TraderInformationResp();
        try {
            theResp.setAccountId(accountId);
            theResp.setCompanyId("Company");
            theValidator.validateTraderInformationResp(theResp);
        } catch ( IllegalArgumentException iae) {
            fail("Account "+accountId+" is valid but failed validation tests");
        }
    }
    private void testBadCompanyIdInTraderResp(String companyId) {
        TraderInformationResp theResp = new TraderInformationResp();
        try {
            theResp.setAccountId("1234");
            theResp.setCompanyId(companyId);
            theValidator.validateTraderInformationResp(theResp);
            fail("CompanyId "+companyId+" is invalid but passed validation tests");
        } catch ( IllegalArgumentException iae) {
            // Test passed
        }
    }
    private void testGoodCompanyIdInTraderResp(String companyId) {
        TraderInformationResp theResp = new TraderInformationResp();
        try {
            theResp.setAccountId("1234");
            theResp.setCompanyId(companyId);
            theValidator.validateTraderInformationResp(theResp);
        } catch ( IllegalArgumentException iae) {
            fail("CompanyId "+companyId+" is valid but failed validation tests");
        }
    }
}
