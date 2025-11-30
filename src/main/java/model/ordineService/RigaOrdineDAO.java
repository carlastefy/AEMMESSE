package model.ordineService;

import model.ConPool;
import model.carrelloService.RigaCarrello;
import model.libroService.LibroDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RigaOrdineDAO {

    public void doSave(final RigaOrdine rigaOrdine){
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO rigaordine (idOrdine, isbn, prezzoUnitario, quantita) VALUES(?,?,?, ?)");
            ps.setString(1, rigaOrdine.getIdOrdine());
            ps.setString(2, rigaOrdine.getLibro().getIsbn());
            ps.setDouble(3, rigaOrdine.getPrezzoUnitario());
            ps.setInt(4, rigaOrdine.getQuantita());
            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("INSERT error.");
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<RigaOrdine> doRetrivedByOrdine(final String idOrdine) {
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement("SELECT idOrdine, isbn, prezzoUnitario, quantita FROM rigaordine WHERE idOrdine=?");
            ps.setString(1, idOrdine);
            final ResultSet rs = ps.executeQuery();
            final List<RigaOrdine> lista = new ArrayList<>();
            while (rs.next()) {
                final RigaOrdine p = new RigaOrdine();
                final LibroDAO libroService= new LibroDAO();
                p.setIdOrdine(rs.getString(1));
                final String isbn=rs.getString(2);
                p.setLibro(libroService.doRetrieveById(isbn));
                //p.setIsbn(rs.getString(2));
                p.setPrezzoUnitario(rs.getDouble(3));
                p.setQuantita(rs.getInt(4));
                lista.add(p);
            }
            return lista;
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public RigaOrdine doRetriveById(final String idOrdine, final String isbn){
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement("SELECT idOrdine, isbn, prezzoUnitario, quantita FROM rigaordine WHERE idOrdine=? AND isbn=?");
            ps.setString(1, idOrdine);
            ps.setString(2, isbn);
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                final RigaOrdine p = new RigaOrdine();
                final LibroDAO libroService= new LibroDAO();
                p.setIdOrdine(rs.getString(1));
                p.setLibro(libroService.doRetrieveById(isbn));
                //p.setIsbn(rs.getString(2));
                p.setPrezzoUnitario(rs.getDouble(3));
                p.setQuantita(rs.getInt(4));
                return p;
            }
            return null;
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteRigaOrdine(final String isbn, final String idOrdine){
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement("DELETE FROM rigaordine WHERE idOrdine=? AND isbn =?");
            ps.setString(1, idOrdine);
            ps.setString(2, isbn);
            if(ps.executeUpdate() != 1)
                throw new RuntimeException("DELETE error.");
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteRigaOrdineByIdOrdine(final String idOrdine){
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement("DELETE FROM rigaordine WHERE idOrdine=?");
            ps.setString(1, idOrdine);
            if(ps.executeUpdate() < 1)
                throw new RuntimeException("DELETE error.");
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
