package org.alex.ll1;

import java.util.*;

public class LL1Parser {

    private Grammar grammar;
    private Map<String, Set<String>> firstSets;
    private Map<String, Set<String>> followSets;
    private Map<String, Map<String, Production>> parsingTable;

    public LL1Parser(Grammar grammar, Map<String, Set<String>> firstSets, Map<String, Set<String>> followSets) {
        this.grammar = grammar;
        this.firstSets = firstSets;
        this.followSets = followSets;
        this.parsingTable = new HashMap<>();
        buildParsingTable();
    }

    // Método para construir la tabla de análisis LL(1)
    private void buildParsingTable() {
        for (String nonTerminal : grammar.getNonTerminals()) {
            parsingTable.put(nonTerminal, new HashMap<>());
        }

        // Rellenar la tabla M con las producciones
        for (Production production : grammar.getProductions()) {
            String nonTerminal = production.getNonTerminal();
            String rightSide = production.getProductionRule();

            // Si la producción no es ε, obtenemos el conjunto FIRST de la parte derecha
            Set<String> firstSet = firstSets.get(nonTerminal);

            // Procesamos cada símbolo de la parte derecha
            if (rightSide.equals("ε")) {
                // Si la producción es ε, se usa FOLLOW
                for (String terminal : followSets.get(nonTerminal)) {
                    parsingTable.get(nonTerminal).put(terminal, production);
                }
            } else {
                // Si no es ε, procesamos los terminales del conjunto FIRST
                for (String terminal : firstSets.get(rightSide.substring(0, 1))) {
                    parsingTable.get(nonTerminal).put(terminal, production);
                }
            }
        }
    }

    // Método para realizar el análisis sintáctico de una cadena de entrada
    public boolean parseInputString(String input) {
        // Convertir la cadena de entrada en una lista de símbolos
        List<String> inputSymbols = new ArrayList<>(Arrays.asList(input.split(" ")));
        Stack<String> stack = new Stack<>();
        stack.push("$");  // Símbolo de fin de cadena
        stack.push(grammar.getStartSymbol()); // Símbolo de inicio

        while (!stack.isEmpty()) {
            String top = stack.pop();

            // Si el tope de la pila es un terminal
            if (grammar.getTerminals().contains(top)) {
                if (top.equals(inputSymbols.get(0))) {
                    inputSymbols.remove(0);  // Consumir el símbolo de entrada
                } else {
                    return false;  // Error de coincidencia
                }
            } else {
                // Si el tope de la pila es un no terminal, usamos la tabla M
                if (parsingTable.get(top).containsKey(inputSymbols.get(0))) {
                    Production production = parsingTable.get(top).get(inputSymbols.get(0));
                    String rightSide = production.getProductionRule();
                    // Apilar la parte derecha de la producción
                    for (int i = rightSide.length() - 1; i >= 0; i--) {
                        stack.push(String.valueOf(rightSide.charAt(i)));
                    }
                } else {
                    return false;  // Error de análisis (no hay entrada válida en la tabla)
                }
            }
        }

        // Si hemos consumido toda la entrada y la pila está vacía, la cadena es válida
        return inputSymbols.isEmpty();
    }

    // Método para obtener la tabla de análisis M
    public Map<String, Map<String, Production>> getParsingTable() {
        return parsingTable;
    }

}
