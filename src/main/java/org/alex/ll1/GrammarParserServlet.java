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
        Map<String, Set<String>> followSets = new LinkedHashMap<>();

        // Inicializar los conjuntos Follow para todos los no terminales
        for (String nonTerminal : grammar.keySet()) {
            followSets.put(nonTerminal, new HashSet<>());
        }

        // Obtener el símbolo inicial (el primer no terminal en el mapa)
        String startSymbol = grammar.keySet().iterator().next();

        // Agregar $ al conjunto Follow del símbolo inicial
        followSets.get(startSymbol).add("$");

        // Calcular Follow iterativamente hasta que no haya cambios
        boolean changed;
        do {
            changed = false;

            // Para cada no terminal en la gramática
            for (Map.Entry<String, List<String>> entry : grammar.entrySet()) {
                String nonTerminal = entry.getKey();
                List<String> productions = entry.getValue();

                // Para cada producción del no terminal
                for (String production : productions) {
                    String[] symbols = production.split("\\s+");

                    // Para cada símbolo en la producción
                    for (int i = 0; i < symbols.length; i++) {
                        // Si el símbolo actual es un no terminal
                        if (grammar.containsKey(symbols[i])) {
                            // Si es el último símbolo de la producción
                            if (i == symbols.length - 1) {
                                // Agregar Follow de nonTerminal a Follow de symbols[i]
                                if (followSets.get(symbols[i]).addAll(followSets.get(nonTerminal))) {
                                    changed = true;
                                }
                            } else {
                                // Hay más símbolos después del actual
                                String nextSymbol = symbols[i + 1];

                                // Si el siguiente símbolo es un no terminal
                                if (grammar.containsKey(nextSymbol)) {
                                    // Agregar First de nextSymbol a Follow de symbols[i]
                                    Set<String> firstOfNext = new HashSet<>(firstSets.get(nextSymbol));

                                    // Si First del siguiente contiene epsilon (ε)
                                    if (firstOfNext.remove("ε")) {
                                        // Agregar Follow del no terminal de la producción a Follow del símbolo actual
                                        if (followSets.get(symbols[i]).addAll(followSets.get(nonTerminal))) {
                                            changed = true;
                                        }
                                    }

                                    // Agregar First del siguiente símbolo (sin epsilon) a Follow del símbolo actual
                                    if (followSets.get(symbols[i]).addAll(firstOfNext)) {
                                        changed = true;
                                    }
                                } else {
                                    // El siguiente símbolo es un terminal, agregarlo a Follow del símbolo actual
                                    if (followSets.get(symbols[i]).add(nextSymbol)) {
                                        changed = true;
                                    }
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
        // Usar LinkedHashMap para mantener el orden de inserción
        Map<String, List<String>> grammar = new LinkedHashMap<>();

        for (String production : productions) {
            String[] parts = production.split("->");
            String nonTerminal = parts[0].trim(); // No terminal de la producción
            String[] rhs = parts[1].trim().split("\\|"); // Partes derechas de la producción

            // Inicializar la lista de producciones para el no terminal si no existe
            grammar.putIfAbsent(nonTerminal, new ArrayList<>());

            // Agregar cada regla a la lista de producciones del no terminal
            for (String rule : rhs) {
                grammar.get(nonTerminal).add(rule.trim());
            }
        }

        return grammar;
    }
    private Map<String, Set<String>> calculateFirstSets(Map<String, List<String>> grammar) {
        Map<String, Set<String>> firstSets = new LinkedHashMap<>();

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


    private Map<String, Map<String, String>> calculateParsingTable(Map<String, List<String>> grammar, Map<String, Set<String>> firstSets, Map<String, Set<String>> followSets) {
        // Usar LinkedHashMap para mantener el orden de inserción
        Map<String, Map<String, String>> parsingTable = new LinkedHashMap<>();

        for (Map.Entry<String, List<String>> entry : grammar.entrySet()) {
            String nonTerminal = entry.getKey();
            // Usar LinkedHashMap para las entradas internas
            parsingTable.put(nonTerminal, new LinkedHashMap<>());

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