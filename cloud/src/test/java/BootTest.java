import cn.hutool.core.util.NumberUtil;
import com.huangch.cloud.BootApplication;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.math.BigDecimal;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author huangch
 * @date 2023-07-31
 */
@SuppressWarnings("all")
@SpringBootTest(classes = BootApplication.class)
public class BootTest {

    @Resource
    private ApplicationContext applicationContext;
    ReentrantLock reentrantLock = new ReentrantLock();


    @Test
    public void demo() throws Exception {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(1);
        cyclicBarrier.await();

        Condition condition = reentrantLock.newCondition();
        NumberUtil.toStr(new BigDecimal("2"));
    }
}
