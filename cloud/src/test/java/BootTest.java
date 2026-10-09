import cn.hutool.core.util.StrUtil;
import com.huangch.cloud.BootApplication;
import com.huangch.cloud.utils.office.WordUtils;
import com.huangch.cloud.utils.thread.ThreadPoolMonitor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        String parentIds = "9111000071093107XN,91650000789879971H";
        List<String> parentIdList = Arrays.stream(parentIds.split(","))
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toList());
        if(parentIdList.size()>1){
            System.out.println(parentIdList.get(1));
        }
    }
}
