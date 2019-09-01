package com.ib.traderaccounts.messages;

import java.math.BigDecimal;

/**
 * Class to hold the data required for a TraderExecution message
 */
public class TraderExecution {
    private String accountId;
    private String symbolId;
    private BigDecimal quantity;
    private BigDecimal price;

    public String getAccountId() { return accountId; }

    public void setAccountId(String accountId) { this.accountId = accountId; }

    public String getSymbolId() { return symbolId; }

    public void setSymbolId(String symbolId) { this.symbolId = symbolId; }

    public BigDecimal getQuantity() {  return quantity; }

    public void setQuantity(BigDecimal quantity) { this.quantity = quantity;  }

    public BigDecimal getPrice() { return price; }

    public void setPrice(BigDecimal price) { this.price = price; }

    public String toString() {
        return new String("TraderExecution :").concat(accountId)
                .concat(" Sybmbol :").concat(symbolId).concat(" Quantity :")
                .concat(quantity.toString()).concat(" Price :").concat(price.toString());
    }

    @Override
    public boolean equals(Object other) {
        if ( other!= null && (other instanceof TraderExecution) && accountId.equals(((TraderExecution)other).accountId)
                && (symbolId.equals(((TraderExecution)other).symbolId))
                && quantity.compareTo(((TraderExecution)other).quantity) == 0
                && price.compareTo(((TraderExecution)other).price) == 0){
            return true;
        } else {
            return false;
        }
    }
}
