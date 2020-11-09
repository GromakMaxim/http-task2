package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class App {
    public static final String REMOTE_SERVICE_URI = "https://api.nasa.gov/planetary/apod?api_key=cMRXon71hN1WkTjYZPIzS7GHFx2Th1J78J2qLCJb";
    public static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().
                setUserAgent("My Test Service").
                setDefaultRequestConfig(RequestConfig.custom().
                        setConnectTimeout(5000)   // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();

        HttpGet request = new HttpGet(REMOTE_SERVICE_URI);
        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        CloseableHttpResponse response = httpClient.execute(request);

        NASAParser dataFromNasaWebPage = mapper.readValue(new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8), new TypeReference<>() {
        });

        System.out.println(dataFromNasaWebPage.getHdurl());

        savePictureFromNASAWebPage(dataFromNasaWebPage.getHdurl());

    }

    private static void savePictureFromNASAWebPage(String pictureURL) throws IOException {
        URL url = new URL(pictureURL);
        BufferedImage img = ImageIO.read(url);
        File file = new File("ImageByNASA.jpg");
        ImageIO.write(img, "jpg", file);
    }

}
