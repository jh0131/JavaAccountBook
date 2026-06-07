# Java Swing 가계부 프로그램

Java Swing으로 만든 간단한 가계부 프로그램입니다.  
사용자가 수입/지출 내역을 입력하면 거래 목록에 추가되고, 총수입, 총지출, 잔액이 자동으로 계산됩니다.

## 전체 흐름

Main 실행
↓
AccountBookFrame 화면 생성
↓
사용자가 날짜, 구분, 카테고리, 금액, 메모 입력
↓
Income 또는 Expense 객체 생성
↓
AccountBook의 ArrayList에 저장
↓
JTable 갱신
↓
총수입, 총지출, 잔액 다시 계산

저장 버튼을 누르면 현재 거래 목록이 `accountbook-data.txt` 파일에 저장됩니다.  
프로그램을 다시 실행하면 저장 파일을 자동으로 읽어와 거래 목록을 복원합니다.

## 클래스 역할

Main
→ 프로그램 시작점

AccountBookFrame
→ Swing 화면 구성, 입력 처리, 버튼 이벤트 담당

AccountBookStyle
→ 버튼, 라벨, 입력창, 테이블 등 UI 스타일 담당

Transaction
→ 수입과 지출이 공통으로 가지는 거래 정보 담당

Income
→ 수입 거래 객체

Expense
→ 지출 거래 객체

TransactionType
→ 수입/지출 구분 enum

TransactionCategory
→ 수입/지출 카테고리 enum의 공통 인터페이스

IncomeCategory
→ 수입 카테고리 enum

ExpenseCategory
→ 지출 카테고리 enum

AccountBook
→ 거래 추가, 삭제, 수정, 조회, 합계 계산 담당

TransactionTableModel
→ 거래 목록을 JTable에 표시하기 위한 테이블 모델

AccountBookFileManager
→ 파일 저장과 불러오기 담당

## 핵심 데이터 구조

거래 1건의 공통 정보는 `Transaction` 클래스가 가지고 있습니다.


private LocalDate date;
private long amount;
private TransactionCategory category;
private String memo;


`Transaction`은 추상 클래스이고, 실제 객체는 `Income` 또는 `Expense`로 생성됩니다.

public abstract TransactionType getType();


Income은 getType()에서 TransactionType.INCOME을 반환하고,  
Expense는 getType()에서 TransactionType.EXPENSE를 반환합니다.

## 카테고리 enum

수입과 지출은 사용할 수 있는 카테고리가 다르기 때문에 enum을 따로 만들었습니다.


IncomeCategory
→ 월급, 용돈

ExpenseCategory
→ 식비, 교통, 쇼핑, 구독, 기타


두 enum은 공통으로 TransactionCategory 인터페이스를 구현합니다.


public interface TransactionCategory {
    TransactionType getType();
    String getDisplayName();
    String getCode();
}


getDisplayName()은 화면에 보여줄 한글 이름입니다.  
getCode()는 파일에 저장할 enum 코드입니다.

예를 들어 IncomeCategory.SALARY는 화면에는 `월급`으로 보이고, 파일에는 SALARY로 저장됩니다.

from(String value) 메서드는 파일에서 읽은 문자열을 enum으로 다시 바꾸는 역할입니다.  
기존 저장 파일처럼 월급, 식비가 들어 있어도 읽을 수 있게 처리했습니다.

## AccountBook 핵심 로직

AccountBook은 가계부 기능의 핵심 로직 클래스입니다.  
거래 목록은 ArrayList<Transaction>으로 관리합니다.

private final ArrayList<Transaction> transactions = new ArrayList<>();

Income과 Expense는 둘 다 Transaction을 상속하므로 같은 리스트에 함께 저장할 수 있습니다.

주요 메서드는 다음과 같습니다.

addTransaction()
→ 생성된 거래 객체를 ArrayList에 추가

removeTransaction()
→ 선택한 위치의 거래를 ArrayList에서 삭제

setTransaction()
→ 선택한 위치의 거래를 새 거래 객체로 교체

setTransactions()
→ 파일에서 불러온 거래 목록으로 전체 교체

getTransactions()
→ 거래 목록 조회, 외부 직접 수정을 막기 위해 읽기 전용 리스트 반환

getTotalIncome()
→ 수입 거래만 골라 금액 합산

getTotalExpense()
→ 지출 거래만 골라 금액 합산

getBalance()
→ 총수입 - 총지출 계산

총지출 계산 예시는 다음과 같습니다.

public long getTotalExpense() {
    long total = 0;
    for (Transaction transaction : transactions) {
        if (transaction.getType() == TransactionType.EXPENSE) {
            total += transaction.getAmount();
        }
    }
    return total;
}

향상된 for문으로 거래 목록을 하나씩 확인하고, 지출인 경우에만 금액을 더합니다.

## 화면 처리

AccountBookFrame은 Swing 화면과 버튼 이벤트를 담당합니다.

추가 버튼
→ 입력값을 읽고 Income 또는 Expense 객체 생성
→ AccountBook.addTransaction() 호출

수정 버튼
→ 선택한 거래 정보를 팝업에 표시
→ 새 Income 또는 Expense 객체 생성
→ AccountBook.setTransaction() 호출

삭제 버튼
→ 선택한 거래 index 확인
→ AccountBook.removeTransaction() 호출

저장 버튼
→ AccountBookFileManager.save() 호출


구분이 수입이면 IncomeCategory.values()를 카테고리 콤보박스에 넣고,  
구분이 지출이면 ExpenseCategory.values()를 넣습니다.

## 파일 저장

AccountBookFileManager는 거래 목록을 텍스트 파일로 저장하고 다시 불러옵니다.

저장 형식은 다음과 같습니다.

거래유형    날짜    금액    카테고리코드    메모

예시:
INCOME	2026-06-02	50000	SALARY	6월 월급
EXPENSE	2026-06-02	12000	FOOD	점심

불러올 때는 첫 번째 값이 INCOME이면 Income 객체를 만들고,  
EXPENSE이면 Expense 객체를 만듭니다.