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

    // Método para calcular el conjunto FIRST
    private Map<String, Set<String>> calculateFirstSets(String[] productions) {
        Map<String, Set<String>> firstSets = new HashMap<>();
        // Procesar la gramática y calcular el conjunto FIRST
        // Este es un ejemplo simplificado, debes manejar todas las reglas correctamente
        for (String production : productions) {
            String[] parts = production.split("->");
            String left = parts[0].trim();
            String right = parts[1].trim();

            Set<String> firstSet = firstSets.computeIfAbsent(left, k -> new HashSet<>());
            // Aquí se necesita lógica adicional para analizar las producciones y calcular FIRST
            // Esto es solo un esquema, se debe completar adecuadamente
        }
        return firstSets;
    }

    // Método para calcular el conjunto FOLLOW
    private Map<String, Set<String>> calculateFollowSets(String[] productions, Map<String, Set<String>> firstSets) {
        Map<String, Set<String>> followSets = new HashMap<>();
        // Lógica para calcular el conjunto FOLLOW
        // Esto es solo un esquema
        return followSets;
    }

    // Méodo para crear la tabla de análisis M
    private Map<String, Map<String, String>> createParsingTable(String[] productions,
                                                                Map<String, Set<String>> firstSets, Map<String, Set<String>> followSets) {

        Map<String, Map<String, String>> parsingTable = new HashMap<>();
        // Lógica para crear la tabla M basándose en las producciones, FIRST y FOLLOW
        return parsingTable;
    }
}
