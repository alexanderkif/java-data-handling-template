package com.epam.izh.rd.online.repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Optional;

public class SimpleFileRepository implements FileRepository {

    /**
     * Метод рекурсивно подсчитывает количество файлов в директории
     *
     * @param path путь до директори
     * @return файлов, в том числе скрытых
     */
    @Override
    public long countFilesInDirectory(String path) {
        URL resource = getClass().getClassLoader().getResource(path);
        File dir = new File(resource.getFile());
        if (!dir.exists()) return 0L;
        if (dir.isFile()) return 1L;
        if (dir.isDirectory()) {
            File[] dirs = dir.listFiles();
            if (dirs == null || dirs.length == 0) return 0L;
            return Arrays.stream(dirs)
                    .map(d -> countFilesInDirectory(path + "/" + d.getName()))
                    .reduce(Long::sum).orElse(0L);
        }
        return 0L;
    }

    /**
     * Метод рекурсивно подсчитывает количество папок в директории, считая корень
     *
     * @param path путь до директории
     * @return число папок
     */
    @Override
    public long countDirsInDirectory(String path) {
        URL resource = getClass().getClassLoader().getResource(path);
        File dir = new File(resource.getFile());
        if (!dir.exists()) return 0L;
        if (dir.isFile()) return 0L;
        if (dir.isDirectory()) {
            File[] dirs = dir.listFiles();
            if (dirs == null || dirs.length == 0) return 1L;
            return Arrays.stream(dirs)
                    .map(d -> countDirsInDirectory(path + "/" + d.getName()))
                    .reduce(Long::sum).orElse(0L) + 1L;
        }
        return 0L;
    }

    /**
     * Метод копирует все файлы с расширением .txt
     *
     * @param from путь откуда
     * @param to   путь куда
     */
    @Override
    public void copyTXTFiles(String from, String to) {
        Path originalPath = Paths.get(from).getParent();
        Path copiedPath = Paths.get(to).getParent();
        File dir = new File(originalPath.toString());
        File[] dirs = dir.listFiles();
        if (dirs == null || dirs.length == 0) return;
        try {
            Files.createDirectories(copiedPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Arrays.stream(dirs)
                .map(File::getName)
                .filter(n -> n.contains(".")
                        && n.substring(n.lastIndexOf(".") + 1).equals("txt"))
                .forEach(n -> {
                    try {
                        Path original = Paths.get(originalPath + "/" + n);
                        Path copied = Paths.get(copiedPath + "/" + n);
                        Files.copy(original, copied, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    /**
     * Метод создает файл на диске с расширением txt
     *
     * @param path путь до нового файла
     * @param name имя файла
     * @return был ли создан файл
     */
    @Override
    public boolean createFile(String path, String name) {
        URL resource = getClass().getClassLoader().getResource("");
        File dir = new File(resource.getFile() + "/" + path);
        File file = new File(resource.getPath() + "/" + path, name);
        try {
            if (dir.mkdirs() || dir.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.exists();
    }

    /**
     * Метод считывает тело файла .txt из папки src/main/resources
     *
     * @param fileName имя файла
     * @return контент
     */
    @Override
    public String readFileFromResources(String fileName) {
        URL resource = getClass().getClassLoader().getResource(fileName);
        StringBuilder res = new StringBuilder();
        try (FileReader reader = new FileReader(resource.getFile());
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            bufferedReader.lines().forEach(res::append);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res.toString();
    }
}
