package co.ifwe.versus.events;

import org.greenrobot.eventbus.EventBus;

public class Bus {
    public static final EventBus USER_BUS = new EventBus();
    public static final EventBus MESSAGE_BUS = new EventBus();
}
