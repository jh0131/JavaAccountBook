# Java Swing 가계부 프로그램 코드 이해용 README

이 문서는 제출용 보고서가 아니라, 현재 프로젝트 코드를 이해하기 쉽게 정리한 설명서입니다.

이 프로젝트는 **Java + Swing GUI + 파일 저장**으로 만든 간단한 가계부 프로그램입니다. Spring, DB, 외부 라이브러리는 사용하지 않았습니다.

## 1. 전체 흐름

프로그램의 기본 흐름은 다음과 같습니다.

```text
Main
↓
AccountBookFrame 실행
↓
사용자가 날짜, 구분, 카테고리, 금액, 메모 입력
↓
Income 또는 Expense 객체 생성
↓
AccountBook에 저장
↓
JTable 화면 갱신
↓
총 수입, 총 지출, 잔액 다시 계산
```

파일 저장은 저장 버튼을 눌렀을 때 실행됩니다.

```text
저장 버튼 클릭
↓
AccountBookFrame의 saveData() 실행
↓
AccountBookFileManager의 save() 실행
↓
accountbook-data.txt 파일에 저장
```

불러오기는 버튼이 따로 없고, 프로그램 시작 시 자동으로 실행됩니다.

```text
프로그램 시작
↓
AccountBookFrame 생성
↓
loadInitialData() 실행
↓
accountbook-data.txt 파일이 있으면 loadData() 실행
↓
저장된 거래 내역을 화면에 표시
```

## 2. 코드를 읽는 추천 순서

처음 볼 때는 아래 순서대로 읽는 것이 좋습니다.

1. `Main.java`
2. `AccountBookFrame.java`
3. `AccountBookStyle.java`
4. `Transaction.java`
5. `Income.java`, `Expense.java`
6. `TransactionType.java`
7. `AccountBook.java`
8. `TransactionTableModel.java`
9. `AccountBookFileManager.java`

먼저 실행 흐름을 보고, 그다음 화면 코드, 데이터 구조, 저장 방식을 보는 순서입니다.

## 3. 파일 구조

```text
JavaAccountBookSwing
├─ README.md
├─ src
│  └─ main
│     └─ java
│        └─ accountbook
│           ├─ Main.java
│           ├─ AccountBookFrame.java
│           ├─ AccountBookStyle.java
│           ├─ AccountBook.java
│           ├─ AccountBookFileManager.java
│           ├─ Transaction.java
│           ├─ Income.java
│           ├─ Expense.java
│           ├─ TransactionType.java
│           └─ TransactionTableModel.java
└─ accountbook-data.txt
```

`accountbook-data.txt`는 저장 버튼을 누르면 생성되는 저장 파일입니다.

## 4. 클래스 역할 요약

```text
Main
→ 프로그램 시작 담당

AccountBookFrame
→ Swing 화면 배치, 입력 처리, 버튼 이벤트 담당

AccountBookStyle
→ 색상, 폰트, 버튼, 표, 카드 꾸미기 담당

Transaction
→ 거래 1개의 공통 정보 담당

Income
→ 수입 거래 1개 담당

Expense
→ 지출 거래 1개 담당

TransactionType
→ 수입/지출 구분 값 담당

AccountBook
→ 거래 목록 저장, 추가, 삭제, 수정, 합계 계산 담당

TransactionTableModel
→ AccountBook의 거래 목록을 JTable에 표시하는 형식 담당

AccountBookFileManager
→ 파일 저장, 파일 불러오기 담당
```

## 5. `Main.java`

`Main`은 프로그램 시작점입니다.

```java
public static void main(String[] args)
```

이 프로젝트에서는 `Main`이 직접 가계부 기능을 처리하지 않습니다. `AccountBookFrame` 객체를 만들고 화면을 보여주는 역할만 합니다.

```java
SwingUtilities.invokeLater(new Runnable() {
    @Override
    public void run() {
        AccountBookFrame frame = new AccountBookFrame();
        frame.setVisible(true);
    }
});
```

