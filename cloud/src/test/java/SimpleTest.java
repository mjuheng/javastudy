import com.google.common.collect.Sets;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

/**
 * @author huangch
 * @date 2023-10-07
 */
@Slf4j
public class SimpleTest {

    @SneakyThrows
    @Test
    public void test() {
        HashSet<?> objects = Sets.newHashSet("1", "2");
        HashSet<?> objectsa = Sets.newHashSet("2", "1");
        System.out.println(objects.equals(objectsa));
    }
}