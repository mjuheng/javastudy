import cn.hutool.core.date.DateUtil;
import com.huangch.cloud.BootApplication;
import com.huangch.cloud.utils.queue.DelayQueueUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author huangch
 * @date 2023-07-31
 */
@SpringBootTest(classes = BootApplication.class)
public class BootTest {

    @Test
    public void test() throws Exception {
        System.out.println("存入：" + DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        DelayQueueUtils delayQueueUtils = new DelayQueueUtils();
        delayQueueUtils.delay(() -> {
            System.out.println("消费1：" + DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            return null;
        }, 10L, TimeUnit.SECONDS);
        delayQueueUtils.delay(() -> {
            System.out.println("消费2：" + DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            return null;
        }, 3L, TimeUnit.SECONDS);

        Thread.sleep(20 * 1000);
    }

}
