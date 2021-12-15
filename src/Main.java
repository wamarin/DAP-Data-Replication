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
        new Thread(dataManager).start();
        dataManager.startCoreLayer();
        dataManager.sendRead(2);
        dataManager.sendWrite(2, 10);
        dataManager.sendRead(2);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dataManager.shutdown();
    }
}
