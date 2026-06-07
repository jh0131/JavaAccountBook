package accountbook;

// 수입 카테고리 enum과 지출 카테고리 enum을 같은 방식으로 다루기 위해 만든 인터페이스

public interface TransactionCategory {

    TransactionType getType();

    String getDisplayName();

    String getCode();
}
