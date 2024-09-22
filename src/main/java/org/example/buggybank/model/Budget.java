package org.example.buggybank.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

import static jakarta.persistence.GenerationType.IDENTITY;

@Setter
@Getter
@Entity(name = "budget")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer income;

    private Integer consumption;

    private Timestamp addedDate;

    @ManyToOne(optional = false)
    private User user;

    @Override
    public String toString() {
        return "Budget{" +
                "id=" + id +
                ", income=" + income +
                ", consumption=" + consumption +
                ", addedDate=" + addedDate +
                ", user=" + user +
                '}';
    }
}
