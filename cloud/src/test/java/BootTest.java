import com.huangch.cloud.BootApplication;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

/**
 * @author huangch
 * @date 2023-07-31
 */
@SuppressWarnings("all")
@Slf4j
@SpringBootTest(classes = BootApplication.class)
public class BootTest {
    @Test
    public void demo() throws Exception {
        String requestId = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase() + "|" + System.currentTimeMillis();
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json;charset=UTF-8");
        RequestBody body = RequestBody.create(mediaType, "{\r\n    \"metas\": {\r\n        \"ksid\": \"OTDIMWMTA1MzMxNDg3OTk2NDAxUGt0cXplNzdQ\",\r\n        \"appVersion\": \"1.0.0\",\r\n        \"appName\": \"melody\",\r\n        \"shopId\": 1197496416\r\n    },\r\n    \"method\": \"createGroup\",\r\n    \"service\": \"FoodService\",\r\n    \"ncp\": \"2.0.0\",\r\n    \"id\": \"" + requestId + "\",\r\n    \"params\": {\r\n        \"shopType\": \"SINGLE_SHOP\",\r\n        \"shopId\": \"1197496416\",\r\n        \"sFoodCategoryWithChild\": {\r\n            \"categoryMode\": \"NORMAL\",\r\n            \"dayPartingStick\": {\r\n                \"isAllDay\": false,\r\n                \"isUseDayPartingStick\": false\r\n            },\r\n            \"isUseDayPartingStick\": false,\r\n            \"name\": \"下单必选——\"\r\n        }\r\n    }\r\n}");
        Request request = new Request.Builder()
                .url("https://app-api.shop.ele.me/nevermore.goods/invoke/?method=FoodService.createGroup")
                .method("POST", body)
                .addHeader("authority", "app-api.shop.ele.me")
                .addHeader("accept", "application/json, text/plain, */*")
                .addHeader("accept-language", "zh-CN")
                .addHeader("cookie", "")
                .addHeader("origin", "https://napos-goods-pc.faas.ele.me")
                .addHeader("referer", "https://napos-goods-pc.faas.ele.me/")
                .addHeader("sec-ch-ua", "\"Not;A=Brand\";v=\"99\", \"Chromium\";v=\"106\"")
                .addHeader("sec-ch-ua-mobile", "?0")
                .addHeader("sec-ch-ua-platform", "\"Windows\"")
                .addHeader("sec-fetch-dest", "empty")
                .addHeader("sec-fetch-mode", "cors")
                .addHeader("sec-fetch-site", "same-site")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) HAODATA/6.0.25 Chrome/106.0.5249.199 Electron/21.4.4 Safari/537.36")
                .addHeader("x-eleme-requestid", requestId)
                .addHeader("x-shard", "shopid=1197496416")
                .addHeader("content-type", "application/json;charset=UTF-8")
                .addHeader("Host", "app-api.shop.ele.me")
                .addHeader("Connection", "keep-alive")
                .build();
        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());

    }
}

@Getter
enum a {

    PROJECT("项目");
    private final String code;

    a(String code) {
        this.code = code;
    }
}
