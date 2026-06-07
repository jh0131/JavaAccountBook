package accountbook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AccountBook {

    private final ArrayList<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public void removeTransaction(int index) {
        if (index < 0 || index >= transactions.size()) {
            throw new IndexOutOfBoundsException("삭제할 거래 내역을 찾을 수 없습니다.");
        }
        transactions.remove(index);
    }

    public void setTransaction(int index, Transaction transaction) {
        if (index < 0 || index >= transactions.size()) {
            throw new IndexOutOfBoundsException("수정할 거래 내역을 찾을 수 없습니다.");
        }
        transactions.set(index, transaction);
    }

    public void setTransactions(List<Transaction> newTransactions) {
        transactions.clear();
        transactions.addAll(newTransactions);
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public long getTotalIncome() {
        long total = 0;
        for (Transaction transaction : transactions) {
            if (transaction.getType() == TransactionType.INCOME) {
                total += transaction.getAmount();
            }
        }
        return total;
    }

    public long getTotalExpense() {
        long total = 0;
        for (Transaction transaction : transactions) {
            if (transaction.getType() == TransactionType.EXPENSE) {
                total += transaction.getAmount();
            }
        }
        return total;
    }

    public long getBalance() {
        return getTotalIncome() - getTotalExpense();
    }
}
