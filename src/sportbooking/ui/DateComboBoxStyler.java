package sportbooking.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboPopup;

public final class DateComboBoxStyler {

    private static final Dimension DATE_SIZE = new Dimension(140, 32);
    private static final String PROTOTYPE = "2026-05-07";

    private DateComboBoxStyler() {
    }

    public static void style(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboBox.setPreferredSize(DATE_SIZE);
        comboBox.setMinimumSize(DATE_SIZE);
        comboBox.setMaximumSize(new Dimension(150, 32));
        comboBox.setPrototypeDisplayValue(PROTOTYPE);
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(new Color(44, 62, 80));
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218)),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                setHorizontalAlignment(LEFT);
                if (!isSelected) {
                    setBackground(Color.WHITE);
                    setForeground(new Color(44, 62, 80));
                }
                return component;
            }
        });
        comboBox.setMaximumRowCount(12);

        Object child = comboBox.getAccessibleContext().getAccessibleChild(0);
        if (child instanceof BasicComboPopup) {
            BasicComboPopup popup = (BasicComboPopup) child;
            popup.setPreferredSize(new Dimension(DATE_SIZE.width + 20, popup.getPreferredSize().height));
        }
    }
}
