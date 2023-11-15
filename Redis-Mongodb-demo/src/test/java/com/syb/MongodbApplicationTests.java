package com.syb;

import com.syb.pojo.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;


import java.util.List;

/**
 * @Author: sun
 * @Date: 2023/11/15/15:54
 */
@SpringBootTest
public class MongodbApplicationTests {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Test
    void contextLoads() {
        Book book = new Book();
        book.setId(2);
        book.setName("springboot2");
        book.setType("springboot2");
        book.setDescription("springboot2");
        mongoTemplate.save(book);
    }

    //查询所有
    @Test
    void findAll() {
        List<Book> books = mongoTemplate.findAll(Book.class);
        System.out.println(books);
    }

}
