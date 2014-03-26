/*
 *
 * 	This file is part of BattleChat
 *
 * 	BattleChat is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 *
 * 	BattleChat is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * /
 */

package com.ninetwozero.battlechat.factories;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ninetwozero.battlechat.json.chat.Message;
import com.ninetwozero.battlechat.json.chat.User;
import com.ninetwozero.battlechat.utils.MessageDeserializer;
import com.ninetwozero.battlechat.utils.UserDeserializer;

public class GsonProvider {
    private static Gson instance;

    public static Gson getInstance() {
        if (instance == null) {
            instance = createNewInstance();
        }
        return instance;
    }

    private static Gson createNewInstance() {
        return new GsonBuilder().registerTypeAdapter(
            User.class, new UserDeserializer()
        ).registerTypeAdapter(
            Message.class, new MessageDeserializer()
        ).create();
    }
}
