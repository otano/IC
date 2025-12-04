package bibliotheque;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Client {
    // Classe pour interagir avec la table Clients

    private Connection conn;

    public Client(Connection conn)
    {
        this.conn = conn;
    }

    public void addToBDD(String nom, String prenom) 
    {
        // Ajout d'un Client à la bilbiothèque
        String sql = "INSERT INTO Clients(nom, prenom) VALUES (?, ?)";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.executeUpdate();
            System.out.println("Client ajouté : " + nom);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void afficherTous() 
    {
        String sql = "SELECT id_client, nom, prenom FROM Clients";
        try (Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("Liste des Clients :");
            while (rs.next()) {
                int id = rs.getInt("id_client");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                System.out.println(id + " | " + nom + " | " + prenom);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteFromBDD(String nom, String prenom) 
    {
        String sql = "DELETE FROM clients WHERE nom = ? AND prenom = ?";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {

            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Client supprimé : " + nom + " " + prenom);
            } else {
                System.out.println("Aucun client trouvé avec ce nom/prenom.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Integer findId(String nom, String prenom) 
    {
        String sql = "SELECT id_client FROM Clients WHERE nom = ? AND prenom = ?";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, nom);
            stmt.setString(2, prenom);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_client");
                } else {
                    System.out.println("Aucun client trouvé pour : " + prenom + " " + nom);
                    return null;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}