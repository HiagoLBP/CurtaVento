import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckFlyway {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/curtavento?serverTimezone=UTC";
        try (Connection conn = DriverManager.getConnection(url, "root", "admin123");
             Statement stmt = conn.createStatement()) {
            
            // Deletar a V11 se ela tiver sido gravada como falha
            int rows = stmt.executeUpdate("DELETE FROM flyway_schema_history WHERE version = '11'");
            System.out.println("Linhas da V11 deletadas: " + rows);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
