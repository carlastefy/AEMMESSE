package model.wishList;

import model.ConPool;
import model.libroService.Libro;
import model.libroService.LibroDAO;
import model.utenteService.Utente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class WishListDAO {
    public void doSave(final WishList wishList, final String isbn){
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO wishlist (email, isbn) VALUES(?,?)");
            ps.setString(1, wishList.getEmail());
            ps.setString(2, isbn);

            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("INSERT error.");
            }

        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public WishList doRetrieveByEmail(final String email) {
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement("SELECT email, isbn FROM wishlist WHERE email=?");
            ps.setString(1, email);
            final ResultSet rs = ps.executeQuery();
            final WishList wishlist = new WishList();
            wishlist.setLibri(new ArrayList<>());
            while (rs.next()) {
                wishlist.setEmail(rs.getString(1));
                final LibroDAO libroService = new LibroDAO();
                final Libro libro = libroService.doRetrieveById(rs.getString(2));
                wishlist.addLibro(libro);
            }
            return wishlist;
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteLibro(final String email, final String isbn){
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement("DELETE FROM wishlist WHERE email=? AND isbn =?");
            ps.setString(1, email);
            ps.setString(2, isbn);
            if(ps.executeUpdate() != 1)
                throw new RuntimeException("DELETE error.");
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteWishListByEmail(final String email){
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement("DELETE FROM wishlist WHERE email=?");
            ps.setString(1, email);
            if(ps.executeUpdate() <= 0)
                throw new RuntimeException("DELETE error.");
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
