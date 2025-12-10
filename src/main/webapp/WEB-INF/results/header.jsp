<meta name="viewport" content="width=device-width, initial-scale=1.0">

<header>
    <div class="container">

            <div class="logo-searchBar">
                <div class="logoHeader">
                    <img src="./images/logo.png" alt="AEMME" width="88"  class="image">
                </div>
                <div class="search-bar">
                    <div class="barra">
                        <input type="text" id="search" onkeyup="search()" placeholder="Search">
                        <a id="search-icon" href="#">
                            <img src="./images/search-icon-25.png" alt="search" width="25" height="25" class="image">
                        </a>
                    </div>
                    <div id="suggestions" class="suggestions"></div>
                </div>
            </div>


        <div class="icons-and-menu">
            <button class="menu-toggle" onclick="toggleMenu()">
                <img src="images/menu-icon.png" alt="Menu" width="25" height="25">
            </button> <!-- Pulsante per il menu a discesa -->
            <div class="icons">
                <a href="show-wishlist">
                    <img src="./images/hearts-icon-25.png" alt="wishList" width="25" height="25" class="image">
                </a>
                <a href="cart-servlet">
                    <img src="./images/icon-cart-25.png" alt="cart" width="25" height="25" class="spaced-image image">
                </a>
                <a href="area-personale">
                    <img src="./images/icon-user-25.png" alt="area personale" width="24" height="25" class="image">
                </a>
            </div>
        </div>
    </div>


    <ul id="menu">
        <li><a class="active" href="index.html">Home</a></li>
        <c:forEach items="${reparti}" var="reparto">
            <c:if test="${reparto.nome ne 'Libri di Tendenza'}">
                <li><a href="mostra-reparto?id=${reparto.idReparto}">${reparto.nome}</a></li>
            </c:if>
        </c:forEach>
    </ul>
</header>


<script>
    function toggleMenu() {
        var menu = document.getElementById('menu');
        menu.classList.toggle('show');
    }

    function search() {
        let query = document.getElementById("search").value;
        let xhr = new XMLHttpRequest();
        xhr.open("GET", "search?q=" + query, true);
        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4 && xhr.status === 200) {
                let jsonResponse = JSON.parse(xhr.responseText);
                let suggestions = document.getElementById("suggestions");
                suggestions.innerHTML = "";
                for (let i = 0; i < jsonResponse.length; i++) {
                    let suggestion = document.createElement("a");
                    suggestion.classList.add("suggestion");
                    suggestion.href = "mostra-libro?isbn=" + jsonResponse[i].isbn;
                    suggestion.textContent = jsonResponse[i].titolo;
                    suggestions.appendChild(suggestion);
                    let lineBreak = document.createElement("br");
                    suggestions.appendChild(lineBreak);
                }
            }
        };
        xhr.send();
    }

    document.getElementById("search-icon").addEventListener("click", function(event) {
        event.preventDefault();  // Prevent default link behavior
        let query = document.getElementById("search").value;
        window.location.href = "ricerca-servlet?q=" + encodeURIComponent(query);//encodeURIComponent è usato per assicurarsi che i caratteri speciali nel valore della ricerca siano correttamente codificati.
    });


</script>
