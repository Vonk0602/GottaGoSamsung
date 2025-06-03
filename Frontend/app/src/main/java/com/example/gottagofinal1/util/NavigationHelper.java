package com.example.gottagofinal1.util;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.example.gottagofinal1.R;

public class NavigationHelper {

    public static final int TAB_HOME = 0;
    public static final int TAB_FAVORITE = 1;
    public static final int TAB_LISTINGS = 2;
    public static final int TAB_MESSAGES = 3;
    public static final int TAB_PROFILE = 4;

    public static void updateNavigationStyles(
            Context context,
            int activeTab,
            ImageView navHomeIcon, TextView navHomeText,
            ImageView navFavoriteIcon, TextView navFavoriteText,
            ImageView navListingsIcon, TextView navListingsText,
            ImageView navMessagesIcon, TextView navMessagesText,
            ImageView navProfileIcon, TextView navProfileText
    ) {
        int activeColor = ContextCompat.getColor(context, R.color.nav_active);
        int inactiveColor = ContextCompat.getColor(context, R.color.nav_inactive);

        navHomeIcon.setColorFilter(inactiveColor);
        navHomeText.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        navFavoriteIcon.setColorFilter(inactiveColor);
        navFavoriteText.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        navListingsIcon.setColorFilter(inactiveColor);
        navListingsText.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        navMessagesIcon.setColorFilter(inactiveColor);
        navMessagesText.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        navProfileIcon.setColorFilter(inactiveColor);
        navProfileText.setTextColor(ContextCompat.getColor(context, android.R.color.white));

        switch (activeTab) {
            case TAB_HOME:
                navHomeIcon.setColorFilter(activeColor);
                navHomeText.setTextColor(activeColor);
                break;
            case TAB_FAVORITE:
                navFavoriteIcon.setColorFilter(activeColor);
                navFavoriteText.setTextColor(activeColor);
                break;
            case TAB_LISTINGS:
                navListingsIcon.setColorFilter(activeColor);
                navListingsText.setTextColor(activeColor);
                break;
            case TAB_MESSAGES:
                navMessagesIcon.setColorFilter(activeColor);
                navMessagesText.setTextColor(activeColor);
                break;
            case TAB_PROFILE:
                navProfileIcon.setColorFilter(activeColor);
                navProfileText.setTextColor(activeColor);
                break;
        }
    }
}