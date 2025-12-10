document.addEventListener("DOMContentLoaded", function () {
    var queryString = window.location.search;
    var searchParams = new URLSearchParams(queryString);
    var position = searchParams.get("position");

    if (position) {
        var element = document.getElementById(position);
        if (element) {
            element.scrollIntoView({ behavior: "auto", block: "start", inline: "nearest" });
        }
    }
});
