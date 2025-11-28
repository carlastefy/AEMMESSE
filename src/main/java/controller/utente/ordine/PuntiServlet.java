package controller.utente.ordine;

import controller.utils.Validator;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.ordineService.Ordine;
import model.utenteService.Utente;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

@WebServlet("/punti-servlet")
public class PuntiServlet extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final HttpSession session = request.getSession();
        final Utente utente = (Utente) session.getAttribute("utente");
        String address = null;
        if(Validator.checkIfUserAdmin(utente)) {
            final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/admin/homepageAdmin.jsp");
            dispatcher.forward(request, response);
            return;
        }
        final Ordine ordine = new Ordine();
        ordine.setCitta(request.getParameter("citta"));
        ordine.setIndirizzoSpedizione(request.getParameter("indirizzo"));
        final String puntiString = request.getParameter("punti");
        int punti = 0;
        if(puntiString != null && !puntiString.isEmpty())
            punti = Integer.parseInt(puntiString);
        ordine.setPuntiSpesi(punti);

        final double costo = Double.parseDouble(request.getParameter("costo"));
        final double costoAggiornato=costo - (ordine.getPuntiSpesi() * 0.10);
        final BigDecimal bd = new BigDecimal(costoAggiornato).setScale(2, RoundingMode.HALF_UP);
        final double costoArrotondato = bd.doubleValue();
        ordine.setCosto(costoArrotondato);

        address = "/WEB-INF/results/pagamentoOrdine.jsp";
        request.setAttribute("ordine", ordine);
        request.setAttribute("costo", request.getAttribute("costo"));
        final RequestDispatcher dispatcher = request.getRequestDispatcher(address);
        dispatcher.forward(request, response);
    }
}
