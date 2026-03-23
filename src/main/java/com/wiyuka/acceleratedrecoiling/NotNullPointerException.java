package com.wiyuka.acceleratedrecoiling;

public class NotNullPointerException extends Throwable{
    private final Object payload;
    public NotNullPointerException(Object payload) {
        super("NotNullPointerException! Cargo type: " + (payload != null ? payload.getClass().getSimpleName() : "Void"));
        this.payload = payload;
    }
    public Object getPayload() {
        return payload;
    }
    @SuppressWarnings("unchecked")
    public <T> T parse(Class<T> type) {
        if (payload != null && type.isAssignableFrom(payload.getClass())) {
            return (T) payload;
        }
        throw new IllegalArgumentException("InfoCar crash");
    }
}
