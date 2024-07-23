package BankingSystem;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/Admin_Login_Page")
public class Admin_Login_Page extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private static final String SELECT_QUERY = "SELECT * FROM Admin_login_page WHERE username=? AND password=?";
    
    public Admin_Login_Page() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            
            conn = Database_Connector.connect();
            
            
            stmt = conn.prepareStatement(SELECT_QUERY);
            stmt.setString(1, username);
            stmt.setString(2, password);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                 
                response.sendRedirect("Admin.html");  
                } else {
              
                out.println("Invalid username or password");
            }
        } catch (SQLException | Database_Connector.DatabaseConnectionException e) {
            e.printStackTrace();
            out.println("Error: " + e.getMessage());
        } finally {
            
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
