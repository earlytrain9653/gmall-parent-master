package com.atguigu.gmall.search;

import com.atguigu.gmall.search.bean.Person;
import com.atguigu.gmall.search.repo.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

/**
 * @author 杨林
 * @create 2022-12-12 18:28 星期一
 * description:
 */
@SpringBootTest
public class ESCrudTest {

    @Autowired
    PersonRepository personRepository;


    @Test
    public void testFindByAge(){
        List<Person> people = personRepository.findAllByAgeGreaterThanEqual(28);
        people.forEach(System.out::println);
    }

    @Test
    public void testFind(){
        Iterable<Person> all = personRepository.findAll();
        all.forEach(System.out::println);
    }

    @Test
    public void testAdd() {
        List<Person> personList = Arrays.asList(
                    new Person(3L,"王五",13),
                    new Person(4L,"王柳",34)
        );
        Iterable<Person> people = personRepository.saveAll(personList);

    }
}
