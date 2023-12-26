package com.madicetc.mycrypto;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import java.io.File;

public class CryptoController {
    @FXML private TextField keyField; // Поле для ввода ключа шифрования
    @FXML private Label statusLabel;  // Метка для отображения статуса операции
    @FXML private Label fileInfoLabel; // Метка для отображения информации о файле


    private File lastUsedDirectory;
    private File selectedFile; // Выбранный файл для шифрования/дешифрования

    // Обработчик события нажатия на кнопку "Выбрать файл"
    @FXML
    private void handleChooseFile() {
        FileChooser fileChooser = new FileChooser();

        // Установка начальной директории, если она была сохранена
        if (lastUsedDirectory != null) {
            fileChooser.setInitialDirectory(lastUsedDirectory);
        }

        selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            fileInfoLabel.setText("Файл: " + selectedFile.getName());

            // Сохраняем путь к каталогу выбранного файла
            lastUsedDirectory = selectedFile.getParentFile();
        } else {
            fileInfoLabel.setText("Файл не выбран");
        }
    }


    // Обработчик события нажатия на кнопку "Шифровать"
    @FXML
    private void handleEncrypt() {
        if (selectedFile == null) {
            statusLabel.setText("Ошибка: Файл не выбран");
            return;
        }
        try {
            String key = keyField.getText();
            EncryptionUtils.encryptFile(selectedFile, key);
            statusLabel.setText("Файл зашифрован!");

            // Очистка полей после операции
            clearFields();
        } catch (Exception e) {
            statusLabel.setText("Ошибка: " + e.getMessage());
        }
    }

    // Обработчик события нажатия на кнопку "Дешифровать"
    @FXML
    private void handleDecrypt() {
        if (selectedFile == null) {
            statusLabel.setText("Ошибка: Файл не выбран");
            return;
        }
        try {
            String key = keyField.getText();
            EncryptionUtils.decryptFile(selectedFile, key);
            statusLabel.setText("Файл расшифрован успешно!");

            // Очистка полей после операции
            clearFields();
        } catch (Exception e) {
            statusLabel.setText("Ошибка: " + e.getMessage());
        }
    }

    // Метод для очистки полей ввода и информационных меток
    private void clearFields() {
        fileInfoLabel.setText("Файл не выбран");
        keyField.setText("");
        selectedFile = null;
    }
}
