package br.net.rankup.spawners.manager;

import br.net.rankup.spawners.model.user.UserModel;

import java.util.HashMap;

public class UserManager {

   private HashMap<String, UserModel> users;

    public HashMap<String, UserModel> getUsers() {
        return users;
    }

    public void load() {
        users = new HashMap<>();
    }

    public void add(UserModel userModel) {
        if(!users.containsKey(userModel.getName())) {
            this.users.put(userModel.getName(), userModel);
        }
    }

    public UserModel get(String name) {
        if(users.containsKey(name)) {
            return users.get(name);
        }
        return null;
    }
}
