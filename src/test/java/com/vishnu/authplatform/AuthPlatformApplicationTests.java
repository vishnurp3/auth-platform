package com.vishnu.authplatform;

import com.vishnu.authplatform.bdd.TestContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthPlatformApplicationTests extends TestContainersConfig {

    @Test
    void contextLoads() {
    }

}
