package myNetty.jdbcManager;

import myNetty.domain.RedirectRequest;
import myNetty.domain.Request;
import myNetty.domain.RequestsByIp;

import java.util.ArrayList;

/**
 * Created by Jura on 02.08.2015.
 */
public interface RequestJDBCManager {

    /**
     * сохраняет запись о запросе в базу данных
     * @param request класс запроса
     * @return количество изменненых строк в базе данных
     */
    public int create(Request request);

    /**
     * @return общее количество запросов
     */
    public Long getCount();

    /**
     * @return количество уникальных запросов (по IP)
     */
    public Long getUniqueCount();

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
