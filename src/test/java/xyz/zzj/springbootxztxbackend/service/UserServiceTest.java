package xyz.zzj.springbootxztxbackend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.zzj.springbootxztxbackend.model.domain.User;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    private ExecutorService executorService = new ThreadPoolExecutor(23, 1000, 10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));

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

    /**
     * 批量插入
     */
    @Test
    void searchTags() {
        final int INSERT_NUM = 10000000;
        //分十组
        int num = 5000;
        int j = 0;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            //建立一个线程安全的list
            List<User> userList = new ArrayList<>();
            while (true){
                j++;
                User user = new User();
                user.setUserProfile("我是假数据");
                user.setUsername("假数据");
                user.setAvatarUrl("https://zzj-img.oss-cn-hangzhou.aliyuncs.com/2024/02.jpg");
                user.setGender(0);
                user.setUserAccount("jiazzj");
                user.setUserPassword("2970a1691c8ef07f40ddbe6f7b18662f");
                user.setPhone("123456789");
                user.setEmail("123456@qq.com");
                user.setUserStatus(0);
                user.setUserRole(0);
                String[] str = new String[]{"java","考研"};
                Gson gson = new Gson();
                String json = gson.toJson(str);
                user.setTags(json);
                userList.add(user);
                if (j % num == 0){
                    break;
                }
            }
            CompletableFuture<Void> future = CompletableFuture.runAsync(()->{
                System.out.println("线程名" + Thread.currentThread().getName());
                userService.saveBatch(userList, num);
            },executorService);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
    }
}