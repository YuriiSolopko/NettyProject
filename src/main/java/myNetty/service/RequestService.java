package myNetty.service;

import myNetty.domain.RedirectRequest;
import myNetty.domain.Request;
import myNetty.domain.RequestsByIp;
import myNetty.jdbcManager.RequestJDBCManager;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Yurii Solopko
 */
public class RequestService {

    private RequestJDBCManager requestJDBCManager;

    public static final String CONNECTIONS = "connections";
    private ConcurrentHashMap<String, Integer> activeConnections;

    public RequestService() {
        requestJDBCManager = RequestJDBCManager.getInstance();
        activeConnections = new ConcurrentHashMap<>();
        activeConnections.put(CONNECTIONS, 0);
    }

    public synchronized void incrementActiveConnections() {
        this.activeConnections.put(CONNECTIONS, this.activeConnections.get(CONNECTIONS) + 1);
    }


    public synchronized void decrementActiveConnections() {
        this.activeConnections.put(CONNECTIONS, this.activeConnections.get(CONNECTIONS) - 1);
    }

    /**
     * @return количество активных соединений
     */
    public synchronized Integer getActiveConnectionsCount() {
        return this.activeConnections.get(CONNECTIONS);
    }

    /**
     * сохраняет запись о запросе в базу данных
     * @param request класс запроса
     * @return количество изменненых строк в базе данных
     */
    public int saveRequest(Request request) {
        return requestJDBCManager.create(request);
    }

    /**
     * @return общее количество запросов
     */
    public Long getRequestCount() {
        return requestJDBCManager.getCount();
    }

    /**
     * @return количество уникальных запросов (по IP)
     */
    public Long getUniqueRequestCount() {
        return requestJDBCManager.getUniqueCount();
    }

    /**
     * @return список запросов по IP (количество + время последнего запроса)
     */
    public ArrayList<RequestsByIp> getRequestsByIP() {
        return requestJDBCManager.getRequestsByIP();
    }

    /**
     * @return список количества запросов на переадресацию по ссылкам
     */
    public ArrayList<RedirectRequest> getRedirectRequests() {
        return requestJDBCManager.getRedirectRequests();
    }

    /**
     * @return список(лог) последних 16 запросов
     */
    public ArrayList<Request> getLast16Requests() {
        return requestJDBCManager.getLast16Requests();
    }
}
