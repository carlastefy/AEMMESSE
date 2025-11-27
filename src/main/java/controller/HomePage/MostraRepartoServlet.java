package controller.HomePage;

import controller.utils.Validator;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.libroService.Libro;
import model.libroService.LibroDAO;
import model.libroService.Reparto;
import model.libroService.RepartoDAO;
import model.utenteService.Utente;

import java.io.IOException;
import java.util.List;

@WebServlet("/mostra-reparto")
public class MostraRepartoServlet extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        if(Validator.checkIfUserAdmin((Utente) request.getSession().getAttribute("utente"))) {
            final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/admin/homepageAdmin.jsp");
            dispatcher.forward(request, response);
        }
        final int idReparto = Integer.parseInt(request.getParameter("id"));
        final String position = request.getParameter("position");
        System.out.println(position);
        String address="/WEB-INF/results/reparto.jsp";

        Reparto reparto = new Reparto();
        final List<Reparto> reparti = (List<Reparto>) getServletContext().getAttribute("reparti");
        for(final Reparto r : reparti) {
            if(r.getIdReparto() == idReparto) {
                reparto = r;
            }
        }

        if (reparto != null) {
            request.setAttribute("reparto", reparto);

            final HttpSession session = request.getSession();
            session.setAttribute("repartoAttuale", idReparto);
            if (position != null) {
                address += "#"+position;
                System.out.println("address: "+address);
            }
        } else {
            final RequestDispatcher dispatcher=request.getRequestDispatcher("/WEB-INF/errorJsp/ErroreReparto.jsp");
            dispatcher.forward(request, response); //provvisorio
            request.setAttribute("repartoNonTrovato", true);
        }

        final RequestDispatcher dispatcher = request.getRequestDispatcher(address);
        dispatcher.forward(request, response);
    }
}


