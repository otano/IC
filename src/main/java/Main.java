
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        //bibliotheque.Livre livre = new bibliotheque.Livre("1984", "George Orwell");
        System.out.println("Bonjour !");
        try (Connection conn = bibliotheque.BDDbilbio.getConnection()) {
            System.out.println("Connexion réussie à la base de données !");

            // Ajout d'un livre
            System.out.println("=====Ajout d'un livre=====");
            bibliotheque.Livre livre =  new bibliotheque.Livre(conn);
            livre.addToBDD("Le petit prince", "Antoine de Saint Exupéry");

            livre.afficherTous();

            // Ajout d'un client
            System.out.println("=====Ajout d'un client=====");
            bibliotheque.Client client = new bibliotheque.Client(conn);
            client.addToBDD("Nicolas", "Zaimeche");

            client.afficherTous();

            System.out.println("=====Ajout d'un emprunt=====");
            bibliotheque.Emprunt emprunt = new bibliotheque.Emprunt(conn);
            Integer id_livre = livre.findId("Le petit prince", "Antoine de Saint Exupéry");
            Integer id_client = client.findId("Nicolas", "Zaimeche");;
            emprunt.addToBDD(id_client, id_livre, LocalDate.now(), 4);
            emprunt.afficherTous();
            emprunt.deleteByLivre(id_livre);
            emprunt.afficherTous();
            livre.deleteFromBDD("Le petit prince", "Antoine de Saint Exupéry");
            livre.afficherTous();
            client.deleteFromBDD("Nicolas", "Zaimeche");
            client.afficherTous();
            
        } catch (SQLException e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
            e.printStackTrace();
        }
    }
}