import com.huangch.cloud.BootApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author huangch
 * @date 2023-07-31
 */
@SpringBootTest(classes = BootApplication.class)
public class SimpleTest {

    @Test
    public void test() throws Exception {
        System.out.println(String.valueOf(getSimpleString()));
    }

    public static <T> T getSimpleString() {
        return (T) "121";
    }
}
