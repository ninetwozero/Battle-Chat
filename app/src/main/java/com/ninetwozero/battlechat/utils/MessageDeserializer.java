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

package com.ninetwozero.battlechat.utils;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.ninetwozero.battlechat.json.chat.GroupJoinedGame;
import com.ninetwozero.battlechat.json.chat.Message;

import java.lang.reflect.Type;

public class MessageDeserializer implements JsonDeserializer<Message> {

    @Override
    public Message deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        // Deserializer needed due to the jsonObject being serialized String form (ie escaped)
        final Gson gson = new Gson();
        final JsonObject rootObject = new JsonParser().parse(jsonElement.getAsString()).getAsJsonObject();
        final JsonElement messageElement = rootObject.get("message");

        // Our defaults for non-group chats
        Object extra = null;
        Message.Type messageType = Message.Type.MESSAGE;

        // Why, oh why, do they keep doing this shit?
        if (messageElement.isJsonObject()) {
            final JsonObject messageObject = messageElement.getAsJsonObject();
            if (messageObject.has("groupjoin")) {
                extra = gson.fromJson(messageObject.get("groupjoin"), GroupJoinedGame.class);
                messageType = Message.Type.JOINED_GAME;
            } else if(messageObject.has("invitedUsername")) {
                extra = messageObject.get("invitedUsername");
                messageType = Message.Type.JOINED_GROUP;
            } else if(messageObject.has("invited")) {
                extra = messageObject.get("invited");
                messageType = Message.Type.INVITED_TO_GROUP;
            } if(messageObject.has("userLeftId")) {
                extra = messageObject.get("username");
                messageType = Message.Type.LEFT_GROUP;
            }

            // Making the special shit empty
            rootObject.addProperty("username", "");
            rootObject.addProperty("message", "");
        }

        final Message message = gson.fromJson(rootObject, type);
        message.setExtra(extra);
        message.setType(messageType);
        return message;
    }
}
