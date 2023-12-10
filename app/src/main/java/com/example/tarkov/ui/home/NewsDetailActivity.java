package com.example.tarkov.ui.home;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tarkov.R;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import androidx.core.text.HtmlCompat;

public class NewsDetailActivity extends AppCompatActivity {

    private TextView detailTitle;
    private TextView detailDate;
    private ImageView detailImage;
    private TextView detailContent;
    private TextView sourceLink;
    private ImageButton closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_detail_layout);

        // Инициализация элементов интерфейса
        detailTitle = findViewById(R.id.detailTitle);
        detailDate = findViewById(R.id.detailDate);
        detailImage = findViewById(R.id.detailImage);
        detailContent = findViewById(R.id.detailContent);
        sourceLink = findViewById(R.id.sourceLink);
        closeButton = findViewById(R.id.closeButton);

        // Получение ссылки на полную новость из предыдущей активности
        String fullNewsLink = getIntent().getStringExtra("fullNewsLink");

        // Новый экземпляр AsyncTask для выполнения сетевой операции в фоновом потоке
        new LoadNewsTask().execute(fullNewsLink);

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
        protected Document doInBackground(String... params) {
            String fullNewsLink = params[0];
            try {
                return Jsoup.connect(fullNewsLink).get();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Document doc) {
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

                Element sourceElement = doc.select("a[itemprop='mainEntityOfPage']").first();
                if (sourceElement != null) {
                    String sourceLinkText = sourceElement.attr("href");
                    sourceLink.setText("Источник: " + sourceLinkText);
                } else {
                    sourceLink.setText("Источник не указан");
                }
            } else {
                // В случае ошибки вы можете, например, отобразить пользователю сообщение об ошибке
                // или просто выйти из активности
                finish();
            }
        }
    }
}
