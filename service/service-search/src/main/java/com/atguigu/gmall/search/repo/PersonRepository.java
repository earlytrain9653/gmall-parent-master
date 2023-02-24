package com.atguigu.gmall.search.repo;

import com.atguigu.gmall.search.bean.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 杨林
 * @create 2022-12-12 18:25 星期一
 * description:
 */
@Repository
public interface PersonRepository extends CrudRepository<Person,Long> {

    List<Person> findAllByAgeGreaterThanEqual(Integer age);
}
