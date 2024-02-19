package xyz.zzj.springbootxztxbackend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import xyz.zzj.springbootxztxbackend.connon.BaseResponse;
import xyz.zzj.springbootxztxbackend.connon.ErrorCode;
import xyz.zzj.springbootxztxbackend.connon.ResultUtils;
import xyz.zzj.springbootxztxbackend.exception.BusinessException;
import xyz.zzj.springbootxztxbackend.utils.AliOssUtil;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @BelongsPackage: xyz.zzj.springbootxztxbackend.controller
 * @ClassName: UploadConller
 * @Author: zengz
 * @CreateTime: 2024/2/17 23:05
 * @Description: 完成文件上传
 * @Version: 1.0
 */
@RestController
//这个是线上用于跨域的，本地请注释其注解，//上线记得改服务器地址
@CrossOrigin(origins = {"http://localhost:5173/"},allowCredentials = "true")
@Slf4j
public class UploadController {

    @Resource
    private AliOssUtil aliOssUtil;

    /**
     * 用于图片上传业务
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public BaseResponse<String> uploadAvatarUrl(@RequestPart("file") MultipartFile file){
        try {
//            获取这个文件的原始的名字
            String originalFilename = file.getOriginalFilename();
//            MultipartFile file = teamUploadDTO.getFile();
//            将这个图片裁剪把 后缀截取出来 eg .jpg啥的  获取这个文件的后缀名
            String lastIndexOf = originalFilename.substring(originalFilename.lastIndexOf("."));
//            通过UUID 来生成一个字符串（生成新的文件名称） 不唯一的 防止图片重复 从而覆盖之前的图片
            String newName= "xztx/"+UUID.randomUUID().toString()+lastIndexOf;
//          通过工具类来返回 文件在oss 对象存储中的文件
            String filePath = aliOssUtil.upload(file.getBytes(), newName);
            filePath = "https://" + filePath;
            return ResultUtils.success(filePath);
        } catch (Exception e) {
            log.error("错误是啥{}",e);
        }
        throw new BusinessException(ErrorCode.PARAMS_ERROR,"上传失败");
    }
}
