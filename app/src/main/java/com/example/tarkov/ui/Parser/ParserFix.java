package com.example.tarkov.ui.Parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class ParserFix {
    private static final String DIRECTORY_NAME = "MyAppDirectoryName";
    private static final String FILE_NAME = "news.html";

    public static List<NewsItem> parseEftNews(Context context) {
        // Загрузка HTML-кода в файл
        downloadHtmlToFile(context, "https://www.escapefromtarkov.com/news?lang=ru");

        // Парсинг новостей из файла
        List<NewsItem> newsItems = parseEftNewsFromFile(context,5);

        return newsItems;
    }

    private static String getExternalFilePath(Context context) {
        File directory = new File(context.getExternalFilesDir(null), DIRECTORY_NAME);
        if (!directory.exists()) {
            directory.mkdirs(); // Создать директорию, если она не существует
        }
        File file = new File(directory, FILE_NAME);
        return file.getAbsolutePath();
    }

    private static void downloadHtmlToFile(Context context, String url) {
        String filePath = getExternalFilePath(context);
        try {
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                    .get();
            String htmlContent = document.html();
            File file = new File(filePath);

            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            osw.write(htmlContent);
            osw.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Произошла ошибка при обновлении HTML-кода.");
        }
    }

    public static List<NewsItem> parseEftNewsFromFile(Context context, int numberOfNews) {
        List<NewsItem> newsItems = new ArrayList<>();
        try {
            File input = new File(getExternalFilePath(context));
            Document document = Jsoup.parse(input, "UTF-8");

            Elements newsElements = document.select("#news-list .container");

            if (newsElements.isEmpty()) {
                System.out.println("На странице не найдены элементы новостей.");
                return null;
            }

            // Ограничиваем количество новостей
            int count = 0;
            for (Element newsElement : newsElements) {
                if (count >= numberOfNews) {
                    break;
                }

                String title = newsElement.select(".headtext a").text();
                String date = newsElement.select(".headtext span").text();
                String partialContent = newsElement.select(".description").text();
                String imageUrl = newsElement.select(".image img").attr("src");

                // Добавляем ссылку на полную новость
                String fullNewsLink = "https://www.escapefromtarkov.com" + newsElement.select(".headtext a").attr("href");

                NewsItem newsItem = new NewsItem(title, date, partialContent, imageUrl, fullNewsLink);
                newsItems.add(newsItem);

                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Произошла ошибка при парсинге HTML-кода из файла.");
        }
        return newsItems;
    }


    public static class NewsItem {
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

        public String getTitle() {
            return title;
        }

        public String getDate() {
            return date;
        }

        public String getPartialContent() {
            return partialContent;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getFullNewsLink() {
            return fullNewsLink;
        }

        @Override
        public String toString() {
            return "Заголовок: " + title + "\nДата: " + date + "\nЧастичное содержание: " + partialContent + "\nURL изображения: " + imageUrl + "\nСсылка на полную новость: " + fullNewsLink;
        }
    }
}
