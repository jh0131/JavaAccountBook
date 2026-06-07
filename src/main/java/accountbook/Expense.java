package accountbook;

import java.time.LocalDate;

// 지출 거래 객체를 만들 때 사용하고, 입력받은 값을 부모 Transaction에 저장하고, getType()을 호출하면 EXPENSE를 반환

public class Expense extends Transaction {

    public Expense(LocalDate date, long amount, ExpenseCategory category, String memo) {
        super(date, amount, category, memo);
    }

    @Override
    public TransactionType getType() {
        return TransactionType.EXPENSE;
    }
}
