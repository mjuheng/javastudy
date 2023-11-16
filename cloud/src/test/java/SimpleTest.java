import cn.hutool.core.bean.BeanUtil;
import com.huangch.cloud.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @author huangch
 * @date 2023-10-07
 */
@Slf4j
public class SimpleTest {


    @Test
    public void test() throws IOException {
        User user = new User();
        user.setUserPartyMember(null);

        System.out.println(BeanUtil.toBean(user, User.class));
    }


}
