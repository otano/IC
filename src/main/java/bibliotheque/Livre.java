package bibliotheque;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Livre {
    // Classe pour interagir avec la table Livres

    private Connection conn;

    public Livre(Connection conn)
    {
        this.conn = conn;
    }

    public void addToBDD(String titre, String auteur) {
        // Ajout d'un livre à la bilbiothèque
        String sql = "INSERT INTO Livres(titre, auteur) VALUES (?, ?)";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, titre);
            stmt.setString(2, auteur);
            stmt.executeUpdate();
            System.out.println("Livre ajouté : " + titre);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void afficherTous() {
        String sql = "SELECT id_livre, titre, auteur FROM Livres";
        try (Statement stmt = this.conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("Liste des livres :");
            while (rs.next()) {
                int id = rs.getInt("id_livre");
                String titre = rs.getString("titre");
                String auteur = rs.getString("auteur");
                System.out.println(id + " | " + titre + " | " + auteur);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteFromBDD(String titre, String auteur) {
        String sql = "DELETE FROM Livres WHERE titre = ? AND auteur = ?";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {

            stmt.setString(1, titre);
            stmt.setString(2, auteur);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Livre supprimé : " + titre + " de " + auteur);
            } else {
                System.out.println("Aucun livre trouvé avec ce titre/auteur.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Integer findId(String titre, String auteur) 
    {
        String sql = "SELECT id_livre FROM Livres WHERE titre = ? AND auteur = ?";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, titre);
            stmt.setString(2, auteur);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_livre");
                } else {
                    System.out.println("Aucun livre trouvé pour : " + titre + " " + auteur);
                    return null;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}