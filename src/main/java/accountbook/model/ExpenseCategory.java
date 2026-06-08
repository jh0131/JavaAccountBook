package accountbook.model;

// 타입 안전성을 위해 카테고리 enum 선언

public enum ExpenseCategory implements TransactionCategory {

    FOOD("식비"),
    TRANSPORT("교통"),
    SHOPPING("쇼핑"),
    SUBSCRIPTION("구독"),
    ETC("기타");

    private final String displayName;

    ExpenseCategory(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public TransactionType getType() {
        return TransactionType.EXPENSE;
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

    public static ExpenseCategory from(String value) {
        for (ExpenseCategory category : values()) {
            if (category.name().equals(value) || category.displayName.equals(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("알 수 없는 지출 카테고리입니다: " + value);
    }
}
