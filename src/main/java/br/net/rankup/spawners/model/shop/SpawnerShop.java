package br.net.rankup.spawners.model.shop;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SpawnerShop {

    private String entity;
    private double price;
    private long time;

}
