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
    private Map<String, List<String>> parseGrammar(String[] productions) {
        Map<String, List<String>> grammar = new LinkedHashMap<>();

        for (String production : productions) {
            if (production.trim().isEmpty()) continue;

            String[] parts = production.split("->");
            if (parts.length != 2) continue;

            String nonTerminal = parts[0].trim();
            String[] alternatives = parts[1].split("\\|");

            List<String> productionRules = new ArrayList<>();
            for (String alternative : alternatives) {
                String trimmed = alternative.trim();
                // Manejar explícitamente el caso de producciones vacías
                if (trimmed.isEmpty()) {
                    productionRules.add("ε");
                } else {
                    productionRules.add(trimmed);
                }
            }

            grammar.put(nonTerminal, productionRules);
        }

        return grammar;
    }

    private Map<String, Set<String>> calculateFirstSets(Map<String, List<String>> grammar) {
        Map<String, Set<String>> firstSets = new HashMap<>();

        // Inicializar conjuntos FIRST para todos los no terminales
        for (String nonTerminal : grammar.keySet()) {
            firstSets.put(nonTerminal, new HashSet<>());
        }

        boolean changed = true;
        while (changed) {
            changed = false;

            for (Map.Entry<String, List<String>> entry : grammar.entrySet()) {
                String nonTerminal = entry.getKey();
                List<String> productions = entry.getValue();

                for (String production : productions) {
                    // Caso especial para ε
                    if (production.equals("ε")) {
                        if (firstSets.get(nonTerminal).add("ε")) {
                            changed = true;
                        }
                        continue;
                    }

                    String[] symbols = production.split("\\s+");
                    boolean allDeriveEmpty = true;

                    for (int i = 0; i < symbols.length; i++) {
                        String symbol = symbols[i];

                        if (!grammar.containsKey(symbol)) {
                            // Terminal - añadirlo al conjunto FIRST
                            if (firstSets.get(nonTerminal).add(symbol)) {
                                changed = true;
                            }
                            allDeriveEmpty = false;
                            break;
                        } else {
                            // No terminal - añadir todos sus FIRST excepto ε
                            boolean hasEpsilon = false;
                            for (String first : firstSets.get(symbol)) {
                                if (first.equals("ε")) {
                                    hasEpsilon = true;
                                } else if (firstSets.get(nonTerminal).add(first)) {
                                    changed = true;
                                }
                            }

                            // Si este símbolo no puede derivar ε, no continuar
                            if (!hasEpsilon) {
                                allDeriveEmpty = false;
                                break;
                            }
                        }
                    }

                    // Si todos los símbolos pueden derivar ε, añadir ε al conjunto FIRST
                    if (allDeriveEmpty && symbols.length > 0) {
                        if (firstSets.get(nonTerminal).add("ε")) {
                            changed = true;
                        }
                    }
                }
            }
        }

        return firstSets;
    }

    private Map<String, Set<String>> calculateFollowSets(Map<String, List<String>> grammar, Map<String, Set<String>> firstSets) {
        Map<String, Set<String>> followSets = new HashMap<>();

        // Inicializar conjuntos FOLLOW para todos los no terminales
        for (String nonTerminal : grammar.keySet()) {
            followSets.put(nonTerminal, new HashSet<>());
        }

        // Añadir $ al FOLLOW del símbolo inicial (primera producción)
        String startSymbol = grammar.keySet().iterator().next();
        followSets.get(startSymbol).add("$");

        boolean changed = true;
        while (changed) {
            changed = false;

            for (Map.Entry<String, List<String>> entry : grammar.entrySet()) {
                String nonTerminal = entry.getKey();
                List<String> productions = entry.getValue();

                for (String production : productions) {
                    if (production.equals("ε")) continue; // Ignorar producciones ε

                    String[] symbols = production.split("\\s+");

                    for (int i = 0; i < symbols.length; i++) {
                        String symbol = symbols[i];

                        // Solo nos interesan los no terminales para FOLLOW
                        if (!grammar.containsKey(symbol)) continue;

                        // Calcular los símbolos que siguen a este no terminal
                        if (i < symbols.length - 1) {
                            // Hay símbolos después de este
                            int j = i + 1;
                            boolean canBeEmpty = true;

                            while (j < symbols.length && canBeEmpty) {
                                String nextSymbol = symbols[j];

                                if (!grammar.containsKey(nextSymbol)) {
                                    // Si es un terminal, añadirlo al FOLLOW
                                    if (followSets.get(symbol).add(nextSymbol)) {
                                        changed = true;
                                    }
                                    canBeEmpty = false;
                                } else {
                                    // Si es un no terminal, añadir sus FIRST excepto ε
                                    boolean hasEpsilon = false;
                                    for (String first : firstSets.get(nextSymbol)) {
                                        if (first.equals("ε")) {
                                            hasEpsilon = true;
                                        } else if (followSets.get(symbol).add(first)) {
                                            changed = true;
                                        }
                                    }

                                    if (!hasEpsilon) {
                                        canBeEmpty = false;
                                    }
                                }
                                j++;
                            }

                            // Si todos los símbolos posteriores pueden derivar ε, añadir FOLLOW(nonTerminal)
                            if (canBeEmpty) {
                                if (followSets.get(symbol).addAll(followSets.get(nonTerminal))) {
                                    changed = true;
                                }
                            }
                        } else {
                            // Es el último símbolo, añadir FOLLOW(nonTerminal)
                            if (followSets.get(symbol).addAll(followSets.get(nonTerminal))) {
                                changed = true;
                            }
                        }
                    }
                }
            }
        }

        return followSets;
    }

    private Map<String, Map<String, String>> calculateParsingTable(Map<String, List<String>> grammar,
                                                                   Map<String, Set<String>> firstSets,
                                                                   Map<String, Set<String>> followSets) {
        Map<String, Map<String, String>> parsingTable = new HashMap<>();

        // Inicializar la tabla de análisis
        for (String nonTerminal : grammar.keySet()) {
            parsingTable.put(nonTerminal, new HashMap<>());
        }

        // Llenar la tabla de análisis
        for (Map.Entry<String, List<String>> entry : grammar.entrySet()) {
            String nonTerminal = entry.getKey();
            List<String> productions = entry.getValue();

            for (int i = 0; i < productions.size(); i++) {
                String production = productions.get(i);

                if (production.equals("ε")) {
                    // Regla 2: Si A -> ε es una producción, entonces para cada terminal a en FOLLOW(A),
                    // añadir A -> ε a la tabla M[A,a]
                    for (String terminal : followSets.get(nonTerminal)) {
                        String existingProduction = parsingTable.get(nonTerminal).get(terminal);
                        if (existingProduction != null && !existingProduction.equals("ε")) {
                            System.out.println("Conflicto en la tabla LL(1): " + nonTerminal + ", " + terminal);
                        }
                        parsingTable.get(nonTerminal).put(terminal, "ε");
                    }
                } else {
                    // Regla 1: Si A -> α es una producción y a está en FIRST(α), añadir A -> α a la tabla M[A,a]
                    Set<String> firstOfProduction = calculateFirstOfProduction(production, grammar, firstSets);

                    for (String terminal : firstOfProduction) {
                        if (!terminal.equals("ε")) {
                            String existingProduction = parsingTable.get(nonTerminal).get(terminal);
                            if (existingProduction != null && !existingProduction.equals(production)) {
                                System.out.println("Conflicto en la tabla LL(1): " + nonTerminal + ", " + terminal);
                            }
                            parsingTable.get(nonTerminal).put(terminal, production);
                        } else {
                            // Si ε está en FIRST(α), entonces para cada b en FOLLOW(A), añadir A -> α a M[A,b]
                            for (String followTerminal : followSets.get(nonTerminal)) {
                                String existingProduction = parsingTable.get(nonTerminal).get(followTerminal);
                                if (existingProduction != null && !existingProduction.equals(production)) {
                                    System.out.println("Conflicto en la tabla LL(1): " + nonTerminal + ", " + followTerminal);
                                }
                                parsingTable.get(nonTerminal).put(followTerminal, production);
                            }
                        }
                    }
                }
            }
        }

        return parsingTable;
    }

    private Set<String> calculateFirstOfProduction(String production, Map<String, List<String>> grammar,
                                                   Map<String, Set<String>> firstSets) {
        Set<String> result = new HashSet<>();

        if (production.equals("ε")) {
            result.add("ε");
            return result;
        }

        String[] symbols = production.split("\\s+");
        boolean allDeriveEmpty = true;

        for (int i = 0; i < symbols.length; i++) {
            String symbol = symbols[i];

            if (!grammar.containsKey(symbol)) {
                // Si es un terminal, solo añadir el terminal y terminar
                result.add(symbol);
                allDeriveEmpty = false;
                break;
            } else {
                // Si es un no terminal, añadir todos sus FIRST excepto ε
                boolean hasEpsilon = false;
                for (String first : firstSets.get(symbol)) {
                    if (first.equals("ε")) {
                        hasEpsilon = true;
                    } else {
                        result.add(first);
                    }
                }

                // Si este símbolo no puede derivar ε, no continuar
                if (!hasEpsilon) {
                    allDeriveEmpty = false;
                    break;
                }
            }
        }

        // Si todos los símbolos pueden derivar ε, añadir ε al resultado
        if (allDeriveEmpty) {
            result.add("ε");
        }

        return result;
    }

}