package controller.utente;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.utenteService.Utente;
import model.utenteService.UtenteDAO;

import java.io.IOException;

@WebServlet("/modifica-password")
public class ModificaPasswordServlet extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final String password = request.getParameter("password");
        String address = null;
        if (password == null || password.isEmpty() || password.length() > 16) {
            address = "/WEB-INF/errorJsp/erroreForm.jsp";
           // response.sendRedirect("/WEB-INF/errorJsp/erroreForm.jsp");
        }else {
            address = "area-personale";

            final UtenteDAO serviceUtente = new UtenteDAO();
            final Utente utente = (Utente) request.getSession().getAttribute("utente");
            utente.setCodiceSicurezza(password);
            serviceUtente.updateUtentePassword(utente);
        }

        final RequestDispatcher dispatcher = request.getRequestDispatcher(address);
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }
}
