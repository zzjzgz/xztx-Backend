package xyz.zzj.springbootxztxbackend.noce;

import com.alibaba.excel.EasyExcel;

import java.util.List;

public class ImportExcel {
    /**
     * 指定列的下标或者列名，直接读取
     */
    public static void main(String[] args) {
        String fileName = "F:\\java_study\\springboot-xztx-backend\\src\\main\\resources\\user.xlsx";
        //写上对象，和监听器
//        EasyExcel.read(fileName, xztxUserInfo.class, new xztxUserListener()).sheet().doRead();
        List<xztxUserInfo> list = EasyExcel.read(fileName).head(xztxUserInfo.class).sheet().doReadSync();
        for (xztxUserInfo xztxUserInfo : list) {
            System.out.println(xztxUserInfo);
        }
    }
}
