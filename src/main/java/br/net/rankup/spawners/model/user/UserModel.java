package br.net.rankup.spawners.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserModel {

    private String name;
    private double spawnerLimite;
    private double stackLimite;

}
