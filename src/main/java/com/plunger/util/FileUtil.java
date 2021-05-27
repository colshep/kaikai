package com.plunger.util;

import sun.misc.BASE64Encoder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FileUtil {

    private static final String uploadPath = "upload/";

    public static String getUploadPath() {
        return uploadPath + LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd")) + "/";
    }

    public static String getAbsolutePath(String path) {
        return (isWindows() ? "D:/" : "/") + path;
    }

    public static boolean isWindows() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            return true;
        }
        return false;
    }

    public static String imgToBase64(String imgUrl) {
        InputStream inputStream;
        byte[] data = null;
        try {
            inputStream = new FileInputStream(imgUrl);
            data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 加密
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
    }
}
