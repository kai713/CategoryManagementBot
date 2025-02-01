package com.kairgaliyev.telegrambotdemo.service;

import java.io.IOException;
import java.io.InputStream;

public interface ICategoryExcelService {

    byte[] exportToExcel(Long chatId) throws IOException;

    void importFromExcel(InputStream inputStream, Long chatId) throws Exception;
}
