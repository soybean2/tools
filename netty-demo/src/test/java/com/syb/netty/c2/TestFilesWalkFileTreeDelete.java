package com.syb.netty.c2;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @Author: sun
 * @Date: 2023/11/12/22:38
 * 删除文件夹
 */
public class TestFilesWalkFileTreeDelete {
    public static void main(String[] args) throws IOException {
        Files.walkFileTree(Paths.get("C:\\Users\\12420\\Downloads\\DOC"), new SimpleFileVisitor<Path>(){

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println("====>访问" + file);
                Files.delete(file);
                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                System.out.println("====>离开" + dir);
                Files.delete(dir);
                return super.postVisitDirectory(dir, exc);
            }
        });
    }
}
