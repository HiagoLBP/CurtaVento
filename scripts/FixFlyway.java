import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class FixFlyway {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/curtavento?serverTimezone=UTC";
        String user = "root";
        String pass = "admin123";
        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) {
            int rows = stmt.executeUpdate("DELETE FROM flyway_schema_history WHERE version = '10'");
            System.out.println("Linhas deletadas do Flyway (V10): " + rows);
            System.out.println("Pronto! Historico do Flyway limpo. Pode rodar a aplicacao novamente.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
