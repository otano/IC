package bibliotheque;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class Emprunt {
    // Classe pour interagir avec la table Emprunts

    private Connection conn;

    public Emprunt(Connection conn)
    {
        this.conn = conn;
    }

    public void addToBDD(int idClient, int idLivre, java.time.LocalDate dateEmprunt, int dureeSemaines) 
    {
        String addEmpruntSql = "INSERT INTO Emprunts(id_client, id_livre, date_emprunt, date_retour) VALUES (?, ?, ?, ?)";
        String updateLivreSql = "UPDATE Livres SET disponible = FALSE WHERE id_livre = ?";
        try {
            PreparedStatement stmtEmprunt = conn.prepareStatement(addEmpruntSql);
            stmtEmprunt.setInt(1, idClient);
            stmtEmprunt.setInt(2, idLivre);
            stmtEmprunt.setDate(3, Date.valueOf(dateEmprunt)); // conversion LocalDate -> SQL Date
            stmtEmprunt.setDate(4, Date.valueOf(dateEmprunt.plusWeeks(dureeSemaines)));

            stmtEmprunt.executeUpdate();
            System.out.println("Nouvel emprunt ajouté : client " + idClient + ", livre " + idLivre);

            PreparedStatement stmtDisponible = conn.prepareStatement(updateLivreSql);
            stmtDisponible.setInt(1, idLivre);
            stmtDisponible.executeUpdate();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void afficherTous() {
        String sql = "SELECT id_emprunt, id_client, id_livre, date_emprunt, date_retour FROM Emprunts";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            System.out.println("=== Liste des emprunts ===");
            while (rs.next()) {
                int idEmprunt = rs.getInt("id_emprunt");
                int idClient = rs.getInt("id_client");
                int idLivre = rs.getInt("id_livre");
                java.sql.Date dateEmprunt = rs.getDate("date_emprunt");
                java.sql.Date dateRetour = rs.getDate("date_retour");

                System.out.println(
                    "Emprunt #" + idEmprunt +
                    " | Client: " + idClient +
                    " | Livre: " + idLivre +
                    " | Date emprunt: " + dateEmprunt +
                    " | Date retour: " + (dateRetour != null ? dateRetour : "Non rendu")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteByLivre(Integer idLivre) throws SQLException {
        String sql = "DELETE FROM emprunts WHERE id_livre = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idLivre);
            ps.executeUpdate();
        }
    }
}
