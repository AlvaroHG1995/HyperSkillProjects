package banking;

import java.util.ArrayList;
import java.util.Hashtable;

public class Bank {

    private Hashtable<String, Account> totalAccounts;

    public Bank (){
        this.totalAccounts = new Hashtable<>();
    }

    public Hashtable<String, Account> getTotalAccounts() {
        return totalAccounts;
    }

    public void addAccount(Account account) {

        this.totalAccounts.put(account.getAccountNumber(),account)  ;
    }

    public Account returnAccount(String accountNumber, String pin) {
        Account account = this.totalAccounts.get(accountNumber);
        return account;
    }
    public boolean logIn (String accountNumber, String pin) {
        boolean result = false;
        if(totalAccounts.containsKey(accountNumber)) {
            if (totalAccounts.get(accountNumber).equals(pin)) {
                result = true;
            }
        }

        return result;
    }
    public boolean checkAccount(Account account) {
        if(this.totalAccounts.containsKey(account.getAccountNumber())) {
            return true;
        } else {
            return false;
        }
    }
    public int size() {
        return this.totalAccounts.size();
    }
}
