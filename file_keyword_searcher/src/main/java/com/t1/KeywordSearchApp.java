package com.t1;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KeywordSearchApp {

    enum FileType {
        TXT("txt"),
        LOG("log"),
        XML("xml"),
        CSV("csv");

        private final String extension;

        FileType(String extension) {
            this.extension = extension;
        }

        public String getExtension() {
            return extension;
        }

    }


    private static void runApp() {
        JFileChooser folderChooser = new JFileChooser();
        folderChooser.setDialogTitle("選擇資料夾");
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // 選擇資料夾
        if (folderChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {//使用者 沒點擊APPROVE_OPTION「儲存」，則中斷程式
            return;
        }
        //延伸: 選擇檔案	APPROVE_OPTION；按「取消」或關閉視窗	CANCEL_OPTION；發生錯誤	ERROR_OPTION

        File selectedFolder  = folderChooser.getSelectedFile();

        // 輸入關鍵字
        String keyword = JOptionPane.showInputDialog("請輸入搜尋關鍵字:");
        if (keyword == null || keyword.isEmpty()) {
            JOptionPane.showMessageDialog(null, "關鍵字不可為空");
            return;
        }

        // 選擇輸出檔案
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("選擇輸出檔案位置");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        if (fileChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) { //使用者 沒點擊APPROVE_OPTION「儲存」，則中斷程式
            return;
        }

        File outputFile = fileChooser.getSelectedFile();

        if (!outputFile.getName().endsWith(".txt")) {
            outputFile = new File(outputFile.getAbsolutePath() + ".txt");
        }

        processFiles(selectedFolder.toPath(), keyword, outputFile.toPath());


    }

    private static void processFiles(Path folderPath, String keyword, Path outputPath)  {

        try (BufferedWriter writer =Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            List<Path> files = Files.walk(folderPath)
                    .filter(Files::isRegularFile)
                    .filter(KeywordSearchApp::isAcceptedFileType)  // 過濾指定類型的檔案
                    .collect(Collectors.toList());

            for (Path file : files) {
                processFile(file, keyword, writer);
            }
            JOptionPane.showMessageDialog(null, "搜尋完成，結果已儲存於：" + outputPath);

            // **自動開啟儲存結果的資料夾**
            Desktop.getDesktop().open(outputPath.toFile());

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "發生錯誤: " + e.getMessage());
        }
    }

    private static void processFile(Path file, String keyword, BufferedWriter writer) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                int index = line.indexOf(keyword);
                while (index != -1) {
                    int endIndex = Math.min(index + keyword.length() + 100, line.length()); //關鍵字後100字元寫入
                    String result = file.getFileName() + "//    " + line.substring(index, endIndex)+"\n";
                    writer.write(result);
                    writer.newLine();
                    index = line.indexOf(keyword, index + 1);
                }
            }
        }
    }

    private static boolean isAcceptedFileType(Path file) {
        String fileName = file.getFileName().toString().toLowerCase();
        for (FileType fileType : FileType.values()) {
            if (fileName.endsWith("." + fileType.getExtension())) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(KeywordSearchApp::runApp);
    }
}
