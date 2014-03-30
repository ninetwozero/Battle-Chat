package com.ninetwozero.battlechat.utils;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.ninetwozero.battlechat.json.chat.PresenceType;
import com.ninetwozero.battlechat.json.chat.User;
import com.ninetwozero.battlechat.json.chat.UserPresence;

import java.lang.reflect.Type;

public class UserDeserializer implements JsonDeserializer<User> {
    @Override
    public User deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        final Gson gson = new Gson();
        final JsonObject jsonObject = json.getAsJsonObject();
        final JsonElement gravatarElement = jsonObject.get("gravatarMd5");
        final UserPresence userPresence = gson.fromJson(jsonObject.get("presence"), UserPresence.class);
        final PresenceType presenceType = fetchPresenceType(userPresence);

        return new User(
            jsonObject.get("userId").getAsString(),
            jsonObject.get("username").getAsString(),
            gravatarElement.isJsonNull() ? "" : gravatarElement.getAsString(),
            userPresence,
            presenceType
        );
    }

    private PresenceType fetchPresenceType(final UserPresence userPresence) {
        return PresenceType.from(userPresence);
    }
}
