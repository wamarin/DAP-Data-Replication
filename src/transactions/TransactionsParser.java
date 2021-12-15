package transactions;

import operations.Operation;
import operations.ReadOperation;
import operations.WriteOperation;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TransactionsParser {
    private static final Pattern readOperationMatchPattern = Pattern.compile("r\\(([0-9]+)\\)");
    private static final Pattern writeOperationMatchPattern = Pattern.compile("w\\(([0-9]+),([0-9]+)\\)");
    private static final Pattern numberMatchPattern = Pattern.compile("[0-9]+");

    public static List<Transaction> getTransactions(String transactionsFile) throws Exception {
        return parseTransactions(transactionsFile);
    }

    private static List<Transaction> parseTransactions(String transactionsFile) throws Exception {
        List<Transaction> transactions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(transactionsFile))) {
            String line;
            while((line = reader.readLine()) != null) {
                if (checkTransaction(line)){
                    Transaction transaction = new Transaction(isReadOnly(line));

                    if (isReadOnly(line)) {
                        System.out.println("Read only: " + line);
                        transaction.setLayer(getLayer(line));
                    } else {
                        System.out.println("Write: " + line);
                    }

                    transaction.setOperations(getOperations(line));

                    transactions.add(transaction);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        clearFile(transactionsFile);

        return transactions;
    }

    private static void clearFile(String transactionsFile) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(transactionsFile))) {
            writer.print("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int getLayer(String transaction) {
        Matcher numberMatcher;
        int target = 0;

        numberMatcher = numberMatchPattern.matcher(transaction);
        if (numberMatcher.find())
            target =  Integer.parseInt(numberMatcher.group());

        return target;
    }

    private static List<Operation> getOperations(String transaction) {
        List<Operation> operations = new ArrayList<>();

        operations.addAll(getReadOperations(transaction));
        operations.addAll(getWriteOperations(transaction));

        return operations;
    }

    private static ReadOperation parseReadOperation(String readOperationString) {
        Matcher numberMatcher;

        numberMatcher = numberMatchPattern.matcher(readOperationString);
        if (numberMatcher.find())
            return new ReadOperation(Integer.parseInt(numberMatcher.group()));

        return null;
    }

    private static List<Operation> getReadOperations(String transaction) {
        List<Operation> readOperations = new ArrayList<>();
        Matcher readOperationMatcher = readOperationMatchPattern.matcher(transaction);
        String readOperationString;

        while(readOperationMatcher.find()) {
            readOperationString = readOperationMatcher.group();
            readOperations.add(parseReadOperation(readOperationString));
        }

        return readOperations;
    }

    private static WriteOperation parseWriteOperation(String writeOperationString) {
        Matcher numberMatcher;
        int target = 0, value = 0;

        numberMatcher = numberMatchPattern.matcher(writeOperationString);
        if (numberMatcher.find())
            target = Integer.parseInt(numberMatcher.group());
        if (numberMatcher.find())
            value = Integer.parseInt(numberMatcher.group());

        return new WriteOperation(target, value);
    }

    private static List<Operation> getWriteOperations(String transaction) {
        List<Operation> writeOperations = new ArrayList<>();
        Matcher readOperationMatcher = writeOperationMatchPattern.matcher(transaction);
        String writeOperationString;

        while(readOperationMatcher.find()) {
            writeOperationString = readOperationMatcher.group();
            writeOperations.add(parseWriteOperation(writeOperationString));
        }

        return writeOperations;
    }

    private static boolean isReadOnly(String transaction) {
        return transaction.matches("^b(<[0-9]+>).*$");
    }

    private static boolean checkTransaction(String transaction) throws Exception {
        if (!transaction.matches("^b(<[0-9]+>)?(, *r\\([0-9]+\\)|, *(w\\([0-9]+, *[0-9]+\\)))*, *c$"))
            throw new Exception("Error parsing the transaction: " + transaction);
        return true;
    }
}
