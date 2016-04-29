package co.ifwe.versus.viewholders;

/**
 * Created by schoi on 4/22/16.
 */
public enum MessageViewType {
    HEADER (0),
    SELF (1),
    SELF_START (2),
    OPPONENT (-1),
    OPPONENT_START (-2);

    private int mCode;

    MessageViewType(int code) {
        mCode = code;
    }

    public int getCode() {
        return mCode;
    }
}
