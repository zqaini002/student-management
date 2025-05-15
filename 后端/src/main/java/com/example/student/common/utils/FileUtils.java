package com.example.student.common.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件工具类
 */
public class FileUtils {

    /**
     * 上传文件
     *
     * @param file 文件
     * @param uploadDir 上传目录
     * @return 文件名
     * @throws IOException IO异常
     */
    public static String upload(MultipartFile file, String uploadDir) throws IOException {
        // 获取文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IOException("文件名为空");
        }
        
        // 获取文件后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        
        // 使用UUID生成新的文件名
        String fileName = UUID.randomUUID().toString().replaceAll("-", "") + suffix;
        
        // 按日期创建目录
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        // 确保目录分隔符为当前系统格式
        String targetDir = uploadDir.replace('/', File.separatorChar) + File.separator + datePath.replace('/', File.separatorChar);
        
        // 创建目录
        File dir = new File(targetDir);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                throw new IOException("无法创建目录: " + targetDir);
            }
        }
        
        // 保存文件
        File targetFile = new File(dir, fileName);
        file.transferTo(targetFile);
        
        // 返回文件相对路径（包含日期路径），使用/作为URL路径分隔符
        return datePath.replace('\\', '/') + "/" + fileName;
    }
    
    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return 是否成功
     */
    public static boolean delete(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            return file.delete();
        }
        return false;
    }
} 