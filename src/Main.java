import data.DataManager;
import transactions.TransactionsParser;

public class Main {

    public static void main(String[] args) {
        try {
            TransactionsParser.getTransactions("transactions.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }

        DataManager dataManager = new DataManager(5);
        dataManager.startCoreLayer();
        dataManager.sendWrite(2, 10);
        dataManager.shutdown();
    }
}
