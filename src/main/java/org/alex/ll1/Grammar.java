package org.alex.ll1;

import java.util.List;

public class Grammar {
    private List<String> nonTerminals;
    private List<String> terminals;
    private List<Production> productions;
    private String startSymbol;

    public Grammar() {}

    public Grammar(List<String> nonTerminals, List<String> terminals, List<Production> productions, String startSymbol) {
        this.nonTerminals = nonTerminals;
        this.terminals = terminals;
        this.productions = productions;
        this.startSymbol = startSymbol;
    }

    public List<String> getNonTerminals() {
        return nonTerminals;
    }

    public List<String> getTerminals() {
        return terminals;
    }

    public List<Production> getProductions() {
        return productions;
    }

    public String getStartSymbol() {
        return startSymbol;
    }

    public void setNonTerminals(List<String> nonTerminals) {
        this.nonTerminals = nonTerminals;
    }

    public void setTerminals(List<String> terminals) {
        this.terminals = terminals;
    }

    public void setProductions(List<Production> productions) {
        this.productions = productions;
    }

    public void setStartSymbol(String startSymbol) {
        this.startSymbol = startSymbol;
    }
}