`SwingUtilities.invokeLater`는 Swing 화면을 안전하게 실행하기 위한 코드입니다.

## 6. `AccountBookFrame.java`

`AccountBookFrame`은 Swing 화면과 버튼 이벤트를 담당하는 클래스입니다.

이 클래스는 `JFrame`을 상속받습니다.

```java
public class AccountBookFrame extends JFrame
```

주요 역할은 다음과 같습니다.

```text
화면 구성
입력값 읽기
버튼 클릭 처리
거래 추가
거래 수정
거래 삭제
파일 저장
프로그램 시작 시 자동 불러오기
합계 표시 갱신
```

### 6.1 주요 화면 구성 요소

```java
JTextField dateField
JTextField amountField
JComboBox<TransactionType> typeBox
JComboBox<String> categoryBox
JTextField memoField
JTable table
JLabel incomeLabel
JLabel expenseLabel
JLabel balanceLabel
JLabel statusLabel
```

각 요소의 의미는 다음과 같습니다.

```text
dateField     → 날짜 입력칸
amountField   → 금액 입력칸
typeBox       → 수입/지출 선택 박스
categoryBox   → 카테고리 선택 박스
memoField     → 메모 입력칸
table         → 거래 내역 표
incomeLabel   → 총 수입 표시
expenseLabel  → 총 지출 표시
balanceLabel  → 잔액 표시
statusLabel   → 저장 완료, 자동 불러오기 완료 같은 상태 메시지 표시
```

### 6.2 화면 레이아웃

현재 화면은 다음 영역으로 나뉩니다.

```text
상단 제목 영역
↓
총 수입 / 총 지출 / 잔액 카드
↓
왼쪽 거래 내역 표
오른쪽 거래 입력 패널
↓
하단 상태 메시지
```

입력 패널에는 다음 버튼이 있습니다.

```text
추가
수정
삭제
저장
```

각 버튼 색상은 `AccountBookStyle`에서 관리합니다.

### 6.3 카테고리 목록 처리

수입과 지출은 사용할 수 있는 카테고리가 다릅니다.

```java
private static final String[] INCOME_CATEGORIES = {"월급", "용돈"};
private static final String[] EXPENSE_CATEGORIES = {"식비", "교통", "쇼핑", "구독", "기타"};
```

구분이 `수입`이면 카테고리 박스에는 다음 값만 표시됩니다.

```text
월급
용돈
```

구분이 `지출`이면 카테고리 박스에는 다음 값만 표시됩니다.

```text
식비
교통
쇼핑
구독
기타
```

이 처리는 `updateCategoryBox()`와 `updateCategoryComboBox()`에서 담당합니다.

```java
private void updateCategoryBox() {
    TransactionType type = (TransactionType) typeBox.getSelectedItem();
    updateCategoryComboBox(categoryBox, type);
}
```

수정 팝업에서도 같은 로직을 사용하기 때문에, 사용자가 카테고리를 직접 입력해서 없는 카테고리를 넣는 문제를 막을 수 있습니다.

### 6.4 추가 버튼 흐름

추가 버튼을 누르면 `addTransaction()`이 실행됩니다.

```text
1. 날짜 입력값 읽기
2. 금액 입력값 읽기
3. 구분 선택값 읽기
4. 카테고리 선택값 읽기
5. 메모 입력값 읽기
6. 구분이 수입이면 Income 객체 생성
7. 구분이 지출이면 Expense 객체 생성
8. AccountBook에 추가
9. JTable 새로고침
10. 총 수입, 총 지출, 잔액 다시 계산
```

핵심 코드는 다음과 같습니다.

```java
Transaction transaction;
if (type == TransactionType.INCOME) {
    transaction = new Income(date, amount, category, memo);
} else {
    transaction = new Expense(date, amount, category, memo);
}

book.addTransaction(transaction);
```

`transaction` 변수 타입은 `Transaction`이지만 실제 객체는 `Income` 또는 `Expense`입니다. 이 부분이 다형성입니다.

### 6.5 수정 버튼 흐름

