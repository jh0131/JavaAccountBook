# Java Swing 가계부 프로그램 (2026.5)

Java Swing으로 만든 간단한 가계부 프로그램입니다.
생성형 AI를 통하여, 기본 코드를 짜고 추후에 코드를 검토하면서 리팩토링한 프로젝트입니다.

전체적인 기능은 사용자가 수입/지출 내역을 입력하면 거래 목록에 추가되고, 총수입, 총지출, 잔액이 자동으로 계산됩니다.

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

저장 버튼을 누르면 현재 거래 목록이 accountbook-data.txt 파일에 저장됩니다.  
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

거래 1건의 공통 정보는 Transaction 클래스가 가지고 있습니다.


private LocalDate date;
private long amount;
private TransactionCategory category;
private String memo;

Transaction은 추상 클래스이고, 실제 객체는 Income 또는 Expense로 생성됩니다.

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

## 직접 개선한 부분

기존에는 카테고리를 단순 문자열처럼 처리할 수 있었지만, 객체지향과 타입 안전성을 살리기 위해 카테고리를 enum 타입으로 선언했습니다.

IncomeCategory는 수입 카테고리를 담당하고, ExpenseCategory는 지출 카테고리를 담당합니다.  
enum을 사용하면 정해진 카테고리 값만 사용할 수 있기 때문에 오타나 잘못된 값이 들어가는 문제를 줄일 수 있습니다.  
예를 들어 "식비", "food", "FOOD" 같은 문자열이 섞이는 대신 ExpenseCategory.FOOD처럼 정해진 값으로 관리할 수 있습니다.

또한 IncomeCategory와 ExpenseCategory가 공통으로 가져야 하는 기능은 TransactionCategory 인터페이스로 분리했습니다.

public interface TransactionCategory {
    TransactionType getType();
    String getDisplayName();
    String getCode();
}

이 인터페이스를 통해 수입 카테고리와 지출 카테고리를 각각 따로 만들면서도, 프로그램 내부에서는 TransactionCategory라는 공통 타입으로 다룰 수 있습니다.  
이 부분은 객체지향의 추상화와 다형성을 활용한 부분입니다.

그리고 파일의 역할을 더 명확하게 구분하기 위해 패키지 구조를 나누었습니다.

accountbook.model
→ Transaction, Income, Expense, Category enum 등 데이터 모델 담당

accountbook.service
→ AccountBook처럼 핵심 기능과 계산 로직 담당

accountbook.view
→ AccountBookFrame, AccountBookStyle처럼 화면 구성과 스타일 담당

accountbook.table
→ TransactionTableModel처럼 JTable 표시 방식 담당

accountbook.file
→ AccountBookFileManager처럼 파일 저장과 불러오기 담당

이렇게 패키지를 분리하면 각 클래스가 어떤 역할을 하는지 더 쉽게 파악할 수 있고, 유지보수하기도 좋아집니다.

마지막으로 Transaction 클래스에서는 필드를 private로 선언하고, 외부에서는 public getter/setter 메서드를 통해 접근하도록 만들었습니다.

private LocalDate date;
private long amount;
private TransactionCategory category;
private String memo;

이 방식은 객체 내부 데이터를 직접 접근하지 못하게 막고, 필요한 메서드를 통해서만 값을 읽거나 수정하게 하는 캡슐화입니다.  
캡슐화를 사용하면 객체의 데이터를 더 안전하게 관리할 수 있고, 클래스 내부 구현이 바뀌어도 외부 코드에 주는 영향을 줄일 수 있습니다.
