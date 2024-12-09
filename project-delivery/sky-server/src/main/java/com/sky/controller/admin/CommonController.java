package com.sky.controller.admin;

import com.sky.exception.BaseException;
import com.sky.result.Result;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {


    @Autowired
    private MinioClient minioClient;

    //普通文件桶
    @Value("${minio.bucket.files}")
    private String bucket_Files;

    @Value("${minio.local}")
    private String local;


    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {
        //获取文件名
        String filename = file.getOriginalFilename();

        //创建一个临时文件
        File tempFile = null;
        try {
            tempFile = File.createTempFile("temp", ".temp");
            //转存临时文件
            file.transferTo(tempFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //文件上传minio
        boolean result = addMediaFilesToMinIO(tempFile.getAbsolutePath(), bucket_Files, filename);
        if (!result) {
            throw new BaseException("上传文件失败");
        }

        return Result.success(local+filename);
    }

    /**
     * 将媒体文件添加到minio
     *
     * @param localFilePath 本地文件路径
     * @param bucket        桶
     * @param objectName    对象名称
     * @return boolean
     */
    public boolean addMediaFilesToMinIO(String localFilePath, String bucket, String objectName) {
        try {
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            //上传到那个桶中
                            .bucket(bucket)
                            //指定上传MinIO中后叫什么名字
                            .object(objectName)
                            //指定本地文件路径
                            .filename(localFilePath)
                            .build());
            return true;
        } catch (Exception e) {
            log.error("上传文件到minio出错,bucket:{},objectName:{},错误原因:{}", bucket, objectName, e.getMessage(), e);
            return false;
        }

    }


}
