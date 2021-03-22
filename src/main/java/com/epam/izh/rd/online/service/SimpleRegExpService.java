package com.epam.izh.rd.online.service;

import java.io.*;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleRegExpService implements RegExpService {

    /**
     * Метод должен читать файл sensitive_data.txt (из директории resources) и маскировать в нем конфиденциальную информацию.
     * Номер счета должен содержать только первые 4 и последние 4 цифры (1234 **** **** 5678). Метод должен содержать регулярное
     * выражение для поиска счета.
     *
     * @return обработанный текст
     */
    @Override
    public String maskSensitiveData() {
        StringBuilder res = getFileData();
        Pattern pattern = Pattern.compile("(\\d{4}\\s){3}\\d{4}");
        Matcher matcher = pattern.matcher(res);
        while (matcher.find()) {
            res.replace(matcher.start() + 5, matcher.end() - 5, "**** ****");
        }
        return res.toString();
    }

    /**
     * Метод должен считыввать файл sensitive_data.txt (из директории resources) и заменять плейсхолдер ${payment_amount} и ${balance} на заданные числа. Метод должен
     * содержать регулярное выражение для поиска плейсхолдеров
     *
     * @return обработанный текст
     */
    @Override
    public String replacePlaceholders(double paymentAmount, double balance) {
        String res = getFileData().toString();
        return res
                .replaceAll("\\$\\{payment_amount}", "" + (int) paymentAmount)
                .replaceAll("\\$\\{balance}", "" + (int) balance);
    }

    private StringBuilder getFileData() {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource("sensitive_data.txt");
        StringBuilder res = new StringBuilder();
        try (FileReader reader = new FileReader(resource.getFile());
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            bufferedReader.lines().forEach(res::append);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
}
