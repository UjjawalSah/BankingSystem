package BankingSystem;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/DepositServlet")
public class DepositServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String depositAmountStr = request.getParameter("depositAmount");
        double depositAmount;

        // Validate the deposit amount
        try {
            depositAmount = Double.parseDouble(depositAmountStr);
        } catch (NumberFormatException e) {
            forwardWithAlert(request, response, "Error", "Invalid deposit amount!", "error", "C_Dashboard.html");
            return;
        }

        // Retrieve user details from session
        HttpSession session = request.getSession();
        String fullName = (String) session.getAttribute("fullName");
        String email = (String) session.getAttribute("email");
        String accountNumber = (String) session.getAttribute("account");
        Double currentBalance = (Double) session.getAttribute("balance");

        if (accountNumber == null || currentBalance == null) {
            forwardWithAlert(request, response, "Error", "Session Got Expired", "error", "index.html");
            return;
        }

        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement updateBalanceStmt = null;
        PreparedStatement insertTransactionStmt = null;

        try {
            // Connect to the database
            conn = Database_Connector.connect();

            // Check if the account number exists and retrieve current balance
            checkStmt = conn.prepareStatement("SELECT balance FROM admin_dashboard WHERE account = ?");
            checkStmt.setString(1, accountNumber);
            ResultSet checkResult = checkStmt.executeQuery();

            if (!checkResult.next()) {
                forwardWithAlert(request, response, "Error", "Error: Account number does not exist!", "error", "C_Dashboard.html");
                return;
            }

            double balance = checkResult.getDouble("balance");

            // Deposit operation
            if (depositAmount > 0) {
                double newBalance = balance + depositAmount;

                // Update the balance in the database
                updateBalanceStmt = conn.prepareStatement("UPDATE admin_dashboard SET balance = ? WHERE account = ?");
                updateBalanceStmt.setDouble(1, newBalance);
                updateBalanceStmt.setString(2, accountNumber);
                updateBalanceStmt.executeUpdate();

                // Insert transaction history record for deposit
                insertTransactionStmt = conn.prepareStatement(
                        "INSERT INTO transaction_history (account_number, transaction_type, amount, balance, transaction_date) VALUES (?, 'Deposit', ?, ?, CURRENT_TIMESTAMP)"
                );
                insertTransactionStmt.setString(1, accountNumber);
                insertTransactionStmt.setDouble(2, depositAmount);
                insertTransactionStmt.setDouble(3, newBalance);
                insertTransactionStmt.executeUpdate();

                // Update the session attribute 'balance' with the new balance
                session.setAttribute("balance", newBalance);

                // Send deposit confirmation email
                Emailing.sendDepositConfirmationEmail(fullName, email, accountNumber, depositAmountStr, newBalance);

                // Display success message with user's name and account number
                forwardWithAlert(request, response, "Success", "Deposit successful!\nName: " + fullName + "\nAccount Number: " + accountNumber, "success", "C_Dashboard.html");
            } else {
                // Invalid deposit amount
                forwardWithAlert(request, response, "Error", "Invalid Deposit Amount!", "error", "C_Dashboard.html");
            }
        } catch (SQLException e) {
            handleSQLException(request, response, e);
        } catch (Exception e) {
            handleOtherException(request, response, e);
        } finally {
            // Close database resources
            try {
                if (checkStmt != null) {
                    checkStmt.close();
                }
                if (updateBalanceStmt != null) {
                    updateBalanceStmt.close();
                }
                if (insertTransactionStmt != null) {
                    insertTransactionStmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void forwardWithAlert(HttpServletRequest request, HttpServletResponse response, String title, String message, String icon, String redirectUrl) throws ServletException, IOException {
        request.setAttribute("title", title);
        request.setAttribute("message", message);
        request.setAttribute("icon", icon);
        request.setAttribute("redirectUrl", redirectUrl);
        RequestDispatcher dispatcher = request.getRequestDispatcher("responseHandler.jsp");
        dispatcher.forward(request, response);
    }

    private void handleSQLException(HttpServletRequest request, HttpServletResponse response, SQLException e) throws ServletException, IOException {
        e.printStackTrace();
        forwardWithAlert(request, response, "Error", "Database error: " + e.getMessage(), "error", "C_Dashboard.html");
    }

    private void handleOtherException(HttpServletRequest request, HttpServletResponse response, Exception e) throws ServletException, IOException {
        e.printStackTrace();
        forwardWithAlert(request, response, "Error", "Unexpected error: " + e.getMessage(), "error", "C_Dashboard.html");
    }
}
