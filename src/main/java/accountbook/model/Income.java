package accountbook.model;

import java.time.LocalDate;

// 수입 거래 객체를 만들 때 사용하고, 입력받은 값을 부모 Transaction에 저장하고, getType()을 호출하면 INCOME을 반환

public class Income extends Transaction {

    public Income(LocalDate date, long amount, IncomeCategory category, String memo) {
        super(date, amount, category, memo);
    }

    @Override
    public TransactionType getType() {
        return TransactionType.INCOME;
    }
}
