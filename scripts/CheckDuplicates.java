import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckDuplicates {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/curtavento?serverTimezone=UTC";
        try (Connection conn = DriverManager.getConnection(url, "root", "admin123");
             Statement stmt = conn.createStatement()) {
            
            System.out.println("=== USINAS COM NOMES DUPLICADOS ===");
            ResultSet rs = stmt.executeQuery(
                "SELECT nome, COUNT(*) as qtd FROM usina GROUP BY nome HAVING COUNT(*) > 1"
            );
            boolean hasDups = false;
            while (rs.next()) {
                hasDups = true;
                System.out.println("Nome: " + rs.getString("nome") + " | Quantidade: " + rs.getInt("qtd"));
            }
            if (!hasDups) System.out.println("Nenhuma duplicidade exata de nome encontrada.");
            
            System.out.println("\n=== TOTAL DE USINAS ===");
            rs = stmt.executeQuery("SELECT COUNT(*) FROM usina");
            if (rs.next()) System.out.println("Total: " + rs.getInt(1));

            System.out.println("\n=== USINAS ORIGINAIS (ID <= 4) OU PARECIDAS ===");
            rs = stmt.executeQuery("SELECT id, nome, operador FROM usina WHERE id <= 10 OR nome LIKE '%Pindaí%' OR nome LIKE '%Guanambi%'");
            while (rs.next()) {
                System.out.println("ID " + rs.getInt("id") + " | " + rs.getString("nome") + " | " + rs.getString("operador"));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
