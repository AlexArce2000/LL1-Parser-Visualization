package org.alex.ll1;

import java.util.*;

public class FirstFollowCalcular {

    public Map<String, Set<String>> calculateFirst(Grammar grammar) {
        Map<String, Set<String>> firstSets = new HashMap<>();

        // Inicializamos los FIRST sets para cada no terminal
        for (String nonTerminal : grammar.getNonTerminals()) {
            firstSets.put(nonTerminal, new HashSet<>());
        }

        boolean changed;
        do {
            changed = false;

            for (Production prod : grammar.getProductions()) {
                String nonTerminal = prod.getNonTerminal();
                String rule = prod.getProductionRule();

                // Si la producción tiene un terminal o ε
                if (grammar.getTerminals().contains(rule.substring(0, 1))) {
                    if (firstSets.get(nonTerminal).add(rule.substring(0, 1))) {
                        changed = true;
                    }
                }

                // Si la producción comienza con un no terminal
                else if (grammar.getNonTerminals().contains(rule.substring(0, 1))) {
                    Set<String> firstSetForRightHandSide = firstSets.get(rule.substring(0, 1));
                    for (String terminal : firstSetForRightHandSide) {
                        if (firstSets.get(nonTerminal).add(terminal)) {
                            changed = true;
                        }
                    }
                }
            }
        } while (changed);

        return firstSets;
    }

    // Método para calcular el FOLLOW set
    public Map<String, Set<String>> calculateFollow(Grammar grammar, Map<String, Set<String>> firstSets) {
        Map<String, Set<String>> followSets = new HashMap<>();

        // Inicializar los FOLLOW sets
        for (String nonTerminal : grammar.getNonTerminals()) {
            followSets.put(nonTerminal, new HashSet<>());
        }

        // El FOLLOW del símbolo inicial contiene el símbolo de fin de cadena
        followSets.get(grammar.getStartSymbol()).add("$");

        boolean changed;
        do {
            changed = false;

            for (Production prod : grammar.getProductions()) {
                String nonTerminal = prod.getNonTerminal();
                String rule = prod.getProductionRule();

                // Para cada símbolo en la producción, buscar los FOLLOW
                for (int i = 0; i < rule.length(); i++) {
                    String symbol = String.valueOf(rule.charAt(i));

                    if (grammar.getNonTerminals().contains(symbol)) {
                        Set<String> followSetForLeftHandSide = followSets.get(symbol);

                        // Si no es el último símbolo
                        if (i < rule.length() - 1) {
                            String nextSymbol = String.valueOf(rule.charAt(i + 1));
                            Set<String> firstSetForNext = firstSets.get(nextSymbol);
                            if (followSetForLeftHandSide.addAll(firstSetForNext)) {
                                changed = true;
                            }
                        }

                        // Si es el último símbolo de la producción
                        else {
                            Set<String> followSetForNonTerminal = followSets.get(nonTerminal);
                            if (followSetForLeftHandSide.addAll(followSetForNonTerminal)) {
                                changed = true;
                            }
                        }
                    }
                }
            }
        } while (changed);

        return followSets;
    }
}
