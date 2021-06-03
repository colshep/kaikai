package com.plunger.util;

import com.plunger.config.FilePathProperties;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class FileUtil {

    private static FilePathProperties filePathProperties;

    @Resource
    public void setFilePathProperties(FilePathProperties filePathProperties) {
        FileUtil.filePathProperties = filePathProperties;
    }

    public static String getUploadPath() {
        return filePathProperties.getUploadPath() + LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd")) + "/";
    }

    public static String getResultFilePath() {
        return filePathProperties.getResultPath();
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
