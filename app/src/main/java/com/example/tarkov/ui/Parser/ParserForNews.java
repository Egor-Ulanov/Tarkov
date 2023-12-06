package com.example.tarkov.ui.Parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ParserForNews {
    private static String htmlContent = "";
    private static final int UPDATE_INTERVAL_MINUTES = 10;

    public static void main(String[] args) {
        System.out.println("Парсинг новостей EFT...");

        final String[] htmlContent = {getHtmlContent("https://www.escapefromtarkov.com/news?lang=ru")};

        if (htmlContent[0] != null) {
            List<NewsItem> newsItems = parseEftNews(htmlContent[0]);

            if (newsItems != null && !newsItems.isEmpty()) {
                System.out.println("Новости EFT успешно спарсены.");
                // Выводим первые 3 новости
                for (int i = 0; i < Math.min(3, newsItems.size()); i++) {
                    System.out.println(newsItems.get(i));
                    System.out.println("---------------------");
                }
            } else {
                System.out.println("Новости не были спарсены или произошла ошибка.");
            }

            // Запускаем таймер для обновления HTML-кода
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    htmlContent[0] = getHtmlContent("https://www.escapefromtarkov.com/news?lang=ru");
                }
            }, 0, UPDATE_INTERVAL_MINUTES * 60 * 1000); // Периодически обновляем каждые 10 минут
        }
    }

    // Метод для получения HTML-кода с веб-сайта
    private static String getHtmlContent(String url) {
        try {
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                    .get();
            return document.html();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Произошла ошибка при обновлении HTML-кода.");
            return null;
        }
    }

    // Метод для парсинга новостей из HTML-кода
    private static List<NewsItem> parseEftNews(String htmlContent) {
        List<NewsItem> newsItems = new ArrayList<>();
        if (htmlContent == null) {
            return null;
        }

        Document document = Jsoup.parse(htmlContent);

        Elements newsElements = document.select("#news-list .container");

        if (newsElements.isEmpty()) {
            System.out.println("На странице не найдены элементы новостей.");
            return null;
        }

        for (Element newsElement : newsElements) {
            String title = newsElement.select(".headtext a").text();
            String date = newsElement.select(".headtext span").text();
            String partialContent = newsElement.select(".description").text();
            String imageUrl = newsElement.select(".image img").attr("src");

            // Добавляем ссылку на полную новость
            String fullNewsLink = "https://www.escapefromtarkov.com" + newsElement.select(".headtext a").attr("href");

            NewsItem newsItem = new NewsItem(title, date, partialContent, imageUrl, fullNewsLink);
            newsItems.add(newsItem);
        }
        return newsItems;
    }

    // Внутренний класс для представления новости
    private static class NewsItem {
        private String title;
        private String date;
        private String partialContent;
        private String imageUrl;
        private String fullNewsLink;

        public NewsItem(String title, String date, String partialContent, String imageUrl, String fullNewsLink) {
            this.title = title;
            this.date = date;
            this.partialContent = partialContent;
            this.imageUrl = imageUrl;
            this.fullNewsLink = fullNewsLink;
        }

        @Override
        public String toString() {
            return "Заголовок: " + title + "\nДата: " + date + "\nЧастичное содержание: " + partialContent + "\nURL изображения: " + imageUrl + "\nСсылка на полную новость: " + fullNewsLink;
        }
    }
}
