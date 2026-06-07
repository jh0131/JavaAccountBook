package accountbook;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;


// Swing을 이용한 프론트 엔드 화면 구성 부분.

public class AccountBookFrame extends JFrame {

    private final AccountBook book = new AccountBook();

    private final AccountBookFileManager file = new AccountBookFileManager();
    private final Path saveFile = Path.of("accountbook-data.txt");
    private final NumberFormat wonFormat = NumberFormat.getNumberInstance(Locale.KOREA);

    private final JTextField dateField = new JTextField(LocalDate.now().toString(), 10);
    private final JTextField amountField = new JTextField(10);

    private final JComboBox<TransactionType> typeBox = new JComboBox<>(TransactionType.values());
    private final JComboBox<TransactionCategory> categoryBox = new JComboBox<>();
    private final JTextField memoField = new JTextField(18);

    private final JLabel incomeLabel = new JLabel();
    private final JLabel expenseLabel = new JLabel();
    private final JLabel balanceLabel = new JLabel();
    private final JLabel statusLabel = new JLabel("준비 완료");

    private final TransactionTableModel tableModel = new TransactionTableModel(book);
    private final JTable table = new JTable(tableModel);

    public AccountBookFrame() {
        setTitle("Java Swing 가계부 프로그램");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        AccountBookStyle.styleFrame(this);

        add(createMainPanel(), BorderLayout.CENTER);

        loadInitialData();
        updateSummary();

        setSize(1180, 720);
        setMinimumSize(new Dimension(920, 600));
        setLocationRelativeTo(null);
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(AccountBookStyle.APP_BACKGROUND);
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createContentPanel(), BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        AccountBookStyle.styleHeaderPanel(headerPanel);
        headerPanel.setBorder(new EmptyBorder(14, 24, 14, 24));

        JLabel titleLabel = new JLabel("Java Swing 가계부 프로그램");
        AccountBookStyle.styleTitleLabel(titleLabel);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        return headerPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout(0, 16));
        AccountBookStyle.styleContentPanel(contentPanel);

