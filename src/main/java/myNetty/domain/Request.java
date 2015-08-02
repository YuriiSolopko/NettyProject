package myNetty.domain;

import java.sql.Timestamp;

/**
 * @author Yurii Solopko
 * Класс-оболочка запроса на сервер
 */
public class Request {

    private String sourceIP;

    private String uri;

    private Timestamp time;

    private Integer sentBytes;

    private Integer receivedBytes;

    private Double speed;

    public Request() {
        time = new Timestamp(System.currentTimeMillis());
    }

    public Request(String ip, String uri, Timestamp timestamp, Integer sentBytes, Integer receivedBytes, Double speed) {
        this.sourceIP = ip;
        this.uri = uri;
        this.time = timestamp;
        this.sentBytes = sentBytes;
        this.receivedBytes = receivedBytes;
        this.speed = speed;
    }

    @Override
    public String toString() {
        return "Request [IP:" + sourceIP + ", uri=" + uri + ", time=" + time + ", sent bytes=" + sentBytes +
                ", received bytes=" + receivedBytes + ", speed=" + speed + "(b/s)";
    }

    public String getSourceIP() {
        return sourceIP;
    }

    public void setSourceIP(String sourceIP) {
        this.sourceIP = sourceIP;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public Integer getSentBytes() {
        return sentBytes;
    }

    public void setSentBytes(Integer sentBytes) {
        this.sentBytes = sentBytes;
    }

    public Integer getReceivedBytes() {
        return receivedBytes;
    }

    public void setReceivedBytes(Integer receivedBytes) {
        this.receivedBytes = receivedBytes;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }
}
