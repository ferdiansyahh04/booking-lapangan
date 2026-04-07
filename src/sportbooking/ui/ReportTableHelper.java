package sportbooking.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public final class ReportTableHelper {

    private ReportTableHelper() {
    }

    public static JTextField createSearchField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(220, 32));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218)),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return field;
    }

    public static TableRowSorter<DefaultTableModel> installSearchFilter(
            DefaultTableModel model, JTextField field, int... columns) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                apply();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                apply();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                apply();
            }

            private void apply() {
                String text = field.getText().trim();
                if (text.isEmpty()) {
                    sorter.setRowFilter(null);
                    return;
                }
                RowFilter<DefaultTableModel, Object> filter = RowFilter.regexFilter(
                        "(?i)" + Pattern.quote(text), columns);
                sorter.setRowFilter(filter);
            }
        });
        return sorter;
    }
}
