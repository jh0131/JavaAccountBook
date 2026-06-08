package accountbook.model;

import java.time.LocalDate;

public abstract class Transaction {

    /* 객체 지향 원칙 캡슐화를 위해서 필드를 private 선언하고, get-, set- 메서드는 public으로 제한.
       이를 통해서 값을 설정하고 조회함. */

    private LocalDate date;
    private long amount;
    private TransactionCategory category;
    private String memo;



    public Transaction(LocalDate date, long amount, TransactionCategory category, String memo) {
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.memo = memo;
    }



    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public TransactionCategory getCategory() {
        return category;
    }

    public void setCategory(TransactionCategory category) {
        this.category = category;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }


    public abstract TransactionType getType();

    public long getSignedAmount() {

        if (getType() == TransactionType.INCOME) {
            return amount;
        }
        return -amount;
    }
}
