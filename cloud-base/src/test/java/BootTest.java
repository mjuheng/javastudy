import cn.hutool.core.util.IdUtil;
import com.huangch.BaseApplication;
import com.huangch.base.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author huangch
 * @date 2023-07-31
 */
@SpringBootTest(classes = BaseApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BootTest {

    @Resource
    private StudentService studentService;

    @Test
    public void test() throws Exception {
        for (int i = 0; i < 16; i++) {
            Thread.sleep(500);
            System.out.println(IdUtil.getSnowflake().nextIdStr());
        }

    }

}
