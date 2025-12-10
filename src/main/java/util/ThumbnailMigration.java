package util;

import model.ConPool;

import java.nio.file.Paths;
import java.sql.*;

public class ThumbnailMigration {

    public static void main(String[] args) throws Exception {

        // 1) cartella dove salvare le immagini nel progetto
        String baseDir = Paths.get("src/main/webapp/images/libri")
                .toAbsolutePath()
                .toString();
        System.out.println("Base dir: " + baseDir);

        // 2) connessione al DB usando ConPool
        try (Connection conn = ConPool.getConnection()) {

            // Assumo tabella "libro" con colonne "isbn" e "immagine"
            // Cambia nomi se nel tuo schema sono diversi
            String selectSql = "SELECT isbn, immagine FROM libro";
            String updateSql = "UPDATE libro SET immagine = ? WHERE isbn = ?";

            try (PreparedStatement select = conn.prepareStatement(selectSql);
                 PreparedStatement update = conn.prepareStatement(updateSql)) {

                ResultSet rs = select.executeQuery();
                while (rs.next()) {
                    String isbn = rs.getString("isbn");
                    String img = rs.getString("immagine");

                    // salta se nulla o già locale
                    if (img == null || !img.startsWith("http")) {
                        continue;
                    }

                    try {
                        String newPath = ImageDownloader.downloadAndCreateThumbnail(
                                baseDir,
                                img,
                                isbn
                        );

                        update.setString(1, newPath); // nuovo path relativo
                        update.setString(2, isbn);
                        update.executeUpdate();

                        System.out.println("OK " + isbn + " -> " + newPath);
                    } catch (Exception e) {
                        System.err.println("ERRORE " + isbn + " : " + e.getMessage());
                    }
                }
            }
        }
    }
}
