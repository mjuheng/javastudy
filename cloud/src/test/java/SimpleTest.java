import com.huangch.cloud.utils.http.HttpUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author huangch
 * @date 2023-10-07
 */
@Slf4j
public class SimpleTest {

    @Test
    public void test() throws Exception {

        String filePath = "C:\\Users\\36020\\demo.docx"; // 文件路径
        long fileSize = 1024 * 1024 * 1024; // 文件大小，10 MB

        createFixedSizeFile(filePath, fileSize);
    }

    @SneakyThrows
    public static void createFixedSizeFile(String filePath, long size) {
        byte[] buffer = new byte[1024 * 4]; // 1 KB 缓冲区

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            for (long i = 0; i < size / buffer.length; i++) {
                fos.write(buffer);
            }
            // 如果大小不是缓冲区的整数倍，写入剩余的部分
            fos.write(buffer, 0, (int) (size % buffer.length));
            System.out.println("File created: " + filePath + " with size: " + size + " bytes");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteProcess() throws Exception {
        List<String> instanceIds = new ArrayList<>();
        instanceIds.add("761fe404-173d-11ef-9e3a-525400a34869");

        for (String instanceId : instanceIds) {

            HashMap<String, String> params = new HashMap<>();
            params.put("instanceId", instanceId);
            params.put("deleteReason", "delete");
            System.out.println(HttpUtils.sendPost("http://172.17.2.214:30081/workflow/instance/delete", params, new HashMap<>(), String.class));
        }
    }

}
