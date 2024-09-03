import com.huangch.cloud.utils.http.HttpUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author huangch
 * @date 2023-10-07
 */
@Slf4j
public class SimpleTest {

    @SneakyThrows
    @Test
    public void test() {
        System.out.println(",qsqs".split(",").length);
    }

    @Test
    public void deleteProcess() throws Exception {
        List<String> instanceIds = new ArrayList<>();
        instanceIds.add("d5a19a10-4fd9-11ef-b890-525400d1fae6");

        for (String instanceId : instanceIds) {

            HashMap<String, String> params = new HashMap<>();
            params.put("instanceId", instanceId);
            params.put("deleteReason", "delete");
            System.out.println(HttpUtils.sendPost("http://172.17.1.214:30284/workflow/instance/delete", params, new HashMap<>(), String.class));
        }
    }


}
