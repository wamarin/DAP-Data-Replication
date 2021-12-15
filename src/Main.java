import data.DataManager;
import transactions.TransactionsParser;

public class Main {

    public static void main(String[] args) {
        try {
            TransactionsParser.getTransactions("transactions.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }

        DataManager dataManager = new DataManager(100);
        new Thread(dataManager).start();
        dataManager.startLayers();

        for (int i = 0; i < 100; i++) {
            dataManager.sendWrite(i, i);
            dataManager.sendRead(i, i % 3);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dataManager.shutdown();
    }
}
