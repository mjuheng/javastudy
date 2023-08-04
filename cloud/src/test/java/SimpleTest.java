import com.huangch.cloud.BootApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author huangch
 * @date 2023-07-31
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class SimpleTest {

    @Test
    public void test() throws InterruptedException {
        AtomicInteger i = new AtomicInteger(0);
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(() -> {
            if (i.getAndIncrement() > 2) {
                throw new RuntimeException();
            }
            System.out.println("hello");

        }, 0L, 1L, TimeUnit.SECONDS);

        while (true) {
            Thread.sleep(10000);
        }
    }
}
