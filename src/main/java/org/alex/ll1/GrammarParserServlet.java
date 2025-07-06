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
        String productionsInput = request.getParameter("productions");
        if (productionsInput == null) {
            productionsInput = "";
        }
        String[] productions = productionsInput.split("\n");

        // parseGrammar ahora devuelve un LinkedHashMap para mantener el orden de los no-terminales.
        Map<String, List<String>> grammar = parseGrammar(productions);

        String startSymbol = grammar.keySet().stream().findFirst().orElse(null);

        Map<String, Set<String>> firstSets = calculateFirstSets(grammar);
        Map<String, Set<String>> followSets = calculateFollowSets(grammar, firstSets, startSymbol);
        Map<String, Map<String, String>> parsingTable = calculateParsingTable(grammar, firstSets, followSets);

        request.setAttribute("grammar", grammar);
        request.setAttribute("firstSets", firstSets);
        request.setAttribute("followSets", followSets);
        request.setAttribute("parsingTable", parsingTable);

        request.getRequestDispatcher("/result.jsp").forward(request, response);
    }

    private Map<String, List<String>> parseGrammar(String[] productions) {
        // CAMBIO: Usar LinkedHashMap para preservar el orden de inserción de las reglas.
        // El primer no-terminal insertado será el primero al iterar.
        Map<String, List<String>> grammar = new LinkedHashMap<>();

        for (String production : productions) {
            if (production.trim().isEmpty()) continue;

            String[] parts = production.split("->");
            if (parts.length != 2) continue;

            String nonTerminal = parts[0].trim();
            String[] alternatives = parts[1].split("\\|");

            grammar.putIfAbsent(nonTerminal, new ArrayList<>());

            for (String alternative : alternatives) {
                String trimmed = alternative.trim();
                // Normalizamos las representaciones de epsilon a 'ε'
                if (trimmed.isEmpty() || trimmed.equalsIgnoreCase("epsilon") || trimmed.equals("ε") || trimmed.equals("?")) {
                    grammar.get(nonTerminal).add("ε");
                } else {
                    grammar.get(nonTerminal).add(trimmed);
                }
            }
        }
        return grammar;
    }

    private Map<String, Set<String>> calculateFirstSets(Map<String, List<String>> grammar) {
        Map<String, Set<String>> firstSets = new LinkedHashMap<>();
        for (String nonTerminal : grammar.keySet()) {
            firstSets.put(nonTerminal, new HashSet<>());
        }

        boolean changed = true;
        while (changed) {
            changed = false;
            for (Map.Entry<String, List<String>> entry : grammar.entrySet()) {
                String nonTerminal = entry.getKey();
                List<String> productions = entry.getValue();
                Set<String> currentFirstSet = firstSets.get(nonTerminal);
                int originalSize = currentFirstSet.size();

                for (String production : productions) {
                    Set<String> firstOfProduction = calculateFirstOfProduction(production, grammar, firstSets);
                    currentFirstSet.addAll(firstOfProduction);
                }

                if (currentFirstSet.size() > originalSize) {
                    changed = true;
                }
            }
        }
        return firstSets;
    }

    private Map<String, Set<String>> calculateFollowSets(Map<String, List<String>> grammar, Map<String, Set<String>> firstSets, String startSymbol) {
        Map<String, Set<String>> followSets = new LinkedHashMap<>();
        for (String nonTerminal : grammar.keySet()) {
            followSets.put(nonTerminal, new HashSet<>());
        }

        if (startSymbol != null) {
            followSets.get(startSymbol).add("$");
        }

        boolean changed = true;
        while (changed) {
            changed = false;
            // Iterar sobre cada producción A -> α
            for (Map.Entry<String, List<String>> entry : grammar.entrySet()) {
                String nonTerminalA = entry.getKey();
                for (String production : entry.getValue()) {
                    if (production.equals("ε")) continue;
                    String[] symbols = production.split("\\s+");

                    // Para cada símbolo B en la producción A -> αBβ
                    for (int i = 0; i < symbols.length; i++) {
                        String symbolB = symbols[i];
                        if (!grammar.containsKey(symbolB)) continue;

                        Set<String> followB = followSets.get(symbolB);
                        int originalSize = followB.size();

                        // β es el resto de la cadena después de B
                        String betaString = String.join(" ", Arrays.copyOfRange(symbols, i + 1, symbols.length));
                        Set<String> firstOfBeta = calculateFirstOfProduction(betaString, grammar, firstSets);

                        // **** ESTA ES LA LÓGICA CLAVE ****
                        // Regla 2: Añadir FIRST(β) - {ε} a FOLLOW(B)
                        for(String s : firstOfBeta) {
                            if (!s.equals("ε")) {
                                followB.add(s);
                            }
                        }

                        // Regla 3: Si ε está en FIRST(β) (o β es vacío), añadir FOLLOW(A) a FOLLOW(B)
                        if (firstOfBeta.contains("ε") || betaString.isEmpty()) {
                            followB.addAll(followSets.get(nonTerminalA));
                        }
                        // **** FIN DE LA LÓGICA CLAVE ****

                        if (followB.size() > originalSize) {
                            changed = true;
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
        Map<String, Map<String, String>> parsingTable = new LinkedHashMap<>();
        for (String nonTerminal : grammar.keySet()) {
            parsingTable.put(nonTerminal, new LinkedHashMap<>());
        }

        for (Map.Entry<String, List<String>> entry : grammar.entrySet()) {
            String nonTerminalA = entry.getKey();
            List<String> productions = entry.getValue();

            for (String productionAlpha : productions) {
                Set<String> firstOfAlpha = calculateFirstOfProduction(productionAlpha, grammar, firstSets);

                for (String terminal : firstOfAlpha) {
                    if (!terminal.equals("ε")) {
                        if (parsingTable.get(nonTerminalA).containsKey(terminal)) {
                            System.err.println("Conflicto LL(1) en [" + nonTerminalA + ", " + terminal + "]. Gramática no es LL(1).");
                        }
                        parsingTable.get(nonTerminalA).put(terminal, productionAlpha);
                    }
                }

                if (firstOfAlpha.contains("ε")) {
                    for (String terminal : followSets.get(nonTerminalA)) {
                        if (parsingTable.get(nonTerminalA).containsKey(terminal)) {
                            System.err.println("Conflicto LL(1) en [" + nonTerminalA + ", " + terminal + "]. Gramática no es LL(1).");
                        }
                        parsingTable.get(nonTerminalA).put(terminal, productionAlpha);
                    }
                }
            }
        }
        return parsingTable;
    }

    private Set<String> calculateFirstOfProduction(String production, Map<String, List<String>> grammar,
                                                   Map<String, Set<String>> firstSets) {
        Set<String> result = new HashSet<>();

        if (production == null || production.trim().isEmpty()){
            result.add("ε");
            return result;
        }

        if (production.equals("ε")) {
            result.add("ε");
            return result;
        }

        String[] symbols = production.split("\\s+");
        boolean allPreviousCanBeEpsilon = true;

        for (String symbol : symbols) {
            if (!allPreviousCanBeEpsilon) break;

            if (!grammar.containsKey(symbol)) {
                result.add(symbol);
                allPreviousCanBeEpsilon = false;
            } else {
                Set<String> firstOfSymbol = firstSets.get(symbol);
                boolean hasEpsilon = false;
                for (String first : firstOfSymbol) {
                    if (first.equals("ε")) {
                        hasEpsilon = true;
                    } else {
                        result.add(first);
                    }
                }
                if (!hasEpsilon) {
                    allPreviousCanBeEpsilon = false;
                }
            }
        }

        if (allPreviousCanBeEpsilon) {
            result.add("ε");
        }

        return result;
    }
}