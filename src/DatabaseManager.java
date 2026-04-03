package src;

import java.sql.*;

public class DatabaseManager{
    private static final String URL ="jdbc:sqlite:bank.db";

    public static Connection connect(){
        try{
            return DriverManager.getConnection(URL);
        }
        catch(SQLException e){
            System.out.println("Bağlantı Hatası: "+ e.getMessage());
            return null;
        }
    }


public static void initialize(){
    String sql = "CREATE TABLE IF NOT EXISTS accounts ("
                + "id TEXT PRIMARY KEY,"
                + "balance REAL NOT NULL,"
                + "type TEXT NOT NULL"
                + ");";

    try(Connection conn = connect();
        Statement stmt = conn.createStatement()){
            
            
            stmt.execute(sql);
            System.out.println("SQLite: Tablo oluşturuldu.");
            
        }
    catch(SQLException e){
        System.out.println("Tablo oluşturma hatası: "+e.getMessage());

    }

}

public static void addAccount (BankAccount acc){
    String sql ="INSERT INTO accounts(id,balance,type) VALUES (?,?,?)";

    try(Connection conn = connect();
        PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1,acc.id);
            pstmt.setFloat(2,acc.balance);

            String type = (acc instanceof FXAccount) ? "FXAccount" : "CheckingAccount";
            pstmt.setString(3, type);

            pstmt.executeUpdate();
            System.out.println("SQLite: Yeni hesap başarıyla kaydedildi -> ID: "+acc.id);
        }
    catch(SQLException e){
        System.out.println("Veritabanına ekleme hatası: "+e.getMessage());
    }
}


    public static java.util.HashMap<String, BankAccount> loadAccounts() {
        java.util.HashMap<String, BankAccount> loadedData = new java.util.HashMap<>();

        String sql = "SELECT id, balance, type FROM accounts";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String id = rs.getString("id");
                float balance = rs.getFloat("balance");
                String type = rs.getString("type");

                BankAccount acc;

                if (type.equals("FXAccount")) {
                    acc = new FXAccount(id);
                } else {
                    acc = new CheckingAccount(id, 500f); 
                }
                

                acc.balance = balance; 

                loadedData.put(id, acc);
            }
            System.out.println("SQLite: Veriler başarıyla yüklendi! Toplam Hesap: " + loadedData.size());

        } catch (SQLException e) {
            System.out.println("Veri yükleme hatası: " + e.getMessage());
        }
        
        return loadedData;
    }


}
