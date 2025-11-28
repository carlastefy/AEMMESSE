package controller.utente.supporto;

import com.mysql.cj.Session;
import controller.utils.Validator;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.utenteService.Utente;

import java.io.IOException;

@WebServlet("/modifica-password-supporto")
public class ModificaPasswordSupporto extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final HttpSession session = request.getSession();
        final Utente utente = (Utente) session.getAttribute("utente");

        if(Validator.checkIfUserAdmin(utente)){
            final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/admin/modificaPassAdmin.jsp");
            dispatcher.forward(request, response);
        }else{
            final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/areaPservices/modificaPassword.jsp");
            dispatcher.forward(request, response);
        }

    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }
}
