import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class PrintMuns {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/curtavento?serverTimezone=UTC";
        try (Connection conn = DriverManager.getConnection(url, "root", "admin123");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, nome, codigo_ibge FROM municipio")) {
            
            while (rs.next()) {
                System.out.println(rs.getInt(1) + " - " + rs.getString(2) + " - " + rs.getString(3));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
