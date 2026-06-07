package accountbook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*  가계부 핵심 기능 구현 클래스
    기본적으로 데이터 추가,삭제,수정하는 로직은 ArrayList 라이브러리 사용함. */

public class AccountBook {

    private final ArrayList<Transaction> transactions = new ArrayList<>();


    // 거래를 추가하는 메서드. 생성된 Transaction 객체를 ArrayList에 넣는 메서드
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    // 거래 삭제 메서드
    public void removeTransaction(int index) {

        // remove할 데이터가 없을 때에 대한 예외 처리
        if (index < 0 || index >= transactions.size()) {
            throw new IndexOutOfBoundsException("삭제할 거래 내역을 찾을 수 없습니다.");
        }
        transactions.remove(index);
    }

    // 거래 내역 수정하는 메서드
    public void setTransaction(int index, Transaction transaction) {

        if (index < 0 || index >= transactions.size()) {
            throw new IndexOutOfBoundsException("수정할 거래 내역을 찾을 수 없습니다.");
        }
        transactions.set(index, transaction);
    }

    /* 기존 거래 내역 전체를 새 거래 목록으로 교체하는 메서드.
    파일의 거래내역을 불러올 때 사용 */

    public void setTransactions(List<Transaction> newTransactions) {
        transactions.clear();
        transactions.addAll(newTransactions);
    }


    // 거래내역 조회
    public List<Transaction> getTransactions() {

        return Collections.unmodifiableList(transactions);

    }

    // 총 비용을 계산하는 메서드 for 향상문을 통해 연결 배열 리스트의 값을 모두 더함

    public long getTotalIncome() {

        long total = 0;

        for (Transaction transaction : transactions) {
            if (transaction.getType() == TransactionType.INCOME) {
                total += transaction.getAmount();
            }
        }
        return total;
    }


    // 총 비용을 계산하는 메서드 for 향상문을 통해 연결 배열 리스트의 값을 모두 더함

    public long getTotalExpense() {
        long total = 0;
        for (Transaction transaction : transactions) {
            if (transaction.getType() == TransactionType.EXPENSE) {
                total += transaction.getAmount();
            }
        }
        return total;
    }

    // 잔액 계산
    public long getBalance() {

        return getTotalIncome() - getTotalExpense();
    }
}
