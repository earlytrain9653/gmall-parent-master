package com.atguigu.gmall.search.repo;

import com.atguigu.gmall.search.Goods;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 杨林
 * @create 2022-12-12 20:45 星期一
 * description:
 */
@Repository
public interface GoodsRepository extends CrudRepository<Goods,Long> {
}
