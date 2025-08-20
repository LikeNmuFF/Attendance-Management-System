
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/student_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            initializeDatabase(conn);
            return conn;
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }
    
    private static void initializeDatabase(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Create tables if they don't exist
            String createStudentsTable = "CREATE TABLE IF NOT EXISTS students (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "student_id VARCHAR(20) UNIQUE NOT NULL, " +
                "name VARCHAR(100) NOT NULL, " +
                "email VARCHAR(100), " +
                "phone VARCHAR(20), " +
                "course VARCHAR(50))";
                
            String createAttendanceTable = "CREATE TABLE IF NOT EXISTS attendance (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "student_id VARCHAR(20) NOT NULL, " +
                "date DATE NOT NULL, " +
                "status ENUM('Present', 'Absent') NOT NULL, " +
                "FOREIGN KEY (student_id) REFERENCES students(student_id))";
                
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) UNIQUE NOT NULL, " +
                "password VARCHAR(100) NOT NULL, " +
                "role VARCHAR(20) DEFAULT 'Administrator')";
                
            stmt.execute(createStudentsTable);
            stmt.execute(createAttendanceTable);
            stmt.execute(createUsersTable);
            
            // Insert default admin user if not exists
            String checkAdmin = "SELECT COUNT(*) FROM users WHERE username = 'admin'";
            var rs = stmt.executeQuery(checkAdmin);
            if (rs.next() && rs.getInt(1) == 0) {
                String insertAdmin = "INSERT INTO users (username, password) VALUES ('admin', 'admin123')";
                stmt.execute(insertAdmin);
            }
            
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
}