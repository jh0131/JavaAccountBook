package accountbook.model;

// 수입 카테고리 enum과 지출 카테고리 enum을 같은 방식으로 다루기 위해
// 수입 카테고리와 지출 카테고리의 공통 기능을 추상화한 인터페이스 (추상화 객체지향 원칙)

public interface TransactionCategory {

    TransactionType getType();

    String getDisplayName();

    String getCode();
}
