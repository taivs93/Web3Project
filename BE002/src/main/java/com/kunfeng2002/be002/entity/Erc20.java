package com.kunfeng2002.be002.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "erc20")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Erc20 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_token")
    private String nameToken;

    @Column(name = "address")
    private String address;

    @Column(name = "symbol")
    private String currency;

    @Column(name = "decimals")
    private int types;

}
