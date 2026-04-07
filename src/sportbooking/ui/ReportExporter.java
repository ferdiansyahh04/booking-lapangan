package sportbooking.ui;

import java.awt.Component;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;

public final class ReportExporter {

    private ReportExporter() {
    }

    public static void exportTableToPdf(Component parent, JTable table, String title) {
        if (table.getRowCount() == 0) {
            JOptionPane.showMessageDialog(parent, "Tidak ada data untuk diexport.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        File file = chooseFile(parent, title, "pdf", "PDF Files", "pdf");
        if (file == null) {
            return;
        }

        try {
            writeSimplePdf(file, table, title);
            JOptionPane.showMessageDialog(parent, "PDF berhasil disimpan:\n" + file.getAbsolutePath(), "Sukses", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parent, "Gagal export PDF:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void exportTableToExcel(Component parent, JTable table, String title) {
        if (table.getRowCount() == 0) {
            JOptionPane.showMessageDialog(parent, "Tidak ada data untuk diexport.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        File file = chooseFile(parent, title, "xlsx", "Excel Files", "xlsx");
        if (file == null) {
            return;
        }

        try {
            writeSimpleXlsx(file, table, title);
            JOptionPane.showMessageDialog(parent, "Excel berhasil disimpan:\n" + file.getAbsolutePath(), "Sukses", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parent, "Gagal export Excel:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static File chooseFile(Component parent, String title, String extension, String filterName, String filterExtension) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Simpan " + title);
        chooser.setSelectedFile(new File(sanitizeFileName(title) + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "." + extension));
        chooser.setFileFilter(new FileNameExtensionFilter(filterName, filterExtension));
        int result = chooser.showSaveDialog(parent);
        if (result != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith("." + extension)) {
            file = new File(file.getAbsolutePath() + "." + extension);
        }
        return file;
    }

    private static void writeSimplePdf(File file, JTable table, String title) throws IOException {
        List<String> headers = getHeaders(table);
        List<List<String>> rows = getRows(table);
        List<String> lines = buildTableLines(headers, rows);
        String printedAt = "Dicetak: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        List<List<String>> pages = paginate(title, printedAt, lines, 46);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        List<Integer> offsets = new ArrayList<>();
        out.write("%PDF-1.4\n".getBytes(StandardCharsets.US_ASCII));

        int totalObjects = 3 + (pages.size() * 2);
        List<Integer> pageObjectNumbers = new ArrayList<>();
        List<Integer> contentObjectNumbers = new ArrayList<>();
        int objectNumber = 4;
        for (int i = 0; i < pages.size(); i++) {
            pageObjectNumbers.add(objectNumber++);
            contentObjectNumbers.add(objectNumber++);
        }

        offsets.add(out.size());
        writeObject(out, 1, "<< /Type /Catalog /Pages 2 0 R >>");

        StringBuilder kids = new StringBuilder();
        for (Integer pageObject : pageObjectNumbers) {
            kids.append(pageObject).append(" 0 R ");
        }
        offsets.add(out.size());
        writeObject(out, 2, "<< /Type /Pages /Kids [" + kids.toString().trim() + "] /Count " + pages.size() + " >>");

        offsets.add(out.size());
        writeObject(out, 3, "<< /Type /Font /Subtype /Type1 /BaseFont /Courier >>");

        for (int i = 0; i < pages.size(); i++) {
            String content = buildPdfContentStream(pages.get(i));
            int pageObject = pageObjectNumbers.get(i);
            int contentObject = contentObjectNumbers.get(i);

            offsets.add(out.size());
            writeObject(out, pageObject,
                    "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Resources << /Font << /F1 3 0 R >> >> /Contents "
                    + contentObject + " 0 R >>");

            offsets.add(out.size());
            writeStreamObject(out, contentObject, content.getBytes(StandardCharsets.US_ASCII));
        }

        int xrefOffset = out.size();
        out.write(("xref\n0 " + (totalObjects + 1) + "\n").getBytes(StandardCharsets.US_ASCII));
        out.write("0000000000 65535 f \n".getBytes(StandardCharsets.US_ASCII));
        for (Integer offset : offsets) {
            out.write(String.format("%010d 00000 n \n", offset).getBytes(StandardCharsets.US_ASCII));
        }
        out.write(("trailer\n<< /Size " + (totalObjects + 1) + " /Root 1 0 R >>\nstartxref\n" + xrefOffset + "\n%%EOF").getBytes(StandardCharsets.US_ASCII));

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(out.toByteArray());
        }
    }

    private static List<List<String>> paginate(String title, String printedAt, List<String> lines, int maxLinesPerPage) {
        List<List<String>> pages = new ArrayList<>();
        int index = 0;
        while (index < lines.size()) {
            List<String> page = new ArrayList<>();
            page.add(title);
            page.add(printedAt);
            page.add("");
            for (int count = 0; count < maxLinesPerPage && index < lines.size(); count++, index++) {
                page.add(lines.get(index));
            }
            pages.add(page);
        }
        return pages;
    }

    private static String buildPdfContentStream(List<String> lines) {
        StringBuilder sb = new StringBuilder();
        sb.append("BT\n/F1 10 Tf\n14 TL\n50 760 Td\n");
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) {
                sb.append("T*\n");
            }
            sb.append("(").append(escapePdf(lines.get(i))).append(") Tj\n");
        }
        sb.append("ET");
        return sb.toString();
    }

    private static List<String> buildTableLines(List<String> headers, List<List<String>> rows) {
        int[] widths = new int[headers.size()];
        for (int i = 0; i < headers.size(); i++) {
            widths[i] = Math.min(Math.max(headers.get(i).length(), 8), 24);
        }
        for (List<String> row : rows) {
            for (int i = 0; i < row.size(); i++) {
                widths[i] = Math.min(Math.max(widths[i], row.get(i).length()), 24);
            }
        }

        List<String> lines = new ArrayList<>();
        lines.add(joinPadded(headers, widths));
        lines.add(buildSeparator(widths));
        for (List<String> row : rows) {
            lines.add(joinPadded(row, widths));
        }
        return lines;
    }

    private static String joinPadded(List<String> values, int[] widths) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                sb.append(" | ");
            }
            sb.append(pad(values.get(i), widths[i]));
        }
        return sb.toString();
    }

    private static String buildSeparator(int[] widths) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < widths.length; i++) {
            if (i > 0) {
                sb.append("-+-");
            }
            for (int j = 0; j < widths[i]; j++) {
                sb.append("-");
            }
        }
        return sb.toString();
    }

    private static String pad(String value, int width) {
        String safe = value == null ? "" : value;
        if (safe.length() > width) {
            return safe.substring(0, Math.max(0, width - 1)) + "~";
        }
        StringBuilder sb = new StringBuilder(safe);
        while (sb.length() < width) {
            sb.append(' ');
        }
        return sb.toString();
    }

    private static String escapePdf(String text) {
        return text.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }

    private static void writeObject(ByteArrayOutputStream out, int objectNumber, String body) throws IOException {
        out.write((objectNumber + " 0 obj\n" + body + "\nendobj\n").getBytes(StandardCharsets.US_ASCII));
    }

    private static void writeStreamObject(ByteArrayOutputStream out, int objectNumber, byte[] streamBytes) throws IOException {
        out.write((objectNumber + " 0 obj\n<< /Length " + streamBytes.length + " >>\nstream\n").getBytes(StandardCharsets.US_ASCII));
        out.write(streamBytes);
        out.write("\nendstream\nendobj\n".getBytes(StandardCharsets.US_ASCII));
    }

    private static void writeSimpleXlsx(File file, JTable table, String title) throws IOException {
        List<String> headers = getHeaders(table);
        List<List<String>> rows = getRows(table);
        int[] widths = calculateWidths(headers, rows);

        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(file))) {
            writeZipEntry(zip, "[Content_Types].xml", buildContentTypesXml());
            writeZipEntry(zip, "_rels/.rels", buildRootRelsXml());
            writeZipEntry(zip, "xl/workbook.xml", buildWorkbookXml(title));
            writeZipEntry(zip, "xl/_rels/workbook.xml.rels", buildWorkbookRelsXml());
            writeZipEntry(zip, "xl/styles.xml", buildStylesXml());
            writeZipEntry(zip, "xl/worksheets/sheet1.xml", buildSheetXml(headers, rows, widths, title));
        }
    }

    private static int[] calculateWidths(List<String> headers, List<List<String>> rows) {
        int[] widths = new int[headers.size()];
        for (int i = 0; i < headers.size(); i++) {
            widths[i] = headers.get(i).length();
        }
        for (List<String> row : rows) {
            for (int i = 0; i < row.size(); i++) {
                widths[i] = Math.max(widths[i], row.get(i).length());
            }
        }
        for (int i = 0; i < widths.length; i++) {
            widths[i] = Math.min(Math.max(widths[i] + 2, 12), 40);
        }
        return widths;
    }

    private static String buildContentTypesXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">"
                + "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>"
                + "<Default Extension=\"xml\" ContentType=\"application/xml\"/>"
                + "<Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>"
                + "<Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>"
                + "<Override PartName=\"/xl/styles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml\"/>"
                + "</Types>";
    }

    private static String buildRootRelsXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/>"
                + "</Relationships>";
    }

    private static String buildWorkbookXml(String title) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" "
                + "xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">"
                + "<sheets><sheet name=\"" + escapeXml(trimSheetName(title)) + "\" sheetId=\"1\" r:id=\"rId1\"/></sheets>"
                + "</workbook>";
    }

    private static String buildWorkbookRelsXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/>"
                + "<Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\" Target=\"styles.xml\"/>"
                + "</Relationships>";
    }

    private static String buildStylesXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<styleSheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">"
                + "<fonts count=\"2\">"
                + "<font><sz val=\"11\"/><name val=\"Segoe UI\"/></font>"
                + "<font><b/><sz val=\"11\"/><name val=\"Segoe UI\"/><color rgb=\"FFFFFFFF\"/></font>"
                + "</fonts>"
                + "<fills count=\"3\">"
                + "<fill><patternFill patternType=\"none\"/></fill>"
                + "<fill><patternFill patternType=\"gray125\"/></fill>"
                + "<fill><patternFill patternType=\"solid\"><fgColor rgb=\"FF21618C\"/><bgColor indexed=\"64\"/></patternFill></fill>"
                + "</fills>"
                + "<borders count=\"1\"><border><left/><right/><top/><bottom/><diagonal/></border></borders>"
                + "<cellStyleXfs count=\"1\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\"/></cellStyleXfs>"
                + "<cellXfs count=\"2\">"
                + "<xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\" xfId=\"0\"/>"
                + "<xf numFmtId=\"0\" fontId=\"1\" fillId=\"2\" borderId=\"0\" xfId=\"0\" applyFont=\"1\" applyFill=\"1\"/>"
                + "</cellXfs>"
                + "</styleSheet>";
    }

    private static String buildSheetXml(List<String> headers, List<List<String>> rows, int[] widths, String title) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">");
        sb.append("<cols>");
        for (int i = 0; i < widths.length; i++) {
            sb.append("<col min=\"").append(i + 1).append("\" max=\"").append(i + 1)
                    .append("\" width=\"").append(widths[i]).append("\" customWidth=\"1\"/>");
        }
        sb.append("</cols>");
        sb.append("<sheetData>");
        appendRow(sb, 1, listOf(title), 0);
        appendRow(sb, 2, listOf("Dicetak: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())), 0);
        appendRow(sb, 4, headers, 1);
        int rowNumber = 5;
        for (List<String> row : rows) {
            appendRow(sb, rowNumber++, row, 0);
        }
        sb.append("</sheetData></worksheet>");
        return sb.toString();
    }

    private static void appendRow(StringBuilder sb, int rowNumber, List<String> values, int style) {
        sb.append("<row r=\"").append(rowNumber).append("\">");
        for (int i = 0; i < values.size(); i++) {
            sb.append("<c r=\"").append(columnName(i)).append(rowNumber).append("\" t=\"inlineStr\" s=\"").append(style).append("\">")
                    .append("<is><t>").append(escapeXml(values.get(i))).append("</t></is></c>");
        }
        sb.append("</row>");
    }

    private static List<String> listOf(String single) {
        List<String> list = new ArrayList<>();
        list.add(single);
        return list;
    }

    private static String columnName(int index) {
        StringBuilder sb = new StringBuilder();
        int current = index + 1;
        while (current > 0) {
            int rem = (current - 1) % 26;
            sb.insert(0, (char) ('A' + rem));
            current = (current - 1) / 26;
        }
        return sb.toString();
    }

    private static List<String> getHeaders(JTable table) {
        List<String> headers = new ArrayList<>();
        for (int i = 0; i < table.getColumnCount(); i++) {
            headers.add(String.valueOf(table.getColumnName(i)));
        }
        return headers;
    }

    private static List<List<String>> getRows(JTable table) {
        List<List<String>> rows = new ArrayList<>();
        for (int row = 0; row < table.getRowCount(); row++) {
            List<String> values = new ArrayList<>();
            for (int col = 0; col < table.getColumnCount(); col++) {
                Object value = table.getValueAt(row, col);
                values.add(value == null ? "" : String.valueOf(value));
            }
            rows.add(values);
        }
        return rows;
    }

    private static void writeZipEntry(ZipOutputStream zip, String name, String content) throws IOException {
        zip.putNextEntry(new ZipEntry(name));
        zip.write(content.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    private static String sanitizeFileName(String text) {
        return text.replaceAll("[^a-zA-Z0-9-_]+", "_");
    }

    private static String trimSheetName(String title) {
        String safe = title.replaceAll("[\\\\/*?:\\[\\]]", "");
        return safe.length() > 31 ? safe.substring(0, 31) : safe;
    }

    private static String escapeXml(String text) {
        return text == null ? "" : text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
