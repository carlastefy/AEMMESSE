<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%--
  Created by IntelliJ IDEA.
  User: M.DELUCIA18
  Date: 10/07/2024
  Time: 18:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Stampa Carrello</title>
    <link rel="stylesheet" type="text/css" href="./css/generale.css">
    <link rel="stylesheet" type="text/css" href="./css/headerStyle.css">
    <link rel="stylesheet" type="text/css" href="./css/footerStyle.css">
    <link rel="stylesheet" type="text/css" href="./css/carrelloStyle.css">
    <link rel="stylesheet" href="./css/print.css" media="print">


</head>
<body>
<div class="wrapper">
    <%@include file="header.jsp"%>
    <div class="content">
        <%-- Riepilogo risultati ricerca (NEW) --%>
        <c:if test="${not empty q}">
            <p class="search-summary">
                Trovati ${totalResults} risultati per "<strong>${fn:escapeXml(q)}</strong>"
            </p>
        </c:if>

        <div class="book-list">
            <c:if test="${not empty results}">
                <c:forEach var="libro" items="${results}">
                    <div class="book-item">
                        <a href="mostra-libro?isbn=${libro.isbn}">
                            <img src="${libro.immagine}" alt="${libro.titolo}" class="book-image" loading="lazy">
                        </a>
                        <div class="book-details">
                            <h3 class="book-title">${libro.titolo}</h3>
                            <div class="book-price">
                                <c:if test="${libro.sconto != 0}">
                                    <span class="book-discount">-${libro.sconto}%</span>
                                    <span class="book-new-price">${(libro.prezzo - (libro.prezzo * libro.sconto / 100))} €</span>
                                    <span class="book-old-price">${libro.prezzo} €</span>
                                </c:if>
                                <c:if test="${libro.sconto == 0}">
                                    <span class="book-new-price">${libro.prezzo} €</span>
                                </c:if>
                            </div>
                            <c:choose>
                                <c:when test="${wishList != null && wishList.libri.contains(libro)}">
                                    <c:set var="address" value="images/heartsBlack-icon.png"/>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="address" value="images/hearts-icon.png"/>
                                </c:otherwise>
                            </c:choose>
                            <div class="book-actions">
                                <button class="favorite-button" onclick="addToFavorites('${libro.isbn}')" data-isbn="${libro.isbn}">
                                    <img src="${address}" alt="Aggiungi ai preferiti">
                                </button>
                                <c:choose>
                                    <c:when test="${libro.disponibile}">
                                        <form action="aggiungi-carrello">
                                            <input type="hidden" name="isbn" value="${libro.isbn}">
                                            <input type="hidden" name="source" value="ricerca">
                                            <input type="hidden" name="q" value="${q}">
                                            <input type="image" src="./images/icon-cart.png" name="aggCarBut" alt="Carrello" width="20" height="20">
                                        </form>
                                    </c:when>
                                    <c:otherwise>
                                        <input type="hidden" name="isbn" value="${libro.isbn}">
                                        <input type="image" src="./images/icon-cart-disabled.png" name="aggCarBut" alt="Carrello" width="20" height="20">
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:if>
        </div>
            <c:url var="ricercaUrl" value="/ricerca-servlet"/>

            <c:if test="${totalPages > 1}">
                <div class="pagination">

                        <%-- Calcolo intervallo di pagine da mostrare (max 7) --%>
                    <c:set var="maxPagesToShow" value="7"/>

                    <c:set var="startPage" value="${page - 3}"/>
                    <c:if test="${startPage < 1}">
                        <c:set var="startPage" value="1"/>
                    </c:if>

                    <c:set var="endPage" value="${startPage + maxPagesToShow - 1}"/>
                    <c:if test="${endPage > totalPages}">
                        <c:set var="endPage" value="${totalPages}"/>
                        <c:set var="startPage" value="${endPage - maxPagesToShow + 1}"/>
                        <c:if test="${startPage < 1}">
                            <c:set var="startPage" value="1"/>
                        </c:if>
                    </c:if>

                        <%-- Precedente --%>
                    <c:choose>
                        <c:when test="${page > 1}">
                            <a href="${ricercaUrl}?q=${fn:escapeXml(q)}&page=${page - 1}" class="page-link">
                                &laquo; Precedente
                            </a>
                        </c:when>
                        <c:otherwise>
                            <span class="page-link disabled">&laquo; Precedente</span>
                        </c:otherwise>
                    </c:choose>

                        <%-- Prima pagina + ellissi se necessario --%>
                    <c:if test="${startPage > 1}">
                        <a href="${ricercaUrl}?q=${fn:escapeXml(q)}&page=1" class="page-link">1</a>
                        <span class="page-link ellipsis">…</span>
                    </c:if>

                        <%-- Pagine centrali --%>
                    <c:forEach var="p" begin="${startPage}" end="${endPage}">
                        <a href="${ricercaUrl}?q=${fn:escapeXml(q)}&page=${p}"
                           class="page-link ${p == page ? 'active' : ''}">
                                ${p}
                        </a>
                    </c:forEach>

                        <%-- Ultima pagina + ellissi se necessario --%>
                    <c:if test="${endPage < totalPages}">
                        <span class="page-link ellipsis">…</span>
                        <a href="${ricercaUrl}?q=${fn:escapeXml(q)}&page=${totalPages}" class="page-link">
                                ${totalPages}
                        </a>
                    </c:if>

                        <%-- Successiva --%>
                    <c:choose>
                        <c:when test="${page < totalPages}">
                            <a href="${ricercaUrl}?q=${fn:escapeXml(q)}&page=${page + 1}" class="page-link">
                                Successiva &raquo;
                            </a>
                        </c:when>
                        <c:otherwise>
                            <span class="page-link disabled">Successiva &raquo;</span>
                        </c:otherwise>
                    </c:choose>

                </div>
            </c:if>

    </div>
    <%@include file="footer.jsp"%>
</div>


<script>
    function addToFavorites(isbn) {
        // Crea una nuova istanza di XMLHttpRequest
        var xhttp = new XMLHttpRequest();

        // Definisce la funzione di gestione degli eventi per la risposta ricevuta
        xhttp.onreadystatechange = function() {
            if (this.readyState === 4 && this.status === 200) {
                // La richiesta è stata completata e la risposta è pronta
                // Parsa la risposta JSON
                var data = JSON.parse(this.responseText);

                // Aggiorna l'icona del cuoricino
                const favoriteButton = document.querySelector(`button[class="favorite-button"][data-isbn="` + isbn + `"]`);
                if (data.isInWishList) {
                    favoriteButton.querySelector('img').src = "images/heartsBlack-icon.png";
                } else {
                    favoriteButton.querySelector('img').src = "images/hearts-icon.png";
                }
            } else {
                // Gestisci eventuali errori
                console.error('Errore durante l\'aggiunta ai preferiti:', this.status);
            }
        };

        // Imposta il metodo e l'URL della richiesta
        xhttp.open("POST", "aggiungi-ai-preferiti?isbn=" + isbn, true);

        // Imposta l'intestazione Content-Type
        xhttp.setRequestHeader("Content-Type", "application/json");

        // Invia la richiesta
        xhttp.send();

    }



</script>
</body>
</html>