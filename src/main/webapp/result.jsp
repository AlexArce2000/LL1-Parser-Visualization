<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Análisis LL(1) - Resultados</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
        }
        h1, h2 {
            color: #333;
        }
        table {
            border-collapse: collapse;
            margin-bottom: 20px;
            width: 100%;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
        }
        th {
            background-color: #f2f2f2;
        }
        .container {
            margin-bottom: 30px;
        }
        .production {
            margin-bottom: 5px;
        }
        .set {
            margin-bottom: 5px;
        }
    </style>
</head>
<body>
<h1>Resultados del Análisis LL(1)</h1>

<div class="container">
    <h2>Gramática</h2>
    <%
        Map<String, List<String>> grammar = (Map<String, List<String>>)request.getAttribute("grammar");
        for(Map.Entry<String, List<String>> entry : grammar.entrySet()) {
            String nonTerminal = entry.getKey();
            List<String> productions = entry.getValue();
    %>
    <div class="production">
        <strong><%= nonTerminal %></strong> -> <%= String.join(" | ", productions) %>
    </div>
    <%
        }
    %>
</div>

<div class="container">
    <h2>Conjuntos First</h2>
    <%
        Map<String, Set<String>> firstSets = (Map<String, Set<String>>)request.getAttribute("firstSets");
        for(Map.Entry<String, Set<String>> entry : firstSets.entrySet()) {
            String nonTerminal = entry.getKey();
            Set<String> firstSet = entry.getValue();
    %>
    <div class="set">
        <strong>First(<%= nonTerminal %>)</strong> = { <%= String.join(", ", firstSet) %> }
    </div>
    <%
        }
    %>
</div>

<div class="container">
    <h2>Conjuntos Follow</h2>
    <%
        Map<String, Set<String>> followSets = (Map<String, Set<String>>)request.getAttribute("followSets");
        for(Map.Entry<String, Set<String>> entry : followSets.entrySet()) {
            String nonTerminal = entry.getKey();
            Set<String> followSet = entry.getValue();
    %>
    <div class="set">
        <strong>Follow(<%= nonTerminal %>)</strong> = { <%= String.join(", ", followSet) %> }
    </div>
    <%
        }
    %>
</div>

<div class="container">
    <h2>Tabla de Análisis LL(1)</h2>
    <table>
        <tr>
            <th>No Terminal</th>
            <%
                // Obtener todos los terminales únicos de la tabla de análisis
                Set<String> terminals = new TreeSet<>();
                Map<String, Map<String, String>> parsingTable =
                        (Map<String, Map<String, String>>)request.getAttribute("parsingTable");

                for(Map<String, String> row : parsingTable.values()) {
                    terminals.addAll(row.keySet());
                }

                for(String terminal : terminals) {
            %>
            <th><%= terminal %></th>
            <%
                }
            %>
        </tr>
        <%
            for(Map.Entry<String, Map<String, String>> entry : parsingTable.entrySet()) {
                String nonTerminal = entry.getKey();
                Map<String, String> row = entry.getValue();
        %>
        <tr>
            <td><strong><%= nonTerminal %></strong></td>
            <%
                for(String terminal : terminals) {
                    String production = row.get(terminal);
                    if(production != null) {
            %>
            <td><%= nonTerminal %> -> <%= production %></td>
            <%
            } else {
            %>
            <td></td>
            <%
                    }
                }
            %>
        </tr>
        <%
            }
        %>
    </table>
</div>
</body>
</html>