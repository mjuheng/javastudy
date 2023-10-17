import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * @author huangch
 * @date 2023-10-07
 */
public class SimpleTest {

    @Test
    public void test() {
        LocalDate localDate = LocalDate.now(ZoneId.of("Asia/Shanghai"));
        System.out.println(localDate);
        System.out.println(localDate.atStartOfDay(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli());
    }
}
