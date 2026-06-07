package accountbook;

import javax.swing.table.AbstractTableModel;
import java.text.NumberFormat;
import java.util.Locale;

public class TransactionTableModel extends AbstractTableModel {

    private final String[] columns = {"날짜", "구분", "카테고리", "금액", "메모"};
    private final AccountBook accountBook;
    private final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.KOREA);

    public TransactionTableModel(AccountBook accountBook) {
        this.accountBook = accountBook;
    }

    @Override
    public int getRowCount() {
        return accountBook.getTransactions().size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Transaction transaction = accountBook.getTransactions().get(rowIndex);
        switch (columnIndex) {
            case 0:
                return transaction.getDate();
            case 1:
                return transaction.getType().getDisplayName();
            case 2:
                return transaction.getCategory().getDisplayName();
            case 3:
                return numberFormat.format(transaction.getAmount()) + "원";
            case 4:
                return transaction.getMemo();
            default:
                return "";
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }
}
