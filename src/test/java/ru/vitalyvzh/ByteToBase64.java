package ru.vitalyvzh;

import java.util.Base64;

public class ByteToBase64 {

    public String fileString(String filePath) {
        ArrayToByte arrayToByte = new ArrayToByte();
        byte[] fileContent = arrayToByte.getFileContent(filePath);
        return Base64.getEncoder().encodeToString(fileContent);
    }
}
