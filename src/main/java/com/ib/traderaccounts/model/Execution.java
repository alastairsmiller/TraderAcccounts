package com.ib.traderaccounts.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "EXECUTIONS")
public class Execution implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "accountid")
    @Id
    private String accountId;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "quantity")
    private BigDecimal quantity;

    @Column(name = "price")
    private BigDecimal price;

    public Execution(String accountId, String symbol, BigDecimal quantity, BigDecimal price) {
        this.accountId = accountId;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
    }

    public String getAccountId() { return accountId; }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object other) {
        if ( other != null && (other instanceof Execution) && accountId.equals(((Execution)other).accountId) && symbol.equals(((Execution)other).symbol)
                && quantity.compareTo(((Execution)other).quantity) == 0 && price.compareTo(((Execution)other).price) == 0) {
            return true;
        } else {
            return false;
        }
    }
}
