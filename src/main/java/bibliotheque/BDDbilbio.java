package bibliotheque;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BDDbilbio {
    // L'hôte est le nom du service Docker : 'mariadb'
    private static final String URL = "jdbc:mysql://localhost:3307/bibliotheque"; 
    
    // Identifiants correspondant à votre docker-compose.yml
    private static final String USER = "root"; 
    private static final String PASSWORD = "rootpassword"; 

    public static Connection getConnection() throws SQLException {
        try {
            // Nécessite la dépendance MySQL Connector/J !
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC non trouvé.");
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}