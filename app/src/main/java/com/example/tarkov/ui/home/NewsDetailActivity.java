    package com.example.tarkov.ui.home;

    import android.content.SharedPreferences;
    import android.os.AsyncTask;
    import android.os.Bundle;
    import android.text.Html;
    import android.text.method.LinkMovementMethod;
    import android.view.View;
    import android.widget.ImageButton;
    import android.widget.ImageView;
    import android.widget.ProgressBar;
    import android.widget.TextView;

    import androidx.appcompat.app.ActionBar;
    import androidx.appcompat.app.AppCompatActivity;

    import com.example.tarkov.R;
    import com.squareup.picasso.Callback;
    import com.squareup.picasso.Picasso;

    import org.jsoup.Jsoup;
    import org.jsoup.nodes.Document;
    import org.jsoup.nodes.Element;
    import org.jsoup.select.Elements;

    import java.io.File;
    import java.io.FileOutputStream;
    import java.io.IOException;
    import java.io.OutputStreamWriter;

    import androidx.core.text.HtmlCompat;

    public class NewsDetailActivity extends AppCompatActivity {

        private TextView detailTitle;
        private TextView detailDate;
        private ImageView detailImage;
        private TextView detailContent;
        private TextView sourceLink;
        private ImageButton closeButton;

        private ProgressBar progressBarDetail;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            // Загрузка сохраненной темы перед установкой содержимого вида
            SharedPreferences prefs = getSharedPreferences("AppThemePrefs", MODE_PRIVATE);
            boolean isDarkTheme = prefs.getBoolean("isDarkTheme", false); // false - значение по умолчанию

            setTheme(isDarkTheme ? R.style.AppTheme_Dark : R.style.AppTheme_Light);

            super.onCreate(savedInstanceState);
            setContentView(R.layout.news_detail_layout);

            // Скрыть ActionBar
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }



            // Инициализация элементов интерфейса
            detailTitle = findViewById(R.id.detailTitle);
            detailDate = findViewById(R.id.detailDate);
            detailImage = findViewById(R.id.detailImage);
            detailContent = findViewById(R.id.detailContent);
            sourceLink = findViewById(R.id.sourceLink);
            closeButton = findViewById(R.id.closeButton);

            // Добавлена инициализация ProgressBar
            progressBarDetail = findViewById(R.id.progressBarDetail);

            // Получение ссылки на полную новость из предыдущей активности
            String fullNewsLink = getIntent().getStringExtra("fullNewsLink");



            String newsTitle = getIntent().getStringExtra("newsTitle"); // Получение заголовка новости
            new LoadNewsTask().execute(fullNewsLink, newsTitle); // Передайте заголовок как второй параметр
            // Новый экземпляр AsyncTask для выполнения сетевой операции в фоновом потоке
    //        new LoadNewsTask().execute(fullNewsLink);

            // Установка слушателя для кнопки закрытия
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Завершаем текущую активность
                    finish();
                }
            });
        }

        private class LoadNewsTask extends AsyncTask<String, Void, Document> {

            @Override
            protected void onPreExecute() {
                // Покажем ProgressBar перед началом загрузки
                progressBarDetail.setVisibility(View.VISIBLE);

                // Скроем все элементы интерфейса
                detailTitle.setVisibility(View.GONE);
                detailDate.setVisibility(View.GONE);
                detailImage.setVisibility(View.GONE);
                detailContent.setVisibility(View.GONE);
                sourceLink.setVisibility(View.GONE);
                closeButton.setVisibility(View.GONE);
            }
    //        @Override
    //        protected Document doInBackground(String... params) {
    //            String fullNewsLink = params[0];
    //            try {
    //                return Jsoup.connect(fullNewsLink).get();
    //            } catch (IOException e) {
    //                e.printStackTrace();
    //                return null;
    //            }
    //        }

            @Override
            protected Document doInBackground(String... params) {
                String newsUrl = params[0];
                String title = params[1];
                String fileName = title.replaceAll("[^a-zA-Z0-9а-яА-Я]", "_") + ".html";
                File file = new File(getExternalFilesDir(null), fileName);

                Document doc = null;
                try {
                    if (file.exists()) {
                        doc = Jsoup.parse(file, "UTF-8");
                    } else {
                        doc = Jsoup.connect(newsUrl).get();
                        // Сохраняем документ в файл
                        FileOutputStream fos = new FileOutputStream(file);
                        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
                        osw.write(doc.outerHtml());
                        osw.close();
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return doc;
            }




            @Override
            protected void onPostExecute(Document doc) {

                // Скроем ProgressBar после завершения загрузки
                progressBarDetail.setVisibility(View.GONE);

                // Отобразим все элементы интерфейса
                detailTitle.setVisibility(View.VISIBLE);
                detailDate.setVisibility(View.VISIBLE);
                detailImage.setVisibility(View.VISIBLE);
                detailContent.setVisibility(View.VISIBLE);
                sourceLink.setVisibility(View.VISIBLE);
                closeButton.setVisibility(View.VISIBLE);
                // Обновление UI в основном потоке на основе полученных данных
                if (doc != null) {
                    // Извлечение данных из HTML-документа
                    String title = doc.select("h1[itemprop='name headline']").first() != null ? doc.select("h1[itemprop='name headline']").first().text() : "";
                    String date = doc.select("span").first() != null ? doc.select("span").first().text() : "";
                    String imageUrl = doc.select("img[itemprop='contentUrl url']").first() != null ? doc.select("img[itemprop='contentUrl url']").first().attr("src") : "";
                    String contentHtml = doc.select("div[itemprop='articleBody']").first() != null ? doc.select("div[itemprop='articleBody']").first().html() : "";
                    // Установка извлеченных данных в элементы макета в UI-потоке
                    detailTitle.setText(title);
                    detailDate.setText(date);
                    Picasso.get().load(imageUrl).into(detailImage);
                    detailContent.setText(HtmlCompat.fromHtml(contentHtml, HtmlCompat.FROM_HTML_MODE_COMPACT));
                    detailContent.setMovementMethod(LinkMovementMethod.getInstance());

                    Element sourceElement = doc.select("meta[property='og:url']").first();
                    if (sourceElement != null) {
                        String sourceLinkText = sourceElement.attr("content");
                        sourceLink.setText(Html.fromHtml("Источник: <a href=\"" + sourceLinkText + "\">" + title + "</a>"));
                        sourceLink.setMovementMethod(LinkMovementMethod.getInstance());
                    } else {
                        sourceLink.setText("Источник не указан");
                    }

                    // Загрузка изображения с использованием Picasso с обратным вызовом
                    String finalImageUrl = imageUrl;
                    Picasso.get().load(imageUrl).into(detailImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            // Вызывается при успешной загрузке изображения
                            // Теперь вы можете скрыть прогресс-бар, так как весь контент загружен
                            progressBarDetail.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            // Вызывается в случае ошибки загрузки изображения
                            // Здесь также вы можете скрыть прогресс-бар или обработать ошибку
                        }
                    });
                } else {
                    // В случае ошибки вы можете, например, отобразить пользователю сообщение об ошибке
                    // или просто выйти из активности
                    finish();
                }
            }


        }
    }
