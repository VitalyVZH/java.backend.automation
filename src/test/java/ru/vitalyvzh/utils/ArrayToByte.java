package ru.vitalyvzh.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class ArrayToByte {

    public byte[] getFileContent(String path) {
        ClassLoader classLoader = getClass().getClassLoader();
        File inputFile = new File(classLoader.getResource(path).getFile());

        byte[] fileContent = new byte[0];
        try {
            fileContent = FileUtils.readFileToByteArray(inputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContent;
    }
}
