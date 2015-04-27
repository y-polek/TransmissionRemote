package net.yupol.transmissionremote.app.filtering;

public abstract class BaseFilter implements Filter {

    private int name;
    private int emptyMsg;

    public BaseFilter(int nameResId, int emptyMsgResId) {
        name = nameResId;
        emptyMsg = emptyMsgResId;
    }

    @Override
    public int getNameResId() {
        return name;
    }

    @Override
    public int getEmptyMessageResId() {
        return emptyMsg;
    }
}
