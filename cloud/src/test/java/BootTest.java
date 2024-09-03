import com.huangch.cloud.BootApplication;
import com.huangch.cloud.service.impl.UserServiceImpl;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author huangch
 * @date 2023-07-31
 */
@SuppressWarnings("all")
@SpringBootTest(classes = BootApplication.class)
public class BootTest {

    @Resource
    private UserServiceImpl userService;


    @Test
    public void demo() throws Exception {
    }
}
