import transactions.TransactionsParser;

public class Main {

    public static void main(String[] args) {
        try {
            TransactionsParser.getTransactions("transactions.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
