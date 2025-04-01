import com.huangch.BaseApplication;
import com.huangch.base.service.StudentService;
import org.apache.ibatis.parsing.GenericTokenParser;
import org.apache.ibatis.parsing.TokenHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

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
        Map<String, Object> mapper = new HashMap<>();
        mapper.put("name", "张三");
        mapper.put("pwd", "123456");

        //先初始化一个handler
        TokenHandler handler = new TokenHandler() {
            @Override
            public String handleToken(String content) {
                return (String) mapper.get(content);
            }
        };
        GenericTokenParser parser = new GenericTokenParser("#{", "}", handler);
        System.out.println("************" + parser.parse("用户：#{name}，你的密码是:#{pwd}"));

    }

}
