package co.ifwe.versus.models;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public enum Server {
    STAGE("Stage", "versus-app.versus.3c8c6c32.svc.dockerapp.io"),
    OTHER("Other", "");

    private String mName;
    private String mHost;

    Server(String name, String host) {
        mName = name;
        mHost = host;
    }

    public String getName() {
        return mName;
    }

    public String getHost() {
        return mHost;
    }

    public static int findHostPosition(String host, int defaultPos) {
        Server[] values = Server.values();
        for (int i = 0; i < values.length; i++) {
            if (TextUtils.equals(host, values[i].getHost())) {
                return i;
            }
        }
        return defaultPos;
    }

    public static List<String> getNames() {
        Server[] values = Server.values();
        List<String> names = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            names.add(values[i].getName());
        }
        return names;
    }
}
