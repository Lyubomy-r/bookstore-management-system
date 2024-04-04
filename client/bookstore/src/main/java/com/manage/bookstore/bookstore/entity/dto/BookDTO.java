package com.manage.bookstore.bookstore.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Objects;
import java.util.UUID;


@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDTO {

    private UUID id;

    @NotEmpty(message = "Title can't be empty.")
    @Pattern(regexp = "^[\\p{L} ]{3,30}", message = "Write a correct Title. Use only chars. Min 3 not more than 30.")
    private String title;

    @NotEmpty(message = "Author can't be empty.")
    @Pattern(regexp = "^[\\p{L} ]{3,30}", message = "Write a correct Author. Use only chars. Min 3 not more than 30.")
    private String author;

    @NotEmpty(message = "International Standard Book Number can't be empty.")
    private String isbn;

    @NotNull(message = "Quantity can't be empty or null.")
    @Min(value = 0, message = "Quantity must be at least 0")
    private Integer quantity;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookDTO bookDTO = (BookDTO) o;
        return Objects.equals(id, bookDTO.id) && Objects.equals(title, bookDTO.title) && Objects.equals(author, bookDTO.author) && Objects.equals(isbn, bookDTO.isbn) && Objects.equals(quantity, bookDTO.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, author, isbn, quantity);
    }

    @Override
    public String toString() {
        return "BookDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
