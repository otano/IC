import bibliotheque.Livre;

import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

public class LivreTest {

    private static Connection conn;
    private Livre livre;

    @BeforeAll
    static void initConnection() throws SQLException {
        conn = bibliotheque.BDDbilbio.getConnection();
        assertNotNull(conn, "Connexion à la BDD échouée !");
    }

    @BeforeEach
    void setup() {
        livre = new Livre(conn);
    }

    @Test
    void testAddAndFindLivre() throws SQLException {
        livre.addToBDD("TestLivre", "AuteurTest");
        Integer id = livre.findId("TestLivre", "AuteurTest");
        assertNotNull(id, "Le livre n'a pas été trouvé après insertion !");
    }

    @Test
    void testDeleteLivre() throws SQLException {
        livre.deleteFromBDD("TestLivre", "AuteurTest");
        Integer id = livre.findId("TestLivre", "AuteurTest");
        assertNull(id, "Le livre devrait avoir été supprimé !");
    }

    @AfterAll
    static void closeConn() throws SQLException {
        conn.close();
    }
}