import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LoggerUtil {
    public static void log(String username, String role, String message) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO logs (username, role, message) VALUES (?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, role);
            pst.setString(3, message);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
