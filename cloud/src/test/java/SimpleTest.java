import com.huangch.cloud.utils.http.HttpUtils;
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


    @Test
    public void test() throws Exception {

    }


    public void deleteProcess() throws Exception {
        List<String> instanceIds = new ArrayList<>();
        instanceIds.add("92919021-a3c2-11ee-ba57-525400d4a1e9");

        for (String instanceId : instanceIds) {

            HashMap<String, String> params = new HashMap<>();
            params.put("instanceId", instanceId);
            params.put("deleteReason", "delete");
            System.out.println(HttpUtils.sendPost("http://172.17.2.245:8074/workflow/instance/delete", params, new HashMap<>(), String.class));
        }
    }
}
