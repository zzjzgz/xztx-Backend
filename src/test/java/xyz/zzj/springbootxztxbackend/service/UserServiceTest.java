package xyz.zzj.springbootxztxbackend.service;

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

    private ExecutorService executorService = new ThreadPoolExecutor(40, 1000, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(2500));

    @Test
    void testAddUser(){
        long start = System.currentTimeMillis();
        int num = 5000;  //一次批量插入的数量
        int j = 0;
        for (int i = 0;i<20;i++){
            List<User> userList = new ArrayList<>();
            while (true){
                j++;
                User user = new User();
                user.setUserProfile("我是假数据");
                user.setUsername("假数据");
                user.setAvatarUrl("https://xxxxxx.jpg");
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
            userService.saveBatch(userList,num);
        }
        long end = System.currentTimeMillis();
        System.out.println("用时:"+(end - start));
    }

    /**
     * 批量插入
     */
    @Test
    void searchTags() {
        long start = System.currentTimeMillis();
        int num = 2500;
        int j = 0;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            List<User> userList = new ArrayList<>();
            while (true){
                j++;
                User user = new User();
                user.setUserProfile("我是假数据");
                user.setUsername("假数据");
                user.setAvatarUrl("https://xxxx.jpg");
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
            //定义一个异步任务进行批量插入
            CompletableFuture<Void> future = CompletableFuture.runAsync(()->{
                System.out.println("线程名" + Thread.currentThread().getName());
                userService.saveBatch(userList, num);
            },executorService);
            //每个任务执行完,放人list列表里
            futureList.add(future);
        }
        //等待线程全部执行完后执行后续操作
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();

        long end = System.currentTimeMillis();
        System.out.println("用时:"+(end - start));
    }
}