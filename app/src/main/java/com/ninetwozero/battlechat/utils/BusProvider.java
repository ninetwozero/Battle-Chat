package com.ninetwozero.battlechat.utils;

import com.squareup.otto.Bus;

public class BusProvider {
    private static Bus instance;

    public static Bus getInstance() {
        if (instance == null) {
            instance = new Bus();
        }
        return instance;
    }

    private BusProvider() {}
}
