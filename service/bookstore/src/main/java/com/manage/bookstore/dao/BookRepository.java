package com.manage.bookstore.dao;

import com.manage.bookstore.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {


}
