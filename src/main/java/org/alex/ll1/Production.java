package org.alex.ll1;

public class Production {
    private String nonTerminal;
    private String productionRule;

    public Production(String nonTerminal, String productionRule) {
        this.nonTerminal = nonTerminal;
        this.productionRule = productionRule;
    }

    public String getNonTerminal() {
        return nonTerminal;
    }

    public String getProductionRule() {
        return productionRule;
    }

    @Override
    public String toString() {
        return nonTerminal + " -> " + productionRule;
    }
}
