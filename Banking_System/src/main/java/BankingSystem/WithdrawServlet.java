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

@WebServlet("/WithdrawServlet")
public class WithdrawServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve form data
        String withdrawAmountStr = request.getParameter("withdrawAmount");
        double withdrawAmount;

        // Validate the withdrawal amount
        try {
            withdrawAmount = Double.parseDouble(withdrawAmountStr);
        } catch (NumberFormatException e) {
            forwardWithAlert(request, response, "Error", "Invalid withdrawal amount!", "error", "C_Dashboard.html");
            return;
        }

        // Retrieve user details from session
        HttpSession session = request.getSession();
        String fullName = (String) session.getAttribute("fullName");
        String email = (String) session.getAttribute("email");
        String accountNumber = (String) session.getAttribute("account");
        Double currentBalance = (Double) session.getAttribute("balance");

        if (accountNumber == null || currentBalance == null) {
            forwardWithAlert(request, response, "Error", "Session expired", "error", "index.html");
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
                forwardWithAlert(request, response, "Error", "Account number does not exist!", "error", "C_Dashboard.html");
                return;
            }

            double balance = checkResult.getDouble("balance");

            // Check if withdrawal amount is valid
            if (withdrawAmount > 0 && withdrawAmount <= balance) {
                // Deduct withdrawal amount from the balance
                double newBalance = balance - withdrawAmount;

                // Update the balance in the database
                updateBalanceStmt = conn.prepareStatement("UPDATE admin_dashboard SET balance = ? WHERE account = ?");
                updateBalanceStmt.setDouble(1, newBalance);
                updateBalanceStmt.setString(2, accountNumber);
                updateBalanceStmt.executeUpdate();

                // Insert transaction history record for withdrawal
                insertTransactionStmt = conn.prepareStatement(
                        "INSERT INTO transaction_history (account_number, transaction_type, amount, balance, transaction_date) VALUES (?, 'Withdrawal', ?, ?, CURRENT_TIMESTAMP)"
                );
                insertTransactionStmt.setString(1, accountNumber);
                insertTransactionStmt.setDouble(2, withdrawAmount);
                insertTransactionStmt.setDouble(3, newBalance);
                insertTransactionStmt.executeUpdate();

                // Update the session attribute 'balance' with the new balance
                session.setAttribute("balance", newBalance);

                // Send withdrawal confirmation email
                Emailing.sendWithdrawalConfirmationEmail(fullName, email, accountNumber, withdrawAmountStr, newBalance);

                // Display success message with user's name and account number
                forwardWithAlert(request, response, "Success", "Withdrawal successful!\nName: " + fullName + "\nAccount Number: " + accountNumber, "success", "C_Dashboard.html");
            } else {
                // Invalid withdrawal amount
                forwardWithAlert(request, response, "Error", "Insufficient balance or invalid amount!", "error", "C_Dashboard.html");
            }
        } catch (SQLException e) {
            handleSQLException(request, response, e);
        } catch (Exception e) {
            handleOtherException(request, response, e);
        } finally {
            // Close database resources
            try {
                if (checkStmt != null) checkStmt.close();
                if (updateBalanceStmt != null) updateBalanceStmt.close();
                if (insertTransactionStmt != null) insertTransactionStmt.close();
                if (conn != null) conn.close();
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
        forwardWithAlert(request, response, "Error", "Database error: " + e.getMessage(), "error", "index.html");
    }

    private void handleOtherException(HttpServletRequest request, HttpServletResponse response, Exception e) throws ServletException, IOException {
        e.printStackTrace();
        forwardWithAlert(request, response, "Error", "Unexpected error: " + e.getMessage(), "error", "index.html");
    }
}
