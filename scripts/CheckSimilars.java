import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckSimilars {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/curtavento?serverTimezone=UTC";
        try (Connection conn = DriverManager.getConnection(url, "root", "admin123");
             Statement stmt = conn.createStatement()) {
            
            System.out.println("=== USINAS SIMILARES ===");
            ResultSet rs = stmt.executeQuery(
                "SELECT id, nome FROM usina WHERE nome LIKE '%Caetit%' OR nome LIKE '%Igapor%' OR nome LIKE '%Pinda%'"
            );
            while (rs.next()) {
                System.out.println(rs.getInt(1) + " - " + rs.getString(2));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
