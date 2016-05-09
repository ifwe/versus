package co.ifwe.versus.utils;

import android.content.SharedPreferences;
import android.text.TextUtils;

import co.ifwe.versus.AppConfig;
import co.ifwe.versus.models.User;

public class VersusPrefUtils {
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_SSL_ENABLED = "ssl_enabled";
    private static final String KEY_SERVER = "server";
    private static final String KEY_PORT = "port";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_ENERGY = "user_energy";
    private static final String KEY_USER_NEXT_ENERGY_UPDATE = "user_last_energy_update";
    private static final String KEY_USER_RATIO = "user_win_loss";

    public static void saveUserId(SharedPreferences preferences, String userId) {
        preferences.edit().putString(KEY_USER_ID, userId).commit();
    }

    public static String getUserId(SharedPreferences preferences) {
        if (preferences.contains(KEY_USER_ID)) {
            return preferences.getString(KEY_USER_ID, null);
        }
        return null;
    }

    public static void removeUserId(SharedPreferences preferences) {
        saveUserId(preferences, null);
    }

    public static void saveSslEnabled(SharedPreferences preferences, boolean sslEnabled) {
        preferences.edit().putBoolean(KEY_SSL_ENABLED, sslEnabled).commit();
    }

    public static boolean getSslEnabled(SharedPreferences preferences) {
        if (preferences.contains(KEY_SSL_ENABLED)) {
            return preferences.getBoolean(KEY_SSL_ENABLED, false);
        }
        return false;
    }

    public static void saveServer(SharedPreferences preferences, String server) {
        preferences.edit().putString(KEY_SERVER, server).commit();
    }

    public static String getServer(SharedPreferences preferences) {
        if (preferences.contains(KEY_SERVER)) {
            return preferences.getString(KEY_SERVER, AppConfig.SERVER);
        }
        return AppConfig.SERVER;
    }

    public static void savePort(SharedPreferences preferences, String port) {
        preferences.edit().putString(KEY_PORT, port).commit();
    }

    public static String getPort(SharedPreferences preferences) {
        if (preferences.contains(KEY_PORT)) {
            return preferences.getString(KEY_PORT, "8080");
        }
        return "8080";
    }


    public static void saveUser(SharedPreferences preferences, User user) {
        preferences.edit()
                .putString(KEY_USER_ID, user.getFacebookId())
                .putString(KEY_USER_NAME, user.getName())
                .putInt(KEY_USER_ENERGY, user.getEnergy())
                .putLong(KEY_USER_NEXT_ENERGY_UPDATE, user.getNextEnergyUpdate())
                .commit();
    }

    public static User getUser(SharedPreferences preferences) {
        String userId = getUserId(preferences);
        if (preferences.contains(KEY_USER_NAME) && preferences.contains(KEY_USER_ENERGY)
                && !TextUtils.isEmpty(userId)) {
            String name = preferences.getString(KEY_USER_NAME, null);
            int energy = preferences.getInt(KEY_USER_ENERGY, 0);
            long lastEnergyUpdate = preferences.getLong(KEY_USER_NEXT_ENERGY_UPDATE, 0);
            if (!TextUtils.isEmpty(name)) {
                User user = new User(userId, name, energy, lastEnergyUpdate);
                return user;
            }
        }
        return null;
    }
}
