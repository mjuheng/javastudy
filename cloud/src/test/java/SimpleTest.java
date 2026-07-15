import cn.hutool.core.util.IdUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @author huangch
 * @date 2023-10-07
 */
@Slf4j
public class SimpleTest {

    @SneakyThrows
    @Test
    public void test() {
        System.out.println(IdUtil.getSnowflake().nextIdStr());
    }
}