package com.madicetc.mycrypto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Random;

public class EncryptionUtils {

    // Метод для ширфрования файлов
    public static void encryptFile(File file, String key) throws IOException {
        byte[] fileContent = Files.readAllBytes(file.toPath());

        // Сначала применяем ваш метод шифрования
        byte[] customEncrypted = customEncrypt(fileContent);
        // Затем применяем XOR-шифрование
        byte[] xorEncrypted = xorWithKey(customEncrypted, key.getBytes());

        File encryptedFile = new File(file.getPath() + ".madic");
        try (FileOutputStream stream = new FileOutputStream(encryptedFile)) {
            stream.write(xorEncrypted);
        }
        if (!file.delete()) {
            throw new IOException("Unable to delete original file.");
        }
    }

    // Метод для расшифровки файлов
    public static void decryptFile(File file, String key) throws IOException {
        byte[] fileContent = Files.readAllBytes(file.toPath());

        // Сначала применяем XOR-дешифрование
          byte[] xorDecrypted = xorWithKey(fileContent, key.getBytes());
        // Затем применяем ваш метод дешифрования
        byte[] customDecrypted = customDecrypt(xorDecrypted);

        String originalFileName = file.getPath().replace(".madic", "");
        File originalFile = new File(originalFileName);
        try (FileOutputStream stream = new FileOutputStream(originalFile)) {
            stream.write(customDecrypted);
        }
        if (!file.delete()) {
            throw new IOException("Unable to delete encrypted file.");
        }
    }

    private static byte[] xorWithKey(byte[] data, byte[] key) {
        byte[] res = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            res[i] = (byte) (data[i] ^ key[i % key.length]);
        }
        return res;
    }


    /**
     * Шифрует данные, добавляя случайные байты перед и после каждого сегмента исходных данных.
     * Алгоритм делит исходные данные на сегменты и обрамляет каждый сегмент случайными байтами.
     * Это создает блоки фиксированного размера, каждый из которых содержит часть исходных данных и случайные байты.
     *
     * @param data Массив байтов исходных данных, которые необходимо зашифровать.
     * @return Зашифрованный массив байтов.
     */

    private static byte[] customEncrypt(byte[] data) {
        int start = 20; // Количество случайных байтов в начале каждого блока
        int size = 100; // Размер блока
        int stlen = 30; // Длина сегмента реальных данных в блоке

        // Расчет нового размера с учетом заполнения
        byte[] result = new byte[(data.length / stlen + 1) * size];
        Random random = new Random();

        int dataIndex = 0, resultIndex = 0;
        while (dataIndex < data.length) {
            // Добавляем случайные байты в начало каждого блока
            for (int i = 0; i < start; i++) {
                result[resultIndex++] = (byte) random.nextInt(256);
            }
            // Копируем реальные данные в блок
            int bytesToCopy = Math.min(stlen, data.length - dataIndex);
            System.arraycopy(data, dataIndex, result, resultIndex, bytesToCopy);
            dataIndex += bytesToCopy;
            resultIndex += stlen;

            // Добавляем случайные байты в конец блока, если требуется
            while (resultIndex % size != 0) {
                result[resultIndex++] = (byte) random.nextInt(256);
            }
        }
        return result;
    }


    /**
     * Дешифрует данные, обработанные методом customEncrypt.
     * Алгоритм удаляет добавленные случайные байты из каждого блока, восстанавливая исходные данные.
     * Поскольку исходная структура данных была изменена добавлением случайных байтов, метод извлекает только те части блоков,
     * которые содержат реальные данные, пропуская случайные байты.
     *
     * @param encryptedData Зашифрованный массив байтов, который необходимо дешифровать.
     * @return Дешифрованный массив байтов, соответствующий исходным данным.
     */

    private static byte[] customDecrypt(byte[] encryptedData) {
        int start = 20; // Количество случайных байтов в начале каждого блока
        int size = 100; // Размер блока
        int stlen = 30; // Длина сегмента реальных данных в блоке

        // Расчет размера исходных данных
        byte[] result = new byte[(encryptedData.length / size) * stlen];

        int resultIndex = 0;
        for (int i = 0; i < encryptedData.length; i += size) {
            System.arraycopy(encryptedData, i + start, result, resultIndex, stlen);
            resultIndex += stlen;
        }
        // Обрезаем лишние байты в конце, если они есть
        int realLength = resultIndex;
        for (int i = result.length - 1; i >= 0; i--) {
            if (result[i] != 0) {
                realLength = i + 1;
                break;
            }
        }
        return Arrays.copyOf(result, realLength);
    }



}
