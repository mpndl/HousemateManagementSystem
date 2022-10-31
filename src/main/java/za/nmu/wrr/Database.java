package za.nmu.wrr;

import java.sql.*;

public class Database {
    private Statement stmt;
    private Connection conn;
    public Database() {
        connectToDB();
    }

    public ResultSet executeQuery(String sql) {
        try {
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void executeUpdate(String sql) {
        try {
            stmt.executeUpdate(sql);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int executeInsert(String sql) {
        try {
            stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if(generatedKeys.next())
                return generatedKeys.getInt(1);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getQueryRowCount(String query) throws SQLException {
        try (Statement statement = conn.createStatement();
             ResultSet standardRS = statement.executeQuery(query)) {
            int size = 0;
            while (standardRS.next()) {
                size++;
            }
            return size;
        }
    }

    private void connectToDB() {
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:jtds:sqlserver://localhost:1433/HMS4;instance=MSSQLSERVER");
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnectFromDB() {

        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
