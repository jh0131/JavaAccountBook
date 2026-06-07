package accountbook;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

    // AccountBookStyle 클래스는 화면 디자인/스타일을 담당하는 클래스

public class AccountBookStyle {

    public static final Color APP_BACKGROUND = new Color(245, 246, 248);
    public static final Color PANEL_BACKGROUND = Color.WHITE;
    public static final Color SIDE_BAR = new Color(67, 83, 220);
    public static final Color TEXT_DARK = new Color(45, 52, 65);
    public static final Color TEXT_GRAY = new Color(110, 118, 130);
    public static final Color LINE = new Color(224, 228, 235);
    public static final Color TABLE_HEADER = new Color(250, 251, 253);

    public static final Color PRIMARY_BUTTON = new Color(67, 125, 255);
    public static final Color EDIT_BUTTON = new Color(155, 91, 230);
    public static final Color SAVE_BUTTON = new Color(245, 145, 45);
    public static final Color DELETE_BUTTON = new Color(238, 89, 89);

    public static final Color INCOME = new Color(30, 144, 255);
    public static final Color EXPENSE = new Color(232, 143, 34);
    public static final Color BALANCE = new Color(102, 212, 50);
    public static final Color COUNT = new Color(196, 62, 221);

    private static final Font TITLE_FONT = new Font("맑은 고딕", Font.BOLD, 18);
    private static final Font FORM_TITLE_FONT = new Font("맑은 고딕", Font.BOLD, 16);
    private static final Font CARD_TITLE_FONT = new Font("맑은 고딕", Font.BOLD, 17);
    private static final Font LABEL_FONT = new Font("맑은 고딕", Font.BOLD, 12);
    private static final Font NORMAL_FONT = new Font("맑은 고딕", Font.PLAIN, 13);
    private static final Font BUTTON_FONT = new Font("맑은 고딕", Font.BOLD, 12);
    private static final Font MONEY_FONT = new Font("맑은 고딕", Font.BOLD, 22);

    public static void styleFrame(JFrame frame) {
        frame.getContentPane().setBackground(APP_BACKGROUND);
    }

    public static void styleSideBar(JPanel panel) {
        panel.setBackground(SIDE_BAR);
        panel.setBorder(new EmptyBorder(14, 10, 14, 10));
    }

    public static void styleHeaderPanel(JPanel panel) {
        panel.setBackground(PANEL_BACKGROUND);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, LINE));
    }

    public static void styleContentPanel(JPanel panel) {
        panel.setBackground(APP_BACKGROUND);
        panel.setBorder(new EmptyBorder(18, 24, 20, 24));
    }

    public static void stylePanelBox(JPanel panel) {
        panel.setBackground(PANEL_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(238, 240, 244)),
                new EmptyBorder(14, 16, 14, 16)
        ));
    }

    public static JLabel createSideLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        label.setHorizontalAlignment(JLabel.CENTER);
        return label;
    }

    public static JLabel createInputLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_DARK);
        return label;
    }

    public static void styleTitleLabel(JLabel label) {
        label.setForeground(TEXT_DARK);
        label.setFont(TITLE_FONT);
    }

    public static void styleFormTitleLabel(JLabel label) {
        label.setForeground(TEXT_DARK);
        label.setFont(FORM_TITLE_FONT);
    }

    public static void styleSmallText(JLabel label) {
        label.setForeground(TEXT_GRAY);
        label.setFont(NORMAL_FONT);
    }

    public static void styleInput(JTextField textField) {
        textField.setFont(NORMAL_FONT);
        textField.setForeground(TEXT_DARK);
        textField.setBackground(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE),
                new EmptyBorder(7, 8, 7, 8)
        ));
    }

    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(NORMAL_FONT);
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(TEXT_DARK);
    }

    public static void styleButton(JButton button, Color color) {
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(7, 12, 7, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(34);
        table.setFont(NORMAL_FONT);
        table.setForeground(TEXT_DARK);
        table.setBackground(PANEL_BACKGROUND);
        table.setGridColor(LINE);
        table.setSelectionBackground(new Color(232, 238, 255));
        table.setSelectionForeground(TEXT_DARK);
        table.getTableHeader().setFont(LABEL_FONT);
        table.getTableHeader().setBackground(TABLE_HEADER);
        table.getTableHeader().setForeground(TEXT_DARK);
        table.getTableHeader().setPreferredSize(new Dimension(0, 34));
    }

    public static Border createTableBorder() {
        return BorderFactory.createCompoundBorder(
                new EmptyBorder(0, 0, 0, 0),
                BorderFactory.createLineBorder(new Color(238, 240, 244))
        );
    }

    public static void styleSummaryCard(JPanel card, Color color) {
        card.setBackground(color);
        card.setBorder(new EmptyBorder(16, 16, 16, 16));
    }

    public static void styleSummaryTitle(JLabel label) {
        label.setForeground(Color.WHITE);
        label.setFont(CARD_TITLE_FONT);
    }

    public static void styleMoneyLabel(JLabel label, Color color) {
        label.setForeground(Color.WHITE);
        label.setFont(MONEY_FONT);
    }

    public static void styleStatusLabel(JLabel label) {
        label.setForeground(TEXT_GRAY);
        label.setFont(NORMAL_FONT);
        label.setBorder(new EmptyBorder(0, 4, 0, 0));
    }
}
