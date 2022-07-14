package com.ruoyi.map;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.minio.MinioUtils;
import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.map.domain.dto.ImagePuancuationInformationDto;
import com.ruoyi.map.service.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("/map")
@ResponseBody
public class MapController extends BaseController {

    @Autowired
    private MapService mapService;

    @Value("${minio.bucketName}")
    private String bucketName;

    /**
     * 添加线路图
     *
     * @param file 文件
     * @return 结果
     */
    @PostMapping("/add")
    public AjaxResult addImageAndSgin(@RequestParam(value = "file", required = false) MultipartFile file,
                                      @RequestParam("image") String image) throws IOException {
        ImagePuancuationInformationDto imagePuancuationInformationPo = JSON.parseObject(image, ImagePuancuationInformationDto.class);
        String fileUrl = null;
        //设置上传文件名 uuid
        String upFileName = UUID.fastUUID() + file.getOriginalFilename();
        //判断文件大小
        if (file.getSize() <= 20971520) {
            //图片访问路径
            fileUrl = MinioUtils.getBasisUrl() + upFileName;
            System.err.println(fileUrl);
            try {
                MinioUtils.putObject(bucketName, file, upFileName, file.getContentType());
            } catch (Exception e) {
                e.printStackTrace();
                return AjaxResult.error("上传图片出现异常");
            }
        } else {
            return AjaxResult.error("请上传小于20mb的文件");
        }
        //设置图片url
        imagePuancuationInformationPo.setImageUrl(fileUrl);
        //设置图片名称
//        imagePuancuationInformationPo.setImageName(upFileName);
        return toAjax(mapService.addImageAndSgin(imagePuancuationInformationPo));
    }

    /**
     * 查询线路图信息
     * @return
     */
    @GetMapping("/")
    public AjaxResult queryImageAndSgin(ImagePuancuationInformationDto image){
        return AjaxResult.success(mapService.queryImageAndSgin(image));
    }

    /**
     * 查询线路图信息
     * @return
     */
    @DeleteMapping("/{id}")
    public AjaxResult deleteImageAndSgin(@PathVariable("id") String id){
        return toAjax(mapService.deleteImageAndSgin(id));
    }
}
