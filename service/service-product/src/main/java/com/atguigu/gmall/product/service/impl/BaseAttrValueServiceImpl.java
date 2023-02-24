package com.atguigu.gmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.entity.BaseAttrValue;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【base_attr_value(属性值表)】的数据库操作Service实现
* @createDate 2022-11-29 11:42:45
*/
@Service
public class BaseAttrValueServiceImpl extends ServiceImpl<BaseAttrValueMapper, BaseAttrValue>
    implements BaseAttrValueService{

    /**
     * 根据attr_id 查询所有的value
     * @param attrId
     * @return
     */
    @Override
    public List<BaseAttrValue> getAttrValueLitByAttrId(Long attrId) {
//        LambdaQueryWrapper<BaseAttrValue> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(BaseAttrValue::getId,attrId);

        List<BaseAttrValue> baseAttrValues =
                list(new LambdaQueryWrapper<BaseAttrValue>().eq(BaseAttrValue::getAttrId,attrId));
        return baseAttrValues;
    }
}




