package banking;


import java.sql.*;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Bank bank = new Bank();
        //String url = "jdbc:sqlite:C:card.s3db";
        String url = "jdbc:sqlite:C:\\Users\\alvar\\IdeaProjects\\Simple Banking System\\Simple Banking System\\task\\card.s3db";
        //String acc = "4000001168473544";
        createNewTable(url);
        System.out.println();


        //OPTIONS:
        int end = 0;


        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("1. Create an account");
            System.out.println("2. Log into account");
            System.out.println("0. Exit");

            int command = scanner.nextInt();
            System.out.println();

            switch (command) {
                case 1:
                    int accountEnd = 0;
                    String accountN = "";
                    String accountPin = "";

                    while (accountEnd == 0) {
                        Account account = new Account();
                        boolean result = checkTable(url,"card",account.getAccountNumber());


                        if(result == false) {
                            accountEnd = 1;
                            bank.addAccount(account);
                            accountN = account.getAccountNumber();
                            accountPin = account.getPin();

                        }
                    }
                    Account account = bank.returnAccount(accountN,accountPin);
                    addToTable(url,"card",account);
                    System.out.println("Your card number:");
                    System.out.println(account.getAccountNumber());
                    System.out.println("Your card PIN:");
                    System.out.println(account.getPin());
                    System.out.println();
                    break;
                case 2:
                    System.out.println("Enter your card number:");
                    Scanner sc = new Scanner(System.in);
                    String cardNumber = sc.nextLine();
                    System.out.println("Enter your PIN:");
                    String pin = sc.nextLine();
                    boolean result = logIn(url,"card", cardNumber,pin);
                    System.out.println();

                    if (result) {
                        System.out.println("You have successfully logged in!");
                        login(url,cardNumber);
                    } else {
                        System.out.println("Wrong card number of PIN!");
                    }
                    break;
                case 0:
                    System.out.println("Bye!");
                    System.exit(1);
                    break;
                default:
                    System.out.println("Wrong input!");
                    break;

            }
        }




    }

    public static void login (String url, String accountNumber) {
        System.out.println();
        int end = 0;
        while (end == 0) {


            System.out.println("1. Balance");
            System.out.println("2. Add income");
            System.out.println("3. Do transfer");
            System.out.println("4. Close account");
            System.out.println("5. Log out");
            System.out.println("0. Exit");
            Scanner sc = new Scanner(System.in);
            int command = sc.nextInt();
            System.out.println();


            switch (command) {
                case 1:
                    System.out.print("Balance: ");
                    Integer balance = number(url, accountNumber);
                    System.out.println(balance);
                    System.out.println();
                    break;
                case 2:
                    System.out.println("Enter income:");
                    Scanner inc = new Scanner(System.in);
                    int income = inc.nextInt();
                    addIncome(url, accountNumber,"card",income);
                    System.out.println();
                    break;
                case 3:
                    System.out.println("Transfer");
                    System.out.println("Enter card number:");
                    Scanner cN = new Scanner(System.in);
                    String accountNumber2 = cN.next();
                    String checkSum = checkSum(accountNumber2.substring(0,accountNumber2.length()-1));
                    boolean resultCheck = checkSum.equals(accountNumber2.substring(accountNumber2.length()-1));
                    //System.out.println("check =" + resultCheck);

                    if(resultCheck && !checkTable(url,"card",accountNumber2)) {
                        System.out.println("Such a card does not exist.");
                    } else if (!resultCheck) {
                        System.out.println("Probably you made a mistake in the card number. Please try again!");
                    } else {
                        System.out.println("Enter how much money you want to transfer:");
                        Scanner money = new Scanner(System.in);
                        int transfer = money.nextInt();
                        int moneyAvailable = number(url, accountNumber);
                        System.out.println("MoneyAvailable = " + moneyAvailable);
                        System.out.println("Transfer = " + transfer);
                        if(moneyAvailable < transfer) {
                            System.out.println("Not enough money!");
                        } else {
                            transfer(url,accountNumber,accountNumber2,"card", transfer);
                            System.out.println("Success!");
                        }
                    }
                    System.out.println();
                    break;
                case 4:
                    deleteAccount(url, accountNumber, "card");
                    System.out.println("The account has been closed!");
                    System.out.println();
                    end++;
                    break;
                case 5:
                    System.out.println("You have successfully logged out!");
                    System.out.println();
                    end++;
                    break;
                case 0:
                    System.out.println("Bye!");
                    System.exit(1);
                    break;
                default:
                    System.out.println("Wrong input!");
                    break;
            }
        }
    }

    public static String checkSum(String account) {
        String check ="";
        String d = account;
        Integer[] ints = new Integer[d.length()];

        for(int x = 0; x < d.length(); x++) {
            ints[x] = (int) d.charAt(x) - 48;
        }
        //Multiply even indexes by 2:
        for(int x = 0; x < ints.length; x++) {
            if(x % 2 == 0) {
                ints[x] *=2;
            }
        }
        //Subtract 9 to numbers over 9 and sum:
        int sum = 0;
        for(int x = 0; x < ints.length; x++) {
            if(ints[x] > 9) {
                ints[x] -=9;
            }
            sum += ints[x];
        }
        int checkSum = 10 - (sum % 10);
        if(checkSum == 10) {
            checkSum = 0;
        }
        check = String.valueOf(checkSum);


        return check;
    }

    //-------------------------------- SQL methods:

    //Recuperar valor(PRUEBA):
    public static Integer  number (String url, String accountNumber) {
        Integer result = 0;
        String query = "SELECT balance FROM card WHERE number = " + accountNumber;
        try(Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);

            result = rs.getInt("balance");

        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

        return result;
    }

    //PRUEBA:
    public static void deleteAccount(String url, String accountNumber, String tableName) {
        String query = "DELETE FROM " + tableName + " WHERE number = ? ";
        try (Connection conn = DriverManager.getConnection(url);
        PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1,accountNumber);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void addIncome(String url, String accountNumber, String tableName, int income) {


        try(Connection conn = DriverManager.getConnection(url)) {
            String query = "UPDATE " + tableName + " SET balance = balance + ? WHERE number = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1,income);
            preparedStatement.setString(2,accountNumber);
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
    public static void transfer(String url, String accountNumber1, String accountNumber2, String tableName, int transfer) {



        try(Connection conn = DriverManager.getConnection(url)) {
            String query = "UPDATE " + tableName + " SET balance = balance - ? WHERE number = ?";
            String query2 = "UPDATE " + tableName + " SET balance = balance + ? WHERE number = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            PreparedStatement preparedStatement2 = conn.prepareStatement(query2);
            preparedStatement.setInt(1,transfer);
            preparedStatement.setString(2,accountNumber1);
            preparedStatement.execute();
            preparedStatement2.setInt(1, transfer);
            preparedStatement2.setString(2,accountNumber2);
            preparedStatement2.execute();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }





    public static void connect(String url) {
        Connection conn = null;
        try {
            // db parameters
            // create a connection to the database
            conn = DriverManager.getConnection(url);

            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public static void createNewTable(String url) {
        // SQLite connection string

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS card (\n"
                + "	id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "	number TEXT,\n"
                + "pin TEXT,\n"
                + " balance INTEGER DEFAULT 0\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void addToTable(String url,String name, Account account) {
        String accountNumber = account.getAccountNumber();
        String accountPin = account.getPin();
        int balance = account.getBalance();


        try (Connection conn = DriverManager.getConnection(url)) {
            String query = "INSERT INTO " + name + " (number,pin,balance)" + " VALUES (?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            //
            preparedStatement.setString(1,accountNumber);
            preparedStatement.setString(2,accountPin);
            preparedStatement.setInt(3,balance);

            preparedStatement.execute();

            //conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
    public static boolean checkTable(String url, String name, String accountNumber) {
        boolean result = false;

        try(Connection conn = DriverManager.getConnection(url);
        Statement stmt = conn.createStatement()) {
            String check = "SELECT number FROM " + name + " Where number = " + accountNumber;
            ResultSet rs = stmt.executeQuery(check);

            result = rs.next();

        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }
    public static boolean logIn(String url, String name, String accountNumber, String pin) {
        boolean result = false;

        try(Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement()) {
            String check = "SELECT number, pin FROM " + name + " WHERE number = " + accountNumber + " AND pin = " + pin;
            ResultSet rs = stmt.executeQuery(check);

            result = rs.next();


        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

}