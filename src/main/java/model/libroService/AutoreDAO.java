package model.libroService;
import model.ConPool;
import model.utenteService.Utente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AutoreDAO {
    public void doSave(final Autore autore){
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO autore (cf, nome, cognome) VALUES(?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, autore.getCf());
            ps.setString(2, autore.getNome());
            ps.setString(3, autore.getCognome());
            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("INSERT error.");
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void deleteAutore(final String cf){
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement("DELETE FROM autore WHERE cf=?");
            ps.setString(1, cf);
            if(ps.executeUpdate() != 1)
                throw new RuntimeException("DELETE error.");
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Autore searchAutore(final String cf) {
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement("SELECT nome, cognome FROM autore WHERE cf=?");
            ps.setString(1, cf);
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                final Autore autore = new Autore();
                autore.setCf(cf);
                autore.setNome(rs.getString(1));
                autore.setCognome(rs.getString(2));
                return autore;
            }
            return null;
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Libro> getScrittura(final String cf){
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement("SELECT isbn FROM scrittura WHERE cf=?");
            ps.setString(1, cf);
            final List<Libro> lista = new ArrayList<>();
            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                final String isbn = rs.getString(1);
                final LibroDAO p = new LibroDAO();
                final Libro libro=p.doRetrieveById(isbn);
                lista.add(libro);
            }
            return lista;
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
