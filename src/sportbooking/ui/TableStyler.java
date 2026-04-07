package sportbooking.ui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public final class TableStyler {

    private static final Color HEADER_BG = new Color(240, 244, 248);
    private static final Color HEADER_FG = new Color(33, 37, 41);
    private static final Color GRID = new Color(220, 226, 232);
    private static final Color SELECTION_BG = new Color(214, 234, 248);
    private static final Color SELECTION_FG = new Color(33, 37, 41);

    private TableStyler() {
    }

    public static void styleTable(JTable table, JScrollPane scrollPane, int rowHeight) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(rowHeight);
        table.setGridColor(GRID);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new java.awt.Dimension(0, 1));
        table.setSelectionBackground(SELECTION_BG);
        table.setSelectionForeground(SELECTION_FG);
        table.setFillsViewportHeight(true);
        table.setBackground(Color.WHITE);
        table.setForeground(new Color(44, 62, 80));
        table.setAutoCreateRowSorter(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(HEADER_BG);
        header.setForeground(HEADER_FG);
        header.setOpaque(true);
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 208, 216)));
        header.setPreferredSize(new java.awt.Dimension(0, 34));

        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        if (scrollPane != null) {
            scrollPane.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218)));
            scrollPane.getViewport().setBackground(Color.WHITE);
        }
    }

    public static DefaultTableCellRenderer createCenterRenderer() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        return renderer;
    }
}
