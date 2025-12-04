import bibliotheque.Client;

import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

public class ClientTest {

    private static Connection conn;
    private Client client;

    @BeforeAll
    static void initConnection() throws SQLException {
        conn = bibliotheque.BDDbilbio.getConnection();
        assertNotNull(conn, "Connexion à la BDD échouée !");
    }

    @BeforeEach
    void setup() {
        client = new Client(conn);
    }

    @Test
    void testAddAndFindClient() throws SQLException {
        String nom = "TestNom";
        String prenom = "TestPrenom";
        client.addToBDD(nom, prenom);
        Integer id = client.findId(nom, prenom);
        assertNotNull(id, "Le client n'a pas été trouvé après insertion !");
    }

    @Test
    void testDeleteClient() throws SQLException {
        String nom = "ClientASupprimer";
        String prenom = "Test";
        client.addToBDD(nom, prenom); // S'assurer que le client existe
        client.deleteFromBDD(nom, prenom);
        Integer id = client.findId(nom, prenom);
        assertNull(id, "Le client devrait avoir été supprimé !");
    }

    @AfterAll
    static void closeConn() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
}
