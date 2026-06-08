package accountbook.file;

import accountbook.model.Expense;
import accountbook.model.ExpenseCategory;
import accountbook.model.Income;
import accountbook.model.IncomeCategory;
import accountbook.model.Transaction;
import accountbook.model.TransactionType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


// 파일 저장하고 불러오기 구현 클래스 (파일 I/O)

public class AccountBookFileManager {

    private static final String SEPARATOR = "\t";


    // save() 거래 목록을 파일에 저장하고, 파일 저장 오류에 대한 예외 처리
    public void save(Path path, List<Transaction> transactions) throws IOException {

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            for (Transaction transaction : transactions) {
                writer.write(toLine(transaction));
                writer.newLine();
            }
        }
    }

    // load() 파일을 통해서 저장되어있던 거래내역을 불러옴. (마찬가지로 예외 처리)
    public List<Transaction> load(Path path) throws IOException {
        ArrayList<Transaction> transactions = new ArrayList<>();
        if (!Files.exists(path)) {
            return transactions;
        }

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    transactions.add(fromLine(line));
                }
            }
        }
        return transactions;
    }


    private String toLine(Transaction transaction) {
        return transaction.getType().name()
                + SEPARATOR + transaction.getDate()
                + SEPARATOR + transaction.getAmount()
                + SEPARATOR + escape(transaction.getCategory().getCode())
                + SEPARATOR + escape(transaction.getMemo());
    }


    private Transaction fromLine(String line) {
        String[] parts = line.split(SEPARATOR, -1);
        if (parts.length != 5) {
            throw new IllegalArgumentException("파일 형식이 올바르지 않습니다: " + line);
        }

        TransactionType type = TransactionType.valueOf(parts[0]);
        LocalDate date = LocalDate.parse(parts[1]);
        long amount = Long.parseLong(parts[2]);
        String categoryText = unescape(parts[3]);
        String memo = unescape(parts[4]);

        if (type == TransactionType.INCOME) {
            return new Income(date, amount, IncomeCategory.from(categoryText), memo);
        }
        return new Expense(date, amount, ExpenseCategory.from(categoryText), memo);
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private String unescape(String value) {
        StringBuilder result = new StringBuilder();
        boolean escaping = false;
        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            if (escaping) {
                if (current == 't') {
                    result.append('\t');
                } else if (current == 'n') {
                    result.append('\n');
                } else if (current == 'r') {
                    result.append('\r');
                } else {
                    result.append(current);
                }
                escaping = false;
            } else if (current == '\\') {
                escaping = true;
            } else {
                result.append(current);
            }
        }
        if (escaping) {
            result.append('\\');
        }
        return result.toString();
    }
}
