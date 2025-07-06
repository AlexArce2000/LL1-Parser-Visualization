<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Análisis LL(1) - Resultados</title>
    <link rel="icon" href="assets/LL1.jpg" type="image/jpeg">
    <link rel="stylesheet" href="css/styles_results.css">
</head>
<body>

<%
    Map<String, List<String>> grammar = (Map<String, List<String>>) request.getAttribute("grammar");
    Map<String, Set<String>> firstSets = (Map<String, Set<String>>) request.getAttribute("firstSets");
    Map<String, Set<String>> followSets = (Map<String, Set<String>>) request.getAttribute("followSets");
    Map<String, Map<String, String>> parsingTable = (Map<String, Map<String, String>>) request.getAttribute("parsingTable");
%>

<h1>Resultados del Análisis LL(1)</h1>

<div class="main-container">

    <div class="left-column">
        <div class="card">
            <h2>Gramática</h2>
            <ul class="production-list">
                <%
                    for (Map.Entry<String, List<String>> entry : grammar.entrySet()) {
                        String nonTerminal = entry.getKey();
                        String productions = String.join(" | ", entry.getValue());
                %>
                <li>
                    <strong><%= nonTerminal %></strong> → <span class="code"><%= productions.replace("ε", " ε ") %></span>
                </li>
                <%
                    }
                %>
            </ul>
        </div>

        <div class="card">
            <h2>Conjuntos First y Follow</h2>
            <table>
                <thead>
                <tr>
                    <th>No Terminal</th>
                    <th>First</th>
                    <th>Follow</th>
                </tr>
                </thead>
                <tbody>
                <%
                    for (String nonTerminal : grammar.keySet()) {
                        Set<String> firstSet = firstSets.get(nonTerminal);
                        Set<String> followSet = followSets.get(nonTerminal);

                        SortedSet<String> sortedFirst = new TreeSet<>(firstSet);
                        SortedSet<String> sortedFollow = new TreeSet<>(followSet);
                %>
                <tr>
                    <td><strong><%= nonTerminal %></strong></td>
                    <td>{ <span class="code"><%= String.join(", ", sortedFirst) %></span> }</td>
                    <td>{ <span class="code"><%= String.join(", ", sortedFollow) %></span> }</td>
                </tr>
                <%
                    }
                %>
                </tbody>
            </table>
        </div>
    </div>

    <div class="right-column">
        <div class="card">
            <h2>Tabla de Análisis LL(1)</h2>
            <%
                Set<String> terminals = new TreeSet<>();
                for (Map<String, String> row : parsingTable.values()) {
                    terminals.addAll(row.keySet());
                }
            %>
            <table>
                <thead>
                <tr>
                    <th>No Terminal</th>
                    <% for (String terminal : terminals) { %>
                    <th><%= terminal %></th>
                    <% } %>
                </tr>
                </thead>
                <tbody>
                <%
                    for (Map.Entry<String, Map<String, String>> entry : parsingTable.entrySet()) {
                        String nonTerminal = entry.getKey();
                        Map<String, String> row = entry.getValue();
                %>
                <tr>
                    <td><strong><%= nonTerminal %></strong></td>
                    <%
                        for (String terminal : terminals) {
                            String production = row.get(terminal);
                            if (production != null) {
                    %>
                    <td><span class="code"><%= nonTerminal %> → <%= production %></span></td>
                    <%
                    } else {
                    %>
                    <td class="empty-cell"></td>
                    <%
                            }
                        }
                    %>
                </tr>
                <%
                    }
                %>
                </tbody>
            </table>
        </div>
    </div>
</div>

</body>
</html>