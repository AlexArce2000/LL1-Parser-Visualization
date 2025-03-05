package org.alex.ll1;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import java.io.*;
import java.util.*;

@WebServlet("/parseGrammar")
public class LL1ParserServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Obtener las producciones del formulario
        String productionsInput = request.getParameter("productions");
        String[] productions = productionsInput.split("\n");

        // Procesar la gramática
        Map<String, Set<String>> firstSets = calculateFirstSets(productions);
        Map<String, Set<String>> followSets = calculateFollowSets(productions, firstSets);
        Map<String, Map<String, String>> parsingTable = createParsingTable(productions, firstSets, followSets);

        // Enviar los resultados al cliente
        request.setAttribute("firstSets", firstSets);
        request.setAttribute("followSets", followSets);
        request.setAttribute("parsingTable", parsingTable);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/result.jsp");
        dispatcher.forward(request, response);
    }

    // FIRST
    private Map<String, Set<String>> calculateFirstSets(String[] productions) {
        Map<String, Set<String>> firstSets = new HashMap<>();

        // Inicialización de FIRST para cada no terminal
        for (String production : productions) {
            String left = production.split("->")[0].trim();
            if (!firstSets.containsKey(left)) {
                firstSets.put(left, new HashSet<>());
            }
        }

        boolean changed = true;
        while (changed) {
            changed = false;
            for (String production : productions) {
                String[] parts = production.split("->");
                String left = parts[0].trim();
                String right = parts[1].trim();

                Set<String> currentFirstSet = firstSets.get(left);

                // Si la producción es de la forma X -> a, agregar a la producción
                if (Character.isLowerCase(right.charAt(0))) {
                    if (currentFirstSet.add(String.valueOf(right.charAt(0)))) {
                        changed = true;
                    }
                } else {
                    // Si es no terminal, se procesan recursivamente
                    for (char c : right.toCharArray()) {
                        if (Character.isLowerCase(c)) {
                            if (currentFirstSet.add(String.valueOf(c))) {
                                changed = true;
                            }
                            break;
                        } else {
                            Set<String> firstOfNonTerminal = firstSets.get(String.valueOf(c));
                            if (firstOfNonTerminal != null) {
                                if (currentFirstSet.addAll(firstOfNonTerminal)) {
                                    changed = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return firstSets;
    }


    // FOLLOW
    private Map<String, Set<String>> calculateFollowSets(String[] productions, Map<String, Set<String>> firstSets) {
        Map<String, Set<String>> followSets = new HashMap<>();
        // Inicialización de FOLLOW para cada no terminal
        for (String production : productions) {
            String left = production.split("->")[0].trim();
            if (!followSets.containsKey(left)) {
                followSets.put(left, new HashSet<>());
            }
        }

        // El símbolo de inicio sigue al símbolo de fin de cadena
        followSets.get(productions[0].split("->")[0].trim()).add("$");

        boolean changed = true;
        while (changed) {
            changed = false;
            for (String production : productions) {
                String[] parts = production.split("->");
                String left = parts[0].trim();
                String right = parts[1].trim();

                for (int i = 0; i < right.length(); i++) {
                    char currentChar = right.charAt(i);
                    if (Character.isUpperCase(currentChar)) { // Si es un no terminal
                        String nonTerminal = String.valueOf(currentChar);
                        if (i + 1 < right.length()) {
                            String nextSymbol = String.valueOf(right.charAt(i + 1));
                            if (Character.isLowerCase(nextSymbol.charAt(0))) {
                                if (followSets.get(nonTerminal).add(nextSymbol)) {
                                    changed = true;
                                }
                            } else {
                                Set<String> firstOfNextSymbol = firstSets.get(nextSymbol);
                                if (firstOfNextSymbol != null) {
                                    if (followSets.get(nonTerminal).addAll(firstOfNextSymbol)) {
                                        changed = true;
                                    }
                                }
                            }
                        } else {
                            if (followSets.get(nonTerminal).addAll(followSets.get(left))) {
                                changed = true;
                            }
                        }
                    }
                }
            }
        }
        return followSets;
    }


    // Méodo para crear la tabla de análisis M
    private Map<String, Map<String, String>> createParsingTable(String[] productions,
                                                                Map<String, Set<String>> firstSets, Map<String, Set<String>> followSets) {

        Map<String, Map<String, String>> parsingTable = new HashMap<>();
        for (String production : productions) {
            String[] parts = production.split("->");
            String left = parts[0].trim();
            String right = parts[1].trim();

            // Procesar FIRST
            Set<String> firstSet = firstSets.get(left);
            for (String terminal : firstSet) {
                if (!terminal.equals("ε")) {
                    parsingTable.computeIfAbsent(left, k -> new HashMap<>()).put(terminal, production);
                }
            }

            // Si ε está en FIRST, procesar FOLLOW
            if (firstSet.contains("ε")) {
                Set<String> followSet = followSets.get(left);
                for (String terminal : followSet) {
                    parsingTable.computeIfAbsent(left, k -> new HashMap<>()).put(terminal, production);
                }
            }
        }
        return parsingTable;
    }

}
