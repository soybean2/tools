package com.syb.netty.c2;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: sun
 * @Date: 2023/11/12/22:28
 * Files.walkFileTree()遍历文件夹
 */
public class TestFilesWalkFileTree {
    public static void main(String[] args) throws IOException {
        final AtomicInteger dirCount = new AtomicInteger();
        final AtomicInteger fileCount = new AtomicInteger();
        Files.walkFileTree(Paths.get("C:\\Users\\12420\\Desktop\\ceshi\\java\\code"), new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("====>" + dir);
                dirCount.incrementAndGet();
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println("====>" + file);
                fileCount.incrementAndGet();
                return super.visitFile(file, attrs);
            }
        });

        System.out.println("文件夹数量：" + dirCount.get());
        System.out.println("文件数量：" + fileCount.get());
    }
}
