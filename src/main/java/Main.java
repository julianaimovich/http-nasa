import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {

    public static final String REMOTE_SERVICE_URI = "https://api.nasa.gov/planetary/apod?api_key=2T7mIeFxkSQtpG37Xrb1Yo7iW17rdWkbgh2mANao";

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();

        HttpGet requestForInfo = new HttpGet(REMOTE_SERVICE_URI);
        CloseableHttpResponse infoResponse = httpClient.execute(requestForInfo);

        ObjectMapper objectMapper = new ObjectMapper();
        NasaInfo nasaInfo = objectMapper.readValue(infoResponse.getEntity().getContent(), new TypeReference<>() {});

        String imageUrl = nasaInfo.getUrl();
        HttpGet requestForImage = new HttpGet(imageUrl);
        CloseableHttpResponse imageResponse = httpClient.execute(requestForImage);
        byte[] imageContent = imageResponse.getEntity().getContent().readAllBytes();
        writeFile(imageContent, FilenameUtils.getName(imageUrl));
    }

    public static void writeFile(byte[] content, String fileName) {
        File file = new File(fileName);
        try {
            FileOutputStream output = new FileOutputStream(file);
            output.write(content);
            output.flush();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}