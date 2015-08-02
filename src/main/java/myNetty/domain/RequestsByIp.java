package myNetty.domain;

import java.sql.Timestamp;

/**
 * @author Yurii Solopko
 * класс-оболочка для таблицы количества запросов по IP
 */
public class RequestsByIp {

    private String ip;

    private Long count;

    private Timestamp lastRequestTime;

    public RequestsByIp() {

    }

    public RequestsByIp(String ip, Long count, Timestamp time) {
        this.ip = ip;
        this.count = count;
        this.lastRequestTime = time;
    }

    @Override
    public String toString() {
        return "IP : " + ip + ", No. of requests = " + count + ", last requset time : " + lastRequestTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Timestamp getLastRequestTime() {
        return lastRequestTime;
    }

    public void setLastRequestTime(Timestamp lastRequestTime) {
        this.lastRequestTime = lastRequestTime;
    }
}