수정 버튼을 누르면 `editSelectedTransaction()`이 실행됩니다.

현재 수정 가능한 항목은 다음과 같습니다.

```text
날짜
구분
카테고리
금액
메모
```

수정 흐름은 다음과 같습니다.

```text
1. JTable에서 수정할 거래 선택
2. 수정 버튼 클릭
3. 기존 거래 정보를 팝업창에 표시
4. 날짜, 구분, 카테고리, 금액, 메모 수정
5. 구분이 바뀌면 카테고리 목록도 자동 변경
6. 확인 버튼 클릭
7. 새 Income 또는 Expense 객체 생성
8. AccountBook의 setTransaction()으로 기존 거래 교체
9. JTable 새로고침
10. 총 수입, 총 지출, 잔액 다시 계산
```

구분을 바꾸면 카테고리 목록도 같이 바뀝니다.

```java
editTypeBox.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        TransactionType selectedType = (TransactionType) editTypeBox.getSelectedItem();
        updateCategoryComboBox(editCategoryBox, selectedType);
    }
});
```

구분이 바뀔 수 있기 때문에 기존 객체의 필드만 고치는 것이 아니라 새 거래 객체를 만들어 교체합니다.

```java
Transaction newTransaction;
if (newType == TransactionType.INCOME) {
    newTransaction = new Income(newDate, newAmount, newCategory, newMemo);
} else {
    newTransaction = new Expense(newDate, newAmount, newCategory, newMemo);
}

book.setTransaction(modelRow, newTransaction);
```

수정한 내용은 화면에는 바로 반영됩니다. 하지만 파일에 저장하려면 저장 버튼을 눌러야 합니다.

### 6.6 삭제 버튼 흐름

삭제 버튼을 누르면 `deleteSelectedTransaction()`이 실행됩니다.

```text
1. JTable에서 선택된 줄 확인
2. 선택된 줄이 없으면 오류 메시지 표시
3. 선택된 줄이 있으면 AccountBook에서 삭제
4. JTable 새로고침
5. 총 수입, 총 지출, 잔액 다시 계산
```

### 6.7 저장 버튼 흐름

저장 버튼을 누르면 `saveData()`가 실행됩니다.

```java
file.save(saveFile, book.getTransactions());
```

저장 버튼을 누르면 현재 거래 목록이 `accountbook-data.txt` 파일에 저장됩니다. 저장이 끝나면 화면 하단의 `statusLabel`에 저장 완료 메시지가 표시됩니다.

```text
파일 저장 완료: 저장 파일 경로
```

### 6.8 자동 불러오기 흐름

불러오기 버튼은 따로 없습니다.

프로그램을 시작할 때 `accountbook-data.txt` 파일이 있으면 자동으로 불러옵니다.

```java
private void loadInitialData() {
    if (saveFile.toFile().exists()) {
        loadData();
    }
}
```

`loadData()`는 파일에서 거래 목록을 읽은 뒤 `AccountBook`에 다시 넣습니다.

```java
book.setTransactions(file.load(saveFile));
```

## 7. `AccountBookStyle.java`

`AccountBookStyle`은 UI 꾸미기만 담당하는 클래스입니다.

`AccountBookFrame` 안에 색상, 폰트, 버튼 스타일 코드가 전부 들어가면 화면 클래스가 너무 길고 복잡해집니다. 그래서 UI 스타일은 별도 클래스로 분리했습니다.

예시는 다음과 같습니다.

```java
AccountBookStyle.styleInput(dateField);
AccountBookStyle.styleButton(saveButton, AccountBookStyle.SAVE_BUTTON);
AccountBookStyle.styleTable(table);
```

이 구조는 역할 분리에 해당합니다.

```text
AccountBookFrame
→ 화면 배치와 이벤트 처리

AccountBookStyle
→ 화면 꾸미기
```

버튼 색상도 여기서 관리합니다.

```text
추가 버튼 → 파란색
수정 버튼 → 보라색
삭제 버튼 → 빨간색
저장 버튼 → 주황색
```

## 8. `Transaction.java`

