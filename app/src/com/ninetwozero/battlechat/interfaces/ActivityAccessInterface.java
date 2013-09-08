package com.ninetwozero.battlechat.interfaces;

public interface ActivityAccessInterface {
    public void showToast(final String message);
    public void logoutFromWebsite();
    public void toggleSlidingMenu();
    public void toggleSlidingMenu(final boolean show);

    public boolean isMenuShowing();
}
