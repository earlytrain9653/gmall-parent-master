package com.atguigu.gmall.product;

import com.atguigu.gmall.product.entity.SkuImage;
import com.atguigu.gmall.product.mapper.SkuImageMapper;
import org.apache.shardingsphere.infra.hint.HintManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author 杨林
 * @create 2022-12-05 20:51 星期一
 * description:测试读写分离
 */
@SpringBootTest
public class RWTest {

    @Autowired
    SkuImageMapper skuImageMapper;

    @Test  //测试读写分离
    public void testWrite(){
        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(100L);
        skuImage.setImgName("aaa");
        skuImage.setImgUrl("bbbb");
        skuImage.setSpuImgId(11L);
        skuImage.setIsDefault("ccc");
        skuImageMapper.insert(skuImage);
        System.out.println("插入完成");
    }

    @Test
    public void testRead(){
        SkuImage skuImage1 = skuImageMapper.selectById(269);
        SkuImage skuImage2 = skuImageMapper.selectById(269);
        SkuImage skuImage3 = skuImageMapper.selectById(269);
        SkuImage skuImage4 = skuImageMapper.selectById(269);
        SkuImage skuImage5 = skuImageMapper.selectById(269);
    }

    @Test
    public void testTransaction(){
        //修改
        SkuImage skuImage = new SkuImage();
        skuImage.setId(269L);
        skuImage.setSkuId(100L);
        skuImage.setImgName("aaa钱钱钱");
        skuImage.setImgUrl("bbbb");
        skuImage.setSpuImgId(11L);
        skuImage.setIsDefault("ccc");
        skuImageMapper.updateById(skuImage);
        System.out.println("修改完成");
        //读取  上次修改的数据 下次不要去从库中读取  有可能从库没有同步上
        //强制从主库中读取
        HintManager.getInstance().setWriteRouteOnly();  //设置仅主库路由
        SkuImage skuImage1 = skuImageMapper.selectById(269);
        System.out.println(skuImage1);

    }
}