`Transaction`은 거래 1개의 공통 정보를 담는 추상 부모 클래스입니다.

수입과 지출은 모두 다음 정보를 가집니다.

```text
날짜
금액
카테고리
메모
```

그래서 이 공통 필드를 `Transaction`에 넣었습니다.

```java
private LocalDate date;
private long amount;
private String category;
private String memo;
```

필드는 `private`입니다. 외부에서 직접 접근하지 못하게 하고, getter/setter를 통해 접근합니다.

```java
public long getAmount() {
    return amount;
}

public void setAmount(long amount) {
    this.amount = amount;
}
```

이것이 캡슐화입니다.

`Transaction`에는 추상 메서드가 있습니다.

```java
public abstract TransactionType getType();
```

이 메서드는 “이 거래가 수입인지 지출인지 알려줘”라는 뜻입니다. 부모 클래스에서는 정하지 않고, 자식 클래스인 `Income`, `Expense`에서 구현합니다.

## 9. `Income.java`

`Income`은 수입 거래 클래스입니다.

```java
public class Income extends Transaction
```

`Transaction`을 상속받고, `getType()`에서 `INCOME`을 반환합니다.

```java
@Override
public TransactionType getType() {
    return TransactionType.INCOME;
}
```

## 10. `Expense.java`

`Expense`는 지출 거래 클래스입니다.

```java
public class Expense extends Transaction
```

`Transaction`을 상속받고, `getType()`에서 `EXPENSE`를 반환합니다.

```java
@Override
public TransactionType getType() {
    return TransactionType.EXPENSE;
}
```

## 11. `TransactionType.java`

`TransactionType`은 수입과 지출을 구분하는 enum입니다.

```java
public enum TransactionType {
    INCOME("수입"),
    EXPENSE("지출");
}
```

화면에는 `INCOME`, `EXPENSE` 대신 `수입`, `지출`이 보이도록 `displayName`을 사용합니다.

```java
public String getDisplayName() {
    return displayName;
}
```

또 콤보박스에서도 한글로 보이도록 `toString()`을 오버라이딩했습니다.

```java
@Override
public String toString() {
    return displayName;
}
```

파일에 저장할 때는 `transaction.getType().name()`을 사용하므로 내부 저장 값은 기존처럼 `INCOME`, `EXPENSE`입니다.

## 12. `AccountBook.java`

`AccountBook`은 거래 목록 관리와 합계 계산을 담당하는 핵심 로직 클래스입니다.

거래 목록은 `ArrayList<Transaction>`로 관리합니다.

```java
private final ArrayList<Transaction> transactions = new ArrayList<>();
```

`Transaction` 타입 리스트이기 때문에 `Income`과 `Expense` 객체를 함께 저장할 수 있습니다.

주요 메서드는 다음과 같습니다.

```text
addTransaction()
→ 거래 추가

removeTransaction()
→ 거래 삭제

setTransaction()
→ 선택한 거래 수정 시 기존 거래를 새 거래로 교체

setTransactions()
→ 파일에서 불러온 전체 거래 목록으로 교체

getTransactions()
→ 거래 목록 조회

getTotalIncome()
→ 총 수입 계산

getTotalExpense()
→ 총 지출 계산

getBalance()
→ 잔액 계산
```

합계 계산은 거래 목록을 반복하면서 구분에 따라 금액을 더합니다.

```java
if (transaction.getType() == TransactionType.INCOME) {
    total += transaction.getAmount();
}
```

## 13. `TransactionTableModel.java`

`TransactionTableModel`은 거래 목록을 `JTable`에 보여주기 위한 클래스입니다.

`JTable`은 `ArrayList`를 바로 화면에 표시하지 못합니다. 그래서 `AbstractTableModel`을 상속한 별도 모델 클래스를 만들었습니다.

```java
public class TransactionTableModel extends AbstractTableModel
```

표의 열 이름은 다음과 같습니다.

```java
private final String[] columns = {"날짜", "구분", "카테고리", "금액", "메모"};
```

