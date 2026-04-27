import com.aspose.words.Document;
import com.huangch.cloud.BootApplication;
import com.huangch.cloud.utils.office.WordUtils;
import com.huangch.cloud.utils.thread.ThreadPoolMonitor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;

/**
 * @author huangch
 * @date 2023-07-31
 */
@SuppressWarnings("all")
@Slf4j
@SpringBootTest(classes = BootApplication.class)
public class BootTest {

    @Resource
    private ThreadPoolMonitor threadPoolMonitor;
    @Resource
    private WordUtils wordUtils;

    @Test
    public void demo() throws Exception {
        FileInputStream fileInputStream = new FileInputStream("C:\\Users\\36020\\Desktop\\协审考评表1.xlsx");
        // wordUtils.file2Pdf(fileInputStream.readAllBytes(), "xlsx", new FileOutputStream("C:\\Users\\36020\\Desktop\\协审考评表pdf.pdf"));
        Document document = wordUtils.file2Document(fileInputStream.readAllBytes(), "xlsx");
        document.save("C:\\Users\\36020\\Desktop\\协审考评表.docx");
    }
}
