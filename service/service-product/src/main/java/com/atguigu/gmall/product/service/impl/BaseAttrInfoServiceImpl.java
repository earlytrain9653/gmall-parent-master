package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.entity.BaseAttrValue;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.entity.BaseAttrInfo;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @author Administrator
* @description 针对表【base_attr_info(属性表)】的数据库操作Service实现
* @createDate 2022-11-29 11:42:45
*/
@Service
public class BaseAttrInfoServiceImpl extends ServiceImpl<BaseAttrInfoMapper, BaseAttrInfo>
    implements BaseAttrInfoService{

    /**
     * 根据分类id获取名和值
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @Override
    public List<BaseAttrInfo> getBaseAttrAndValue(Long category1Id,
                                                  Long category2Id,
                                                  Long category3Id) {

        return baseMapper.getBaseAttrAndValue(category1Id,category2Id,category3Id);
    }

    @Autowired
    BaseAttrValueService baseAttrValueService;

    /**
     * 保存平台属性
     * @param baseAttrInfo
     */
    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        //1.把属性名信息保存到attr_info
        boolean save = this.save(baseAttrInfo);

        //获取存入数据库中的attr_info id  作为 attr_value表的attr_id
        Long attrId = baseAttrInfo.getId();

        //2.把属性值保存到attr_value
        //属性值列表
        List<BaseAttrValue> valueList = baseAttrInfo.getAttrValueList();

        //回填属性id  然后进行批量添加
        for (BaseAttrValue baseAttrValue : valueList) {
            //BaseAttrValue attrValue = new BaseAttrValue();
            baseAttrValue.setAttrId(attrId);
            //单个添加
            //attrValue.setValueName(baseAttrValue.getValueName());
            //baseAttrValueService.save(attrValue);
        }

        //批量添加
        baseAttrValueService.saveBatch(valueList);
    }

    /**
     * 修改平台属性
     * @param baseAttrInfo
     */
    @Override
    public void updateAttrInfo(BaseAttrInfo baseAttrInfo) {
        // 修改属性名表 base_attr_info (用户有可能会修改value的属性)
        updateById(baseAttrInfo);
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        Long attrId = baseAttrInfo.getId();  //获取attr_id 用于新增回填attr_id

        //前端没提交的就是删除
        //抽取出数据库原来的和前端提交的差集
        //delete from base_attr_value where attr_id = 11 and id not in (前端提交过来的ids)
        //获取前端提交过来的ids
        List<Long> ids = new ArrayList<>();
        for (BaseAttrValue baseAttrValue : attrValueList) {
            if (baseAttrValue.getId() != null){
                ids.add(baseAttrValue.getId());
            }
        }
        //问题1：如果前端一id都不提交 则ids为空
        //此时的sql为  ：delete from base_attr_value where attr_id = 11 and id not in ()
        //会造成sql语法错误
        //此时需要对ids进行判断
        if (ids.size() != 0){
            //不能直接调用这个方法  该方法表示直接删除前端提交过来的带id的数据
            //baseAttrValueService.removeByIds(ids);

            //不能使用
//        LambdaQueryChainWrapper<BaseAttrValue> wrapper = baseAttrValueService.lambdaQuery()
//                .eq(BaseAttrValue::getAttrId, baseAttrInfo.getId())
//                .notIn(BaseAttrValue::getId, ids);

            //方法1：
//        LambdaQueryWrapper<BaseAttrValue> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(BaseAttrValue::getAttrId, baseAttrInfo.getId())
//                .notIn(BaseAttrValue::getId, ids);
            //baseAttrValueService.remove();

            //方法2：
            baseAttrValueService.lambdaUpdate().eq(BaseAttrValue::getAttrId, baseAttrInfo.getId())
                    .notIn(BaseAttrValue::getId, ids)
                    .remove(); //直接使用remove()删除
        }else{
            //则需要将数据库中的值都删除
            baseAttrValueService.lambdaUpdate().eq(BaseAttrValue::getAttrId, baseAttrInfo.getId())
                                                .remove(); //直接使用remove()删除
        }

        // 修改base_attr_value
        //判断哪些是新增  哪些是修改   哪些是删除
        for (BaseAttrValue baseAttrValue : attrValueList) {
            if (baseAttrValue.getId() == null){
                //前端提交的值 没带id的是新增
                //需要回填attr_id
                baseAttrValue.setAttrId(attrId);
                baseAttrValueService.save(baseAttrValue);
            }else{
                //前端提交的值可能是修改（可能修改 可能没变）
                baseAttrValueService.updateById(baseAttrValue);
            }
        }
    }
}




