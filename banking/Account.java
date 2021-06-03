package banking;

import java.text.DecimalFormat;
import java.util.Random;

public class Account {
    private String accountNumber;
    private String pin;
    private int balance;


    public Account () {
        Random random = new Random();
        String mii = "400000";
        String account = mii + account();
        String checksum = checkSum(account);

        this.accountNumber =account +checksum;
        this.pin = pin();

        this.balance = 0;
    }
    //Constructor
    public Account (String accountNumber, String pin) {

    }
    public String checkSum(String account) {
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
    public String pin() {
        Random r = new Random();
        String pin = "";
        for(int x = 0; x < 4; x++) {
            String add = String.valueOf(r.nextInt(10));
            pin = pin + add;
        }
        return pin;
    }
    public String account() {
        Random random = new Random();
        String account = "";
        for(int x = 0; x < 9; x++) {
            String add = String.valueOf(random.nextInt(10));
            account = account + add;
        }
        return account;
    }


    public int getBalance() {
        return this.balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    @Override
    public String toString() {
        return "Account number: " + accountNumber + ", pin: " + pin;
    }
}
