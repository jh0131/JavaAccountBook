package accountbook;

import java.time.LocalDate;

public class Income extends Transaction {
    public Income(LocalDate date, long amount, String category, String memo) {
        super(date, amount, category, memo);
    }

    @Override
    public TransactionType getType() {
        return TransactionType.INCOME;
    }
}
