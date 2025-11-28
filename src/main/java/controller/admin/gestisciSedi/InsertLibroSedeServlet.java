package controller.admin.gestisciSedi;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.libroService.SedeDAO;

import java.io.IOException;

@WebServlet("/insert-libroSede")
public class InsertLibroSedeServlet extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final String[] libriIsbn = (request.getParameterValues("isbn"));
        final SedeDAO sedeDAO = new SedeDAO();
        if(libriIsbn!=null){
            for(final String isbn : libriIsbn){
                sedeDAO.addLibroSede(sedeDAO.doRetrieveById(Integer.parseInt(request.getParameter("idSede")
                )), isbn);
            }
        }
        response.sendRedirect("gestisci-sedi");
    }
}