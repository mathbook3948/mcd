package mcd.config;

import java.util.ArrayList;
import java.util.List;

import mcd.listener.AbstractListener;
import mcd.listener.ServerInfoListener;

/**
 * 리스너 등록 관리 클래스
 */
public class ListenerRegistry {

    private static final List<AbstractListener> listeners = new ArrayList<>();

    static {
        // 새로운 리스너 추가 위치#########################
        registerListener(ServerInfoListener.getInstance());
    }

    /**
     * 리스너 등록
     */
    private static void registerListener(AbstractListener listener) {
        listeners.add(listener);
    }

    /**
     * 등록된 모든 리스너를 반환
     */
    public static List<AbstractListener> getListeners() {
        return new ArrayList<>(listeners);
    }
}
