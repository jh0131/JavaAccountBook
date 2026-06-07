package accountbook;

public enum IncomeCategory implements TransactionCategory {

    SALARY("월급"),
    ALLOWANCE("용돈");

    private final String displayName;

    IncomeCategory(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public TransactionType getType() {
        return TransactionType.INCOME;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String toString() {
        return displayName;
    }



    // 문자열로 저장되어 있던 카테고리 값을 다시 enum 값으로 바꾸는 변환 로직

    public static IncomeCategory from(String value) {
        for (IncomeCategory category : values()) {
            if (category.name().equals(value) || category.displayName.equals(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("알 수 없는 수입 카테고리입니다: " + value);
    }
}
