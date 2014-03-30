package com.ninetwozero.battlechat.json.chat;

import com.google.gson.annotations.SerializedName;

public class ComCenterRequest {
    @SerializedName("context")
    private ComCenterInformation information;

    public ComCenterInformation getInformation() {
        return information;
    }
}