        JPanel topPanel = new JPanel(new BorderLayout(0, 16));
        topPanel.setOpaque(false);
        topPanel.add(createSummaryPanel(), BorderLayout.CENTER);

        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(createWorkPanel(), BorderLayout.CENTER);
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        AccountBookStyle.styleStatusLabel(statusLabel);
        contentPanel.add(statusLabel, BorderLayout.SOUTH);
        return contentPanel;
    }

    private JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 18, 0));
        summaryPanel.setOpaque(false);
        summaryPanel.add(createSummaryCard("총 수입", incomeLabel, AccountBookStyle.INCOME));
        summaryPanel.add(createSummaryCard("총 지출", expenseLabel, AccountBookStyle.EXPENSE));
        summaryPanel.add(createSummaryCard("잔액", balanceLabel, AccountBookStyle.BALANCE));
        return summaryPanel;
    }

    private JPanel createWorkPanel() {
        JPanel workPanel = new JPanel(new BorderLayout(14, 0));
        workPanel.setOpaque(false);

        JPanel tablePanel = new JPanel(new BorderLayout(0, 12));
        AccountBookStyle.stylePanelBox(tablePanel);
        JLabel tableTitle = new JLabel("거래 내역");
        AccountBookStyle.styleTitleLabel(tableTitle);
        tablePanel.add(tableTitle, BorderLayout.NORTH);
        tablePanel.add(createTablePanel(), BorderLayout.CENTER);

        workPanel.add(tablePanel, BorderLayout.CENTER);
        workPanel.add(createInputPanel(), BorderLayout.EAST);
        return workPanel;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setPreferredSize(new Dimension(320, 0));
        AccountBookStyle.stylePanelBox(panel);

        categoryBox.setEditable(false);
        AccountBookStyle.styleInput(dateField);
        AccountBookStyle.styleInput(amountField);
        AccountBookStyle.styleInput(memoField);
        AccountBookStyle.styleComboBox(typeBox);
        AccountBookStyle.styleComboBox(categoryBox);
        updateCategoryBox();

        typeBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCategoryBox();
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 0, 7, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1;

        JLabel inputTitle = new JLabel("거래 입력");
        AccountBookStyle.styleFormTitleLabel(inputTitle);
        gbc.gridy = 0;
        gbc.insets = new Insets(2, 0, 12, 0);
        panel.add(inputTitle, gbc);

        addFormRow(panel, gbc, 1, "날짜", dateField);
        addFormRow(panel, gbc, 3, "구분", typeBox);
        addFormRow(panel, gbc, 5, "카테고리", categoryBox);
        addFormRow(panel, gbc, 7, "금액", amountField);
        addFormRow(panel, gbc, 9, "메모", memoField);

        JButton addButton = new JButton("추가");
        AccountBookStyle.styleButton(addButton, AccountBookStyle.PRIMARY_BUTTON);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTransaction();
            }
        });

        JButton editButton = new JButton("수정");
        AccountBookStyle.styleButton(editButton, AccountBookStyle.EDIT_BUTTON);
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editSelectedTransaction();
            }
        });

        JButton deleteButton = new JButton("삭제");
        AccountBookStyle.styleButton(deleteButton, AccountBookStyle.DELETE_BUTTON);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedTransaction();
            }
        });

        JButton saveButton = new JButton("저장");
        AccountBookStyle.styleButton(saveButton, AccountBookStyle.SAVE_BUTTON);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveData();
            }
        });

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        buttonPanel.setOpaque(false);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(saveButton);

        gbc.gridy = 11;
        gbc.insets = new Insets(12, 0, 0, 0);
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, Component input) {
        gbc.gridy = row;
        gbc.insets = new Insets(7, 0, 2, 0);
        panel.add(AccountBookStyle.createInputLabel(label), gbc);

        gbc.gridy = row + 1;
        gbc.insets = new Insets(0, 0, 7, 0);
        panel.add(input, gbc);
    }

    private void updateCategoryBox() {
        TransactionType type = (TransactionType) typeBox.getSelectedItem();
        updateCategoryComboBox(categoryBox, type);
    }

    private void updateCategoryComboBox(JComboBox<TransactionCategory> comboBox, TransactionType type) {
        TransactionCategory[] categories = getCategories(type);
        comboBox.removeAllItems();
        for (TransactionCategory category : categories) {
            comboBox.addItem(category);
        }
    }

    private TransactionCategory[] getCategories(TransactionType type) {
        if (type == TransactionType.INCOME) {
            return IncomeCategory.values();
        }
        return ExpenseCategory.values();
    }

    private JScrollPane createTablePanel() {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        AccountBookStyle.styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(AccountBookStyle.createTableBorder());
        scrollPane.getViewport().setBackground(AccountBookStyle.PANEL_BACKGROUND);
        return scrollPane;
    }

    private JPanel createSummaryCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout(6, 6));
        AccountBookStyle.styleSummaryCard(card, color);

        JLabel titleLabel = new JLabel(title);
        AccountBookStyle.styleSummaryTitle(titleLabel);

        AccountBookStyle.styleMoneyLabel(valueLabel, color);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private void addTransaction() {
        try {
            LocalDate date = LocalDate.parse(dateField.getText().trim());
            long amount = parseAmount(amountField.getText().trim());
            TransactionType type = (TransactionType) typeBox.getSelectedItem();
            TransactionCategory category = getRequiredCategory(categoryBox.getSelectedItem());
            String memo = memoField.getText().trim();

            Transaction transaction;
            if (type == TransactionType.INCOME) {
                transaction = new Income(date, amount, (IncomeCategory) category, memo);
            } else {
                transaction = new Expense(date, amount, (ExpenseCategory) category, memo);
            }

            book.addTransaction(transaction);
            tableModel.refresh();
            updateSummary();
            clearInput();
            statusLabel.setText("거래 내역을 추가했습니다.");
        } catch (DateTimeParseException exception) {
            showError("날짜는 yyyy-MM-dd 형식으로 입력하세요.");
        } catch (NumberFormatException exception) {
            showError("금액은 0보다 큰 숫자로 입력하세요.");
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    private long parseAmount(String text) {
        long amount = Long.parseLong(text.replace(",", ""));
        if (amount <= 0) {
            throw new NumberFormatException();
        }
        return amount;
    }

    private TransactionCategory getRequiredCategory(Object selectedItem) {
        if (!(selectedItem instanceof TransactionCategory)) {
            throw new IllegalArgumentException("카테고리를 선택하세요.");
        }
        return (TransactionCategory) selectedItem;
    }

    private void deleteSelectedTransaction() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            showError("삭제할 거래 내역을 선택하세요.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        book.removeTransaction(modelRow);
        tableModel.refresh();
        updateSummary();
        statusLabel.setText("선택한 거래 내역을 삭제했습니다.");
    }

    private void editSelectedTransaction() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            showError("수정할 거래 내역을 선택하세요.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        Transaction transaction = book.getTransactions().get(modelRow);

        JTextField editDateField = new JTextField(transaction.getDate().toString(), 15);
        JComboBox<TransactionType> editTypeBox = new JComboBox<>(TransactionType.values());
        JComboBox<TransactionCategory> editCategoryBox = new JComboBox<>();
        JTextField editAmountField = new JTextField(String.valueOf(transaction.getAmount()), 15);
        JTextField editMemoField = new JTextField(transaction.getMemo(), 15);

        editTypeBox.setSelectedItem(transaction.getType());
        updateCategoryComboBox(editCategoryBox, transaction.getType());
        selectCategoryIfExists(editCategoryBox, transaction.getCategory());

        AccountBookStyle.styleInput(editDateField);
        AccountBookStyle.styleComboBox(editTypeBox);
        AccountBookStyle.styleComboBox(editCategoryBox);
        AccountBookStyle.styleInput(editAmountField);
        AccountBookStyle.styleInput(editMemoField);

        editTypeBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TransactionType selectedType = (TransactionType) editTypeBox.getSelectedItem();
                updateCategoryComboBox(editCategoryBox, selectedType);
            }
        });

        JPanel editPanel = new JPanel(new GridBagLayout());
        editPanel.setBackground(AccountBookStyle.PANEL_BACKGROUND);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        editPanel.add(AccountBookStyle.createInputLabel("날짜"), gbc);

        gbc.gridx = 1;
        editPanel.add(editDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        editPanel.add(AccountBookStyle.createInputLabel("구분"), gbc);

        gbc.gridx = 1;
        editPanel.add(editTypeBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        editPanel.add(AccountBookStyle.createInputLabel("카테고리"), gbc);

        gbc.gridx = 1;
        editPanel.add(editCategoryBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        editPanel.add(AccountBookStyle.createInputLabel("금액"), gbc);

        gbc.gridx = 1;
        editPanel.add(editAmountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        editPanel.add(AccountBookStyle.createInputLabel("메모"), gbc);

        gbc.gridx = 1;
        editPanel.add(editMemoField, gbc);

        int result = JOptionPane.showConfirmDialog(
                this,
                editPanel,
                "거래 내역 수정",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            LocalDate newDate = LocalDate.parse(editDateField.getText().trim());
            TransactionType newType = (TransactionType) editTypeBox.getSelectedItem();
            TransactionCategory newCategory = getRequiredCategory(editCategoryBox.getSelectedItem());
            long newAmount = parseAmount(editAmountField.getText().trim());
            String newMemo = editMemoField.getText().trim();

            Transaction newTransaction;
            if (newType == TransactionType.INCOME) {
                newTransaction = new Income(newDate, newAmount, (IncomeCategory) newCategory, newMemo);
            } else {
                newTransaction = new Expense(newDate, newAmount, (ExpenseCategory) newCategory, newMemo);
            }

            book.setTransaction(modelRow, newTransaction);
            tableModel.refresh();
            updateSummary();
            statusLabel.setText("선택한 거래 내역을 수정했습니다. 저장 버튼을 누르면 파일에 반영됩니다.");
        } catch (DateTimeParseException exception) {
            showError("날짜는 yyyy-MM-dd 형식으로 입력하세요.");
        } catch (NumberFormatException exception) {
            showError("금액은 0보다 큰 숫자로 입력하세요.");
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    private void selectCategoryIfExists(JComboBox<TransactionCategory> comboBox, TransactionCategory category) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            if (comboBox.getItemAt(i).equals(category)) {
                comboBox.setSelectedItem(category);
                return;
            }
        }
    }

    private void saveData() {
        try {
            file.save(saveFile, book.getTransactions());
            statusLabel.setText("파일 저장 완료: " + saveFile.toAbsolutePath());
        } catch (Exception exception) {
            showError("파일 저장 중 오류가 발생했습니다: " + exception.getMessage());
        }
    }

    private void loadInitialData() {
        if (saveFile.toFile().exists()) {
            loadData();
        }
    }

    private void loadData() {
        try {
            book.setTransactions(file.load(saveFile));
            tableModel.refresh();
            updateSummary();
            statusLabel.setText("자동 불러오기 완료: " + saveFile.toAbsolutePath());
        } catch (Exception exception) {
            showError("파일 불러오기 중 오류가 발생했습니다: " + exception.getMessage());
        }
    }

    private void updateSummary() {
        incomeLabel.setText(formatWon(book.getTotalIncome()));
        expenseLabel.setText(formatWon(book.getTotalExpense()));
        balanceLabel.setText(formatWon(book.getBalance()));
    }

    private String formatWon(long amount) {
        return wonFormat.format(amount) + "원";
    }

    private void clearInput() {
        dateField.setText(LocalDate.now().toString());
        amountField.setText("");
        memoField.setText("");
        updateCategoryBox();
        amountField.requestFocus();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "입력 오류", JOptionPane.ERROR_MESSAGE);
        statusLabel.setText(message);
    }
}
