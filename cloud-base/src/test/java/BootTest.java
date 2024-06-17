import com.huangch.BaseApplication;
import com.huangch.minio.utils.MinioUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;

/**
 * @author huangch
 * @date 2023-07-31
 */
// @SpringBootTest(classes = BaseApplication.class)
@SpringBootTest(classes = BaseApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BootTest {

    @Test
    public void test() throws Exception {
        FileInputStream fis = new FileInputStream("C:\\Users\\36020\\Downloads\\事项 (2).xlsx");
        MinioUtils.upload(fis, "xlsx");
        fis.close();
    }

}
