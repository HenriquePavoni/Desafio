<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.atd.autorizacao.model.SolicitacaoAutorizacao" %>
<%
    List<SolicitacaoAutorizacao> solicitacoes =
            (List<SolicitacaoAutorizacao>) request.getAttribute("solicitacoes");
%>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Autorizacao de Procedimentos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<header>
    <h1>Solicitacao de Autorizacao</h1>
</header>
<main>
    <section class="card">
        <form method="post" action="${pageContext.request.contextPath}/autorizacoes">
            <label for="codigoProcedimento">Procedimento</label>
            <input type="text" id="codigoProcedimento" name="codigoProcedimento" required>

            <label for="idade">Idade</label>
            <input type="number" id="idade" name="idade" min="0" required>

            <label for="sexo">Sexo</label>
            <select id="sexo" name="sexo" required>
                <option value="M">M</option>
                <option value="F">F</option>
            </select>

            <button type="submit">Avaliar</button>
        </form>

        <% if (request.getAttribute("resultado") != null) { %>
            <p class="resultado status-${resultado.status}">
                <strong>${resultado.status}</strong> &mdash; ${resultado.motivo}
            </p>
        <% } %>
        <% if (request.getAttribute("erro") != null) { %>
            <p class="resultado erro">${erro}</p>
        <% } %>
    </section>

    <section class="card">
        <h2>Solicitacoes avaliadas</h2>
        <table>
            <thead>
            <tr>
                <th>Procedimento</th>
                <th>Idade</th>
                <th>Sexo</th>
                <th>Status</th>
                <th>Motivo</th>
            </tr>
            </thead>
            <tbody>
            <% if (solicitacoes != null && !solicitacoes.isEmpty()) {
                   for (SolicitacaoAutorizacao s : solicitacoes) { %>
                <tr>
                    <td><%= s.getCodigoProcedimento() %></td>
                    <td><%= s.getIdade() %></td>
                    <td><%= s.getSexo() %></td>
                    <td><%= s.getStatus() %></td>
                    <td><%= s.getMotivo() %></td>
                </tr>
            <%     }
               } else { %>
                <tr>
                    <td colspan="5">Nenhuma solicitacao avaliada ainda.</td>
                </tr>
            <% } %>
            </tbody>
        </table>
    </section>
</main>
</body>
</html>
