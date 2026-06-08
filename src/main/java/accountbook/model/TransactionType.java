package accountbook.model;

// 타입 안전성을 위해 카테고리 enum 선언

public enum TransactionType {

    INCOME("수입"),
    EXPENSE("지출");

    private final String displayName;

    TransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
