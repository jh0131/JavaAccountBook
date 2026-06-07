package accountbook;

import java.time.LocalDate;

public class Expense extends Transaction {
    public Expense(LocalDate date, long amount, String category, String memo) {
        super(date, amount, category, memo);
    }

    @Override
    public TransactionType getType() {
        return TransactionType.EXPENSE;
    }
}
