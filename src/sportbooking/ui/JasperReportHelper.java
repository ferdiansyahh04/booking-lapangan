package sportbooking.ui;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

public class JasperReportHelper {

    // Get absolute path based on project root (where the jar is run from)
    private static String getProjectRoot() {
        // Try to find project root by looking for 'reports' folder
        String userDir = System.getProperty("user.dir");
        File reportsDir = new File(userDir, "reports");
        if (reportsDir.exists() && reportsDir.isDirectory()) {
            return new File(userDir).getAbsolutePath();
        }
        // Fallback: try parent directory
        File parentDir = new File(userDir).getParentFile();
        if (parentDir != null) {
            reportsDir = new File(parentDir, "reports");
            if (reportsDir.exists() && reportsDir.isDirectory()) {
                return parentDir.getAbsolutePath();
            }
        }
        return new File(userDir).getAbsolutePath();
    }

    private static final String REPORT_PATH = new File(getProjectRoot(), "reports").getAbsolutePath() + File.separator;
    private static final String OUTPUT_PATH = new File(getProjectRoot(), "reports" + File.separator + "output").getAbsolutePath() + File.separator;

    private JasperReportHelper() {
    }

    /**
     * Export report ke PDF - metode utama (tanpa preview JasperViewer)
     */
    public static void exportToPdf(String jrxmlFile, Map<String, Object> params,
            Connection conn, java.awt.Component parent, String defaultFileName) {
        System.out.println("REPORT_PATH: " + REPORT_PATH);
        System.out.println("Looking for: " + REPORT_PATH + jrxmlFile);

        // Buat output directory jika belum ada
        new File(OUTPUT_PATH).mkdirs();

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Simpan Laporan PDF");
        chooser.setSelectedFile(new File(defaultFileName + ".pdf"));
        chooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));

        if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File outputFile = chooser.getSelectedFile();
        if (!outputFile.getName().toLowerCase().endsWith(".pdf")) {
            outputFile = new File(outputFile.getAbsolutePath() + ".pdf");
        }

        try {
            File reportFile = new File(REPORT_PATH, jrxmlFile);
            if (!reportFile.exists()) {
                JOptionPane.showMessageDialog(parent,
                    "Template laporan tidak ditemukan:\n" + reportFile.getAbsolutePath(),
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            System.out.println("Loading report: " + reportFile.getAbsolutePath());
            JasperDesign design = JRXmlLoader.load(reportFile);
            System.out.println("Compiling report...");
            JasperReport jasperReport = JasperCompileManager.compileReport(design);
            System.out.println("Filling report with data...");
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, conn);
            System.out.println("Exporting to PDF: " + outputFile.getAbsolutePath());
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputFile.getAbsolutePath());
            System.out.println("Done! PDF saved to: " + outputFile.getAbsolutePath());

            JOptionPane.showMessageDialog(parent,
                "PDF berhasil disimpan:\n" + outputFile.getAbsolutePath(),
                "Sukses", JOptionPane.INFORMATION_MESSAGE);

        } catch (JRException e) {
            JOptionPane.showMessageDialog(parent, "Gagal export PDF:\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(new PrintWriter(System.err));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent, "Error:\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(new PrintWriter(System.err));
        }
    }

    /**
     * Export report ke PDF dengan timestamp otomatis
     */
    public static void exportToPdfAuto(String jrxmlFile, Map<String, Object> params,
            Connection conn, java.awt.Component parent, String filePrefix) {
        System.out.println("REPORT_PATH (auto): " + REPORT_PATH);
        new File(OUTPUT_PATH).mkdirs();

        String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss")
            .format(new java.util.Date());
        String fileName = OUTPUT_PATH + filePrefix + "_" + timestamp + ".pdf";

        try {
            File file = new File(REPORT_PATH + jrxmlFile);
            if (!file.exists()) {
                JOptionPane.showMessageDialog(parent,
                    "Template laporan tidak ditemukan:\n" + file.getAbsolutePath(),
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JasperDesign design = JRXmlLoader.load(file);
            JasperReport jasperReport = JasperCompileManager.compileReport(design);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, conn);

            JasperExportManager.exportReportToPdfFile(jasperPrint, fileName);

            JOptionPane.showMessageDialog(parent,
                "PDF berhasil disimpan:\n" + fileName,
                "Sukses", JOptionPane.INFORMATION_MESSAGE);

        } catch (JRException e) {
            JOptionPane.showMessageDialog(parent, "Gagal export PDF:\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(new PrintWriter(System.err));
        }
    }

    /**
     * Create common parameters untuk laporan
     */
    public static Map<String, Object> createParams(String title, String dari, String sampai) {
        Map<String, Object> params = new HashMap<>();
        params.put("REPORT_TITLE", title);
        params.put("PERIODE_DARI", dari);
        params.put("PERIODE_SAMPAI", sampai);
        params.put("PRINTED_DATE", new java.util.Date());
        params.put("PRINTED_BY", "Sport Booking System");
        return params;
    }

    /**
     * Create parameters untuk laporan tanpa periode
     */
    public static Map<String, Object> createParams(String title) {
        Map<String, Object> params = new HashMap<>();
        params.put("REPORT_TITLE", title);
        params.put("PRINTED_DATE", new java.util.Date());
        params.put("PRINTED_BY", "Sport Booking System");
        return params;
    }

    /**
     * Generate report dan return JasperPrint object (untuk custom handling)
     */
    public static JasperPrint generateReport(String jrxmlFile, Map<String, Object> params,
            Connection conn) throws JRException {
        File file = new File(REPORT_PATH, jrxmlFile);
        System.out.println("Generating report from: " + file.getAbsolutePath());
        if (!file.exists()) {
            throw new JRException("Template laporan tidak ditemukan: " + file.getAbsolutePath());
        }

        JasperDesign design = JRXmlLoader.load(file);
        JasperReport jasperReport = JasperCompileManager.compileReport(design);
        return JasperFillManager.fillReport(jasperReport, params, conn);
    }

    /**
     * Check apakah template report exists
     */
    public static boolean reportExists(String jrxmlFile) {
        File f = new File(REPORT_PATH, jrxmlFile);
        System.out.println("Checking report exists: " + f.getAbsolutePath() + " = " + f.exists());
        return f.exists();
    }

    /**
     * Get list available reports
     */
    public static String[] getAvailableReports() {
        File reportsDir = new File(REPORT_PATH);
        System.out.println("Reports dir: " + reportsDir.getAbsolutePath());
        if (!reportsDir.exists() || !reportsDir.isDirectory()) {
            return new String[0];
        }

        File[] files = reportsDir.listFiles((dir, name) ->
            name.endsWith(".jrxml") && !name.startsWith("_"));

        if (files == null || files.length == 0) {
            return new String[0];
        }

        String[] reports = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            reports[i] = files[i].getName();
        }

        return reports;
    }
}