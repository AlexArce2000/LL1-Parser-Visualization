package org.alex.ll1;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;


@WebServlet("/parseGrammar")
public class GrammarParserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Obtener las producciones del formulario
        String productionsInput = request.getParameter("productions");
        String[] productions = productionsInput.split("\n");

        // Parsear la gramática
        Map<String, List<String>> grammar = parseGrammar(productions);

        // Calcular First y Follow
        Map<String, Set<String>> firstSets = calculateFirstSets(grammar);
        Map<String, Set<String>> followSets = calculateFollowSets(grammar, firstSets);

        // Calcular la tabla de análisis LL(1)
        Map<String, Map<String, String>> parsingTable = calculateParsingTable(grammar, firstSets, followSets);

        // Establecer los resultados como atributos de la solicitud
        request.setAttribute("grammar", grammar);
        request.setAttribute("firstSets", firstSets);
        request.setAttribute("followSets", followSets);
        request.setAttribute("parsingTable", parsingTable);

        // Redirigir a la página de resultados
        request.getRequestDispatcher("/result.jsp").forward(request, response);
    }
    private Map<String, Set<String>> calculateFollowSets(Map<String, List<String>> grammar, Map<String, Set<String>> firstSets) {
        Map<String, Set<String>> followSets = new HashMap<>();

        // Inicializar Follow para todos los no terminales
        for (String nonTerminal : grammar.keySet()) {
            followSets.put(nonTerminal, new HashSet<>());
        }

        // El símbolo inicial tiene $ en su Follow
        String startSymbol = grammar.keySet().iterator().next();
        followSets.get(startSymbol).add("$");

        // Calcular Follow recursivamente
        boolean changed;
        do {
            changed = false;
            for (Map.Entry<String, List<String>> entry : grammar.entrySet()) {
                String nonTerminal = entry.getKey();
                for (String production : entry.getValue()) {
                    String[] symbols = production.split("\\s+");
                    for (int i = 0; i < symbols.length; i++) {
                        if (grammar.containsKey(symbols[i])) {
                            // Calcular Follow para el no terminal symbols[i]
                            Set<String> firstOfNext = new HashSet<>();
                            if (i < symbols.length - 1) {
                                // Calcular First de los símbolos siguientes
                                for (int j = i + 1; j < symbols.length; j++) {
                                    if (grammar.containsKey(symbols[j])) {
                                        firstOfNext.addAll(firstSets.get(symbols[j]));
                                        if (!firstSets.get(symbols[j]).contains("ε")) {
                                            break;
                                        }
                                    } else {
                                        firstOfNext.add(symbols[j]);
                                        break;
                                    }
                                }
                            }

                            // Agregar First de los siguientes símbolos al Follow actual
                            if (followSets.get(symbols[i]).addAll(firstOfNext)) {
                                changed = true;
                            }

                            // Si hay un ε en First de los siguientes símbolos, agregar Follow del no terminal actual
                            if (i == symbols.length - 1 || firstOfNext.contains("ε")) {
                                if (followSets.get(symbols[i]).addAll(followSets.get(nonTerminal))) {
                                    changed = true;
                                }
                            }
                        }
                    }
                }
            }
        } while (changed);

        return followSets;
    }
    private Map<String, List<String>> parseGrammar(String[] productions) {
        Map<String, List<String>> grammar = new HashMap<>();
        for (String production : productions) {
            String[] parts = production.split("->");
            String nonTerminal = parts[0].trim();
            String[] rhs = parts[1].trim().split("\\|");
            grammar.putIfAbsent(nonTerminal, new ArrayList<>());
            for (String rule : rhs) {
                grammar.get(nonTerminal).add(rule.trim());
            }
        }
        return grammar;
    }
    private Map<String, Set<String>> calculateFirstSets(Map<String, List<String>> grammar) {
        Map<String, Set<String>> firstSets = new HashMap<>();

        // Inicializar First para todos los no terminales
        for (String nonTerminal : grammar.keySet()) {
            firstSets.put(nonTerminal, new HashSet<>());
        }

        // Calcular First recursivamente
        boolean changed;
        do {
            changed = false;
            for (Map.Entry<String, List<String>> entry : grammar.entrySet()) {
                String nonTerminal = entry.getKey();
                for (String production : entry.getValue()) {
                    Set<String> firstOfProduction = calculateFirstOfProduction(production, grammar, firstSets);
                    if (firstSets.get(nonTerminal).addAll(firstOfProduction)) {
                        changed = true;
                    }
                }
            }
        } while (changed);

        return firstSets;
    }

    private Set<String> calculateFirstOfProduction(String production, Map<String, List<String>> grammar, Map<String, Set<String>> firstSets) {
        Set<String> first = new HashSet<>();
        String[] symbols = production.split("\\s+");

        for (String symbol : symbols) {
            if (grammar.containsKey(symbol)) {
                // Es un no terminal
                first.addAll(firstSets.get(symbol));
                if (!firstSets.get(symbol).contains("ε")) {
                    break;
                }
            } else {
                // Es un terminal
                first.add(symbol);
                break;
            }
        }

        return first;
    }


    private Set<String> calculateFirst(String symbol, Map<String, List<String>> grammar, Map<String, Set<String>> firstSets) {
        if (firstSets.containsKey(symbol)) {
            return firstSets.get(symbol);
        }

        Set<String> first = new HashSet<>();
        if (grammar.containsKey(symbol)) {
            for (String production : grammar.get(symbol)) {
                String[] symbols = production.split("\\s+");
                for (String s : symbols) {
                    Set<String> firstOfS = calculateFirst(s, grammar, firstSets);
                    first.addAll(firstOfS);
                    if (!firstOfS.contains("ε")) {
                        break;
                    }
                }
            }
        } else {
            first.add(symbol); // Es un terminal
        }

        firstSets.put(symbol, first);
        return first;
    }


    private void calculateFollow(String symbol, Map<String, List<String>> grammar, Map<String, Set<String>> firstSets, Map<String, Set<String>> followSets) {
        for (Map.Entry<String, List<String>> entry : grammar.entrySet()) {
            String nonTerminal = entry.getKey();
            for (String production : entry.getValue()) {
                String[] symbols = production.split("\\s+");
                for (int i = 0; i < symbols.length; i++) {
                    if (symbols[i].equals(symbol)) {
                        if (i < symbols.length - 1) {
                            String nextSymbol = symbols[i + 1];
                            Set<String> firstOfNext = calculateFirst(nextSymbol, grammar, firstSets);
                            followSets.computeIfAbsent(symbol, k -> new HashSet<>()).addAll(firstOfNext);
                            if (firstOfNext.contains("ε")) {
                                followSets.computeIfAbsent(symbol, k -> new HashSet<>()).addAll(followSets.getOrDefault(nonTerminal, new HashSet<>()));
                            }
                        } else {
                            followSets.computeIfAbsent(symbol, k -> new HashSet<>()).addAll(followSets.getOrDefault(nonTerminal, new HashSet<>()));
                        }
                    }
                }
            }
        }
    }

    private Map<String, Map<String, String>> calculateParsingTable(Map<String, List<String>> grammar, Map<String, Set<String>> firstSets, Map<String, Set<String>> followSets) {
        Map<String, Map<String, String>> parsingTable = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : grammar.entrySet()) {
            String nonTerminal = entry.getKey();
            parsingTable.put(nonTerminal, new HashMap<>());

            for (String production : entry.getValue()) {
                Set<String> firstOfProduction = calculateFirstOfProduction(production, grammar, firstSets);
                for (String terminal : firstOfProduction) {
                    if (!terminal.equals("ε")) {
                        parsingTable.get(nonTerminal).put(terminal, production);
                    }
                }
                if (firstOfProduction.contains("ε")) {
                    for (String terminal : followSets.get(nonTerminal)) {
                        parsingTable.get(nonTerminal).put(terminal, production);
                    }
                }
            }
        }

        return parsingTable;
    }

}