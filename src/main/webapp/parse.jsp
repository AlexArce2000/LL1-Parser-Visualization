<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*" %>
<%@ page import="org.alex.ll1.Production" %>
<%
    // Recuperar los resultados de la solicitud
    Map<String, Set<String>> firstSets = (Map<String, Set<String>>) request.getAttribute("firstSets");
    Map<String, Set<String>> followSets = (Map<String, Set<String>>) request.getAttribute("followSets");
    Map<String, Map<String, Production>> parsingTable = (Map<String, Map<String, Production>>) request.getAttribute("parsingTable");
    String result = (String) request.getAttribute("result");
    String inputString = (String) request.getAttribute("inputString");
%>

<h2>Resultado del Análisis LL(1)</h2>
<p>La cadena '<%= inputString %>' es <%= result %>.</p>

<h3>Conjunto FIRST:</h3>
<% for (Map.Entry<String, Set<String>> entry : firstSets.entrySet()) { %>
<p><%= entry.getKey() %> : <%= entry.getValue() %></p>
<% } %>

<h3>Conjunto FOLLOW:</h3>
<% for (Map.Entry<String, Set<String>> entry : followSets.entrySet()) { %>
<p><%= entry.getKey() %> : <%= entry.getValue() %></p>
<% } %>

<h3>Tabla M (Análisis LL(1)):</h3>
<% for (Map.Entry<String, Map<String, Production>> entry : parsingTable.entrySet()) { %>
<p><%= entry.getKey() %> : <%= entry.getValue() %></p>
<% } %>
