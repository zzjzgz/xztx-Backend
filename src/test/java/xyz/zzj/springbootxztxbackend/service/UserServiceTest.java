package xyz.zzj.springbootxztxbackend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.zzj.springbootxztxbackend.model.domain.User;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    void testAddUser() throws JsonProcessingException {
        User user = new User();
        user.setUsername("zzjzzj");
        user.setAvatarUrl("http://xxxxxx");
        user.setGender(0);
        user.setUserAccount("zzjzzj");
        user.setUserPassword("12345678");
        user.setPhone("123");
        user.setEmail("123");
        String str = "java";
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(str);
        user.setTags(jsonString);
        userService.save(user);
    }

    @Test
    void searchTags() {
        List<String> list = new ArrayList<>();
        list.add("java");
        List<User> users = userService.searchUserByTags(list);
        assert (users != null);
    }

}