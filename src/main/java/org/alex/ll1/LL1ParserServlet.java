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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Recuperar los datos del formulario
        String startSymbol = request.getParameter("startSymbol");
        String productionsInput = request.getParameter("productions");

        // Parsear las producciones
        String[] productionsArray = productionsInput.split("\\n");

        // Crear una lista para las producciones
        List<Production> productions = new ArrayList<>();
        for (String production : productionsArray) {
            String[] parts = production.split("->");
            if (parts.length == 2) {
                String leftSide = parts[0].trim();
                String rightSide = parts[1].trim();
                productions.add(new Production(leftSide, rightSide));
            }
        }

        // Crear la gramática
        Grammar grammar = new Grammar();
        grammar.setStartSymbol(startSymbol);
        grammar.setProductions(productions);

        // Calcular los conjuntos FIRST y FOLLOW
        FirstFollowCalcular calculator = new FirstFollowCalcular();
        Map<String, Set<String>> firstSets = calculator.calculateFirst(grammar);
        Map<String, Set<String>> followSets = calculator.calculateFollow(grammar, firstSets);

        // Crear el parser LL(1)
        LL1Parser parser = new LL1Parser(grammar, firstSets, followSets);

        // Analizar una cadena de ejemplo (o recibirla también como parámetro si lo prefieres)
        String inputString = request.getParameter("inputString"); // Cadena a analizar
        if (inputString == null || inputString.isEmpty()) {
            inputString = "T + T * T"; // Valor por defecto
        }

        boolean result = parser.parseInputString(inputString);

        // Establecer los resultados en el contexto de la solicitud
        request.setAttribute("firstSets", firstSets);
        request.setAttribute("followSets", followSets);
        request.setAttribute("parsingTable", parser.getParsingTable());
        request.setAttribute("result", result ? "válida" : "inválida");
        request.setAttribute("inputString", inputString);

        // Redirigir a la página de resultados
        RequestDispatcher dispatcher = request.getRequestDispatcher("/parse.jsp");
        dispatcher.forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Redirigir a la página de inicio si el método es GET
        response.sendRedirect("index.jsp");
    }
}
