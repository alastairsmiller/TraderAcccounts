package com.ib.traderaccounts.businessobjects;

import com.ib.traderaccounts.dao.AccountRepository;
import com.ib.traderaccounts.dao.ExecutionRepository;
import com.ib.traderaccounts.model.Account;
import com.ib.traderaccounts.model.Execution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Cals responsible for the report generation. This is called from the RestController. The RestController
 * is limited to only run one of these at any single time.
 */
@Component
public class ReportGenerator implements Callable<Boolean> {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ExecutionRepository executionRepository;

    @Value("${report.file.path}")
    private String reportFilePath;

    public ReportGenerator(AccountRepository accountRepository, ExecutionRepository executionRepository) {
        this.accountRepository = accountRepository;
        this.executionRepository = executionRepository;
    }

    public void setReportFilePath(String reportFilePath) {
        this.reportFilePath = reportFilePath;
    }

    @Override
    public Boolean call() throws Exception {
        // Get all the Accounts and all the executions and create the report
        List<Account> theAccounts = accountRepository.findAll();
        List<Execution> theExecutions = executionRepository.findAll();

        // Create and open the report file
        String fileName = reportFilePath + String.valueOf(System.currentTimeMillis()) + ".txt";
        PrintWriter writer = new PrintWriter(fileName, "UTF-8");
        createCompaniesReport(writer,theAccounts,theExecutions);
        createTradersReport(writer,theAccounts,theExecutions);
        writer.close();
        return true;
    }

    private void createCompaniesReport(PrintWriter writer, List<Account> theAccounts, List<Execution> theExecutions) {
        // Get a distinct list of all the companies
        List<String> theCompanies = theAccounts.stream().filter(acc -> acc.getCompanyId()!=null).filter(distinctByKey(Account::getCompanyId)).map(Account::getCompanyId).collect(Collectors.toList());

        // For each company, get and sum all the balances and also get the executions
        Iterator<String> theCompaniesIter = theCompanies.iterator();
        while ( theCompaniesIter.hasNext() ) {
            String companyId = theCompaniesIter.next();
            // Work out the Sum of all the balances for this company
            BigDecimal companyBalance = theAccounts.stream().filter(acc -> companyId.equals(acc.getCompanyId())).map(Account::getBalance).reduce(BigDecimal.ZERO,BigDecimal::add).setScale(2, RoundingMode.HALF_EVEN);
            // Get the AccountIds associated with this company
            List<String> accountIds = theAccounts.stream().filter(acc -> companyId.equals(acc.getCompanyId())).map(Account::getAccountId).collect(Collectors.toList());
            // Get the Executions which were done on these accountids
            List<Execution> companyExecutions = theExecutions.stream().filter(exe->accountIds.contains(exe.getAccountId())).collect(Collectors.toList());

            // Create the contents for the report file
            String nextLine = new String("Company Id :").concat(companyId).concat(" Balance : ").concat(companyBalance.toString()).concat("\n")
                .concat(companyExecutions.stream().map(exe->" Symbol :"+exe.getSymbol()+" Quantity : "+exe.getQuantity()+" Price :"+exe.getPrice()+'\n').reduce("", String::concat));

            writer.println(nextLine);
        }
    }

    private void createTradersReport(PrintWriter writer, List<Account> theAccounts, List<Execution> theExecutions) {
        // For each AccountId get the list of executions associated with that account
        Iterator<Account> accountsIter = theAccounts.iterator();
        while ( accountsIter.hasNext() ) {
            Account nextAccount = accountsIter.next();
            // Get a list of all this accounts executions
            List<Execution> accountExes = theExecutions.stream().filter(exe->exe.getAccountId().equals(nextAccount.getAccountId())).collect(Collectors.toList());
            // Write the data to a buffer and then write it out to the file
            String nextLine = new String("AccountId :").concat(nextAccount.getAccountId()).concat(" Company Id :").concat(nextAccount.getCompanyId()).concat(" Balance : ").concat(nextAccount.getBalance().toString()).concat("\n")
                .concat(accountExes.stream().map(exe->" Symbol :"+exe.getSymbol()+" Quantity : "+exe.getQuantity()+" Price :"+exe.getPrice()+'\n').reduce("", String::concat));
            writer.println(nextLine);
        }
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        final Set<Object> seen = new HashSet<>();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