`getValueAt()`은 각 칸에 어떤 값을 보여줄지 정합니다.

```text
0번 열 → 날짜
1번 열 → 수입/지출
2번 열 → 카테고리
3번 열 → 금액
4번 열 → 메모
```

거래가 추가, 수정, 삭제되면 `refresh()`를 호출해서 표를 갱신합니다.

```java
public void refresh() {
    fireTableDataChanged();
}
```

## 14. `AccountBookFileManager.java`

`AccountBookFileManager`는 파일 저장과 불러오기를 담당합니다.

`AccountBook`에 파일 저장 코드까지 넣으면 한 클래스가 너무 많은 일을 하게 되므로, 파일 I/O는 별도 클래스로 분리했습니다.

### 14.1 저장

```java
public void save(Path path, List<Transaction> transactions) throws IOException
```

거래 목록을 파일에 한 줄씩 저장합니다.

저장 예시는 다음과 같습니다.

```text
INCOME	2026-06-02	50000	월급	6월 월급
EXPENSE	2026-06-02	12000	식비	점심
```

순서는 다음과 같습니다.

```text
거래유형    날짜    금액    카테고리    메모
```

### 14.2 불러오기

```java
public List<Transaction> load(Path path) throws IOException
```

파일을 읽어서 다시 거래 객체 목록으로 만듭니다.

첫 번째 값이 `INCOME`이면 `Income` 객체를 만들고, `EXPENSE`이면 `Expense` 객체를 만듭니다.

```java
if (type == TransactionType.INCOME) {
    return new Income(date, amount, category, memo);
}
return new Expense(date, amount, category, memo);
```

### 14.3 `escape()`와 `unescape()`

메모나 카테고리에 탭, 줄바꿈 같은 문자가 들어가면 파일 형식이 깨질 수 있습니다. 그래서 저장할 때는 특수 문자를 안전한 형태로 바꾸고, 불러올 때는 다시 원래 문자로 되돌립니다.

```java
replace("\t", "\\t")
replace("\n", "\\n")
replace("\r", "\\r")
```

## 15. 사용한 자바 개념

### 클래스와 객체

프로그램을 여러 클래스로 나누고, 필요한 곳에서 객체를 생성해서 사용했습니다.

### 캡슐화

필드는 `private`으로 두고 getter/setter로 접근하도록 했습니다.

### 상속

`Income`과 `Expense`가 `Transaction`을 상속합니다.

### 추상 클래스

`Transaction`은 공통 개념이므로 추상 클래스로 만들었습니다.

### 오버라이딩

`Income`과 `Expense`가 `getType()`을 각각 다르게 구현합니다.

### 다형성

`ArrayList<Transaction>` 하나에 `Income`과 `Expense` 객체를 함께 저장합니다.

### 컬렉션과 제네릭

여러 거래를 저장하기 위해 `ArrayList<Transaction>`을 사용했습니다.

### 파일 I/O

`BufferedReader`, `BufferedWriter`, `Files`를 사용해 텍스트 파일로 저장하고 불러옵니다.

### Swing

`JFrame`, `JPanel`, `JButton`, `JTextField`, `JComboBox`, `JTable`, `JLabel`, `JOptionPane`을 사용해 GUI를 만들었습니다.

## 16. 실행 방법

IntelliJ에서는 `JavaAccountBookSwing` 폴더를 열고 `Main.java`를 실행하면 됩니다.

터미널에서는 프로젝트 폴더에서 다음 명령어를 실행합니다.

```powershell
javac -encoding UTF-8 -d out src/main/java/accountbook/*.java
java -cp out accountbook.Main
```

## 17. 헷갈릴 수 있는 부분

### `TransactionType`과 `getType()` 차이

`TransactionType`은 수입/지출이라는 선택지입니다.

```text
INCOME
EXPENSE
```

`getType()`은 실제 거래 객체가 어떤 선택지에 속하는지 알려주는 메서드입니다.

```text
Income 객체의 getType()  → INCOME
Expense 객체의 getType() → EXPENSE
```


