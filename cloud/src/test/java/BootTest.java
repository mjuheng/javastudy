import com.huangch.cloud.BootApplication;
import com.huangch.cloud.utils.thread.ThreadPoolMonitor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 12, 3, TimeUnit.NANOSECONDS, new LinkedBlockingQueue<>());
        threadPoolMonitor.register("线程1", threadPoolExecutor);
    }
}
