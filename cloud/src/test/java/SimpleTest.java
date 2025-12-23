import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author huangch
 * @date 2023-10-07
 */
@Slf4j
public class SimpleTest {

    @Test
    public void test1() throws MalformedURLException {
    }

    public static String getBaseUrl(String url) {
        int idx = url.indexOf("?");
        if (idx == -1) {
            return url;
        }
        return url.substring(0, idx);
    }

    @SneakyThrows
    @Test
    public void test() {
        // 你的 webhook（未加签）
        String webhook = "https://oapi.dingtalk.com/robot/send?access_token=f734fac1616c13de89b4e918040ce741819ec2768d2d8e84ba42d1f19edb9d1b";

        // 必须包含关键词“热点”，否则钉钉会拒绝
        String json = "{\n" +
                "  \"msgtype\": \"actionCard\",\n" +
                "  \"actionCard\": {\n" +
                "    \"title\": \"热点 - 任务处理通知\",\n" +
                "    \"text\": \"### 🔥 热点通知\\n您有一个任务需要处理，请点击查看详情。\",\n" +
                "    \"btnOrientation\": \"0\",\n" +
                "    \"btns\": [\n" +
                "      {\n" +
                "        \"title\": \"查看详情\",\n" +
                "        \"actionURL\": \"https://authserver.cjlu.edu.cn/authserver/login?service=http%3A%2F%2Fsjc-zngl.cjlu.edu.cn%2Fns-app-ui%2F%23%2FSingleLogin%3FauditDingTalkBizId%3Dwf202512030330517960870%26redirectUrl%3DauditCenter\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        sendPost(webhook, json);
    }

    // 发送 POST
    private static void sendPost(String url, String json) throws Exception {
        URL apiUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");

        OutputStream os = conn.getOutputStream();
        os.write(json.getBytes("utf-8"));
        os.close();

        int code = conn.getResponseCode();
        System.out.println("钉钉返回状态码：" + code);
    }

}