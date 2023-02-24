package com.atguigu.gmall.gateway.filter;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author 杨林
 * @create 2022-12-15 19:39 星期四
 * description:响应式编程测试
 */
public class ReactiveTest {
    /**
     *
     * 响应式编程：万物皆数据  数据皆为流
     *  Mono：发布0或1个数据的发布者
     *  Flux：发布n个数据的发布者
     * @param args
     */
    public static void main111(String[] args) throws InterruptedException {
//        List<Integer> collect = Arrays.asList(1, 2, 9, 5, 7, 3)
//                .stream()
//                .sorted()  //声明式
//                .collect(Collectors.toList());
//        System.out.println(collect);

        //数据发布者
//        Mono<String> mono = Mono.fromCallable(() -> {
//            System.out.println("正在准备一堆数据....");
//            Thread.sleep(3000);
//            System.out.println("数据准备完成");
//            return "1111";
//        });
//
//        mono.subscribe((item) -> {
//            System.out.println("拿到数据：" + item);
//        });
//
//        System.out.println("哈哈哈");
//
//        Thread.sleep(300000);


        //Flux<Integer> just = Flux.just(1, 2, 3, 4);

        Flux<Long> just = Flux.interval(Duration.ofSeconds(1));

        just.subscribe((item) -> {

            System.out.println("a：" + item);
        });

        just.subscribe((item) -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("b：" + item);
        });

        Thread.sleep(300000);
    }
}
