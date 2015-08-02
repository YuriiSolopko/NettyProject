package myNetty.service;

import myNetty.domain.RedirectRequest;
import myNetty.domain.Request;
import myNetty.domain.RequestsByIp;

import java.util.ArrayList;

/**
 * Created by Jura on 02.08.2015.
 */
public interface RequestService {

    /**
     * increment active connections count
     */
    public void incrementActiveConnections();

    /**
     * decrement active connections count
     */
    public void decrementActiveConnections();

    /**
     * @return количество активных соединений
     */
    public Integer getActiveConnectionsCount();

    /**
     * сохраняет запись о запросе в базу данных
     * @param request класс запроса
     * @return количество изменненых строк в базе данных
     */
    public int saveRequest(Request request);

    /**
     * @return общее количество запросов
     */
    public Long getRequestCount();

    /**
     * @return количество уникальных запросов (по IP)
     */
    public Long getUniqueRequestCount();

    /**
     * @return список запросов по IP (количество + время последнего запроса)
     */
    public ArrayList<RequestsByIp> getRequestsByIP();

    /**
     * @return список количества запросов на переадресацию по ссылкам
     */
    public ArrayList<RedirectRequest> getRedirectRequests();

    /**
     * @return список(лог) последних 16 запросов
     */
    public ArrayList<Request> getLast16Requests();
}
