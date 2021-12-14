import transactions.TransactionManager;

public class Main {

    public static void main(String[] args) {
        try {
            TransactionManager.getTransactions("transactions.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
