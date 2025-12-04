import bibliotheque.Emprunt;
import bibliotheque.BDDbilbio;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class EmpruntTest {

    private static Connection conn;
    private Emprunt empruntService;
    
    private static int TEST_CLIENT_ID = 999;
    private static int TEST_LIVRE_ID = 888;
    // On garde la variable LIVRE_ISBN mais on l'utilise pour la colonne 'auteur'
    private static String LIVRE_ISBN = "978-TEST-EMPRUNT"; 
    
    // initialisation de la Connexion et des Données de Base ---

    @BeforeAll
    static void initConnectionAndData() throws SQLException {
        conn = BDDbilbio.getConnection();
        assertNotNull(conn, "La connexion à la BDD ne doit pas être nulle.");
        
        cleanUpData(conn); 
       
        insertTestData(conn); 
    }

    @BeforeEach
    void setup() {
        empruntService = new Emprunt(conn);
    }

    // Test d'Intégration Principal ---

    @Test
    @DisplayName("L'ajout d'un emprunt doit insérer l'emprunt et marquer le livre comme non disponible")
    void testAddToBDD_SuccessAndUpdate() throws SQLException {
        
        LocalDate dateEmprunt = LocalDate.now();
        int dureeSemaines = 2;
        
        // WHEN
        empruntService.addToBDD(TEST_CLIENT_ID, TEST_LIVRE_ID, dateEmprunt, dureeSemaines);

        // THEN (
        // L'emprunt existe dans la table Emprunts
        assertTrue(empruntExists(TEST_CLIENT_ID, TEST_LIVRE_ID), 
                   "L'enregistrement de l'emprunt doit être présent dans la BDD.");

        //  Le livre est marqué comme non disponible dans la table Livres
        assertFalse(isLivreDisponible(TEST_LIVRE_ID), 
                    "La colonne 'disponible' du livre doit être FALSE après l'emprunt.");
        
        // La date de retour est correctement calculée
        LocalDate expectedDateRetour = dateEmprunt.plusWeeks(dureeSemaines);
        assertEquals(expectedDateRetour, getDateRetour(TEST_LIVRE_ID),
                     "La date de retour calculée n'est pas correcte.");
    }
    
    // Nettoyage après chaque test pour assurer l'indépendance ---

    @AfterEach
    void cleanUpTestRun() throws SQLException {
        empruntService.deleteByLivre(TEST_LIVRE_ID);
        updateLivreDisponibilite(conn, TEST_LIVRE_ID, true);
    }
    
    @AfterAll
    static void finalCleanUp() throws SQLException {
        // Suppression finale du livre et du client de test
        cleanUpData(conn); 
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    // Helpers

    // supprime les données de test
    private static void cleanUpData(Connection conn) throws SQLException {
        try (PreparedStatement deleteEmprunt = conn.prepareStatement("DELETE FROM Emprunts WHERE id_livre = ?");
             PreparedStatement deleteLivre = conn.prepareStatement("DELETE FROM Livres WHERE id_livre = ?");
             PreparedStatement deleteClient = conn.prepareStatement("DELETE FROM Clients WHERE id_client = ?")) {
            
            deleteEmprunt.setInt(1, TEST_LIVRE_ID); deleteEmprunt.executeUpdate();
            deleteLivre.setInt(1, TEST_LIVRE_ID); deleteLivre.executeUpdate();
            deleteClient.setInt(1, TEST_CLIENT_ID); deleteClient.executeUpdate();
        }
    }
    
    // Insère les données nécessaires avant le début des tests
    private static void insertTestData(Connection conn) throws SQLException {
        // CORRECTION MAJEURE: Remplacement de 'isbn' par 'auteur' pour correspondre au schéma BDD
        try (PreparedStatement psClient = conn.prepareStatement("INSERT INTO Clients(id_client, nom, prenom) VALUES (?, 'TestEmprunt', 'Client')");
             PreparedStatement psLivre = conn.prepareStatement("INSERT INTO Livres(id_livre, titre, auteur, disponible) VALUES (?, 'TitreTest', ?, TRUE)")) {
            
            psClient.setInt(1, TEST_CLIENT_ID); psClient.executeUpdate();
            
            psLivre.setInt(1, TEST_LIVRE_ID); 
            // CORRECTION: psLivre.setString(1, ...) a été changé en psLivre.setString(2, ...)
            psLivre.setString(2, LIVRE_ISBN); // Définit le 2ème '?' (auteur) avec la valeur d'ISBN pour les besoins du test
            
            psLivre.executeUpdate();
        }
    }

    // Vérifie si un emprunt existe
    private boolean empruntExists(int idClient, int idLivre) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Emprunts WHERE id_client = ? AND id_livre = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idClient);
            stmt.setInt(2, idLivre);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // Vérifie le statut 'disponible' du livre
    private boolean isLivreDisponible(int idLivre) throws SQLException {
        String sql = "SELECT disponible FROM Livres WHERE id_livre = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idLivre);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("disponible");
                }
            }
        }
        return false; // Supposons non disponible si non trouvé
    }
    
    // Récupère la date de retour calculée
    private LocalDate getDateRetour(int idLivre) throws SQLException {
        String sql = "SELECT date_retour FROM Emprunts WHERE id_livre = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idLivre);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Conversion de java.sql.Date à java.time.LocalDate
                    java.sql.Date sqlDate = rs.getDate("date_retour");
                    return sqlDate != null ? sqlDate.toLocalDate() : null;
                }
            }
        }
        return null; 
    }

    // Met à jour manuellement le statut 'disponible' du livre
    private static void updateLivreDisponibilite(Connection conn, int idLivre, boolean disponible) throws SQLException {
        String sql = "UPDATE Livres SET disponible = ? WHERE id_livre = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, disponible);
            stmt.setInt(2, idLivre);
            stmt.executeUpdate();
        }
    }
}