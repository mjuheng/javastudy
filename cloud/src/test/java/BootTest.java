import cn.hutool.core.util.IdUtil;
import com.huangch.cloud.BootApplication;
import com.huangch.cloud.utils.thread.ThreadPoolMonitor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author huangch
 * @date 2023-07-31
 */
@SuppressWarnings("all")
@Slf4j
@SpringBootTest(classes = BootApplication.class)
public class BootTest {

    @Resource
    private ThreadPoolMonitor threadPoolMonitor;

    @Test
    public void demo() throws Exception {
        for (int i = 0; i < 4; i++) {
            System.out.println(IdUtil.getSnowflake().nextIdStr());
        }
    }
}
