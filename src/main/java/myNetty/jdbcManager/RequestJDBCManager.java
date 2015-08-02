package myNetty.jdbcManager;

import myNetty.domain.RedirectRequest;
import myNetty.domain.Request;
import myNetty.domain.RequestsByIp;

import java.sql.*;
import java.util.ArrayList;
import java.util.Locale;

/**
 * @author Yurii Solopko
 */
public class RequestJDBCManager {

    private Connection sqlConnection;
    private static final String DB_URL = "jdbc:oracle:thin:@127.0.0.1:1521:XE";
    private static final String LOGIN = "netty";
    private static final String PASSWORD = "netty";

    private static volatile RequestJDBCManager instance = new RequestJDBCManager();

    private RequestJDBCManager() {
        Locale.setDefault(Locale.ENGLISH);
        try {
            sqlConnection = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static RequestJDBCManager getInstance() {
        if (instance == null) {
            instance = new RequestJDBCManager();
        }
        return instance;
    }

    /**
     * сохраняет запись о запросе в базу данных
     * @param request класс запроса
     * @return количество изменненых строк в базе данных
     */
    public synchronized int create(Request request) {
        int res = 0;

        PreparedStatement sqlStatement = null;
        try {
            sqlStatement = sqlConnection.prepareStatement("INSERT INTO requests(id, src_ip, uri, time, sent_bytes, " +
                    "received_bytes, speed) VALUES(requests_seq.nextVal, ?, ?, ?, ?, ?, ?)");
            sqlStatement.setString(1, request.getSourceIP());
            sqlStatement.setString(2, request.getUri());
            sqlStatement.setTimestamp(3, request.getTime());
            sqlStatement.setInt(4, request.getSentBytes());
            sqlStatement.setInt(5, request.getReceivedBytes());
            sqlStatement.setDouble(6, request.getSpeed());

            res = sqlStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (sqlStatement != null) {
                try {
                    sqlStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return res;
    }

    /**
     * @return общее количество запросов
     */
    public synchronized Long getCount() {
        Long count = -1L;
        ResultSet resultSet = null;
        PreparedStatement sqlStatement = null;
        try {
            sqlStatement = sqlConnection.prepareStatement("SELECT COUNT(*) FROM requests");
            resultSet = sqlStatement.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    /**
     * @return количество уникальных запросов (по IP)
     */
    public synchronized Long getUniqueCount() {
        Long count = -1L;
        ResultSet resultSet = null;
        PreparedStatement sqlStatement = null;
        try {
            sqlStatement = sqlConnection.prepareStatement("SELECT COUNT(DISTINCT src_ip) FROM requests");
            resultSet = sqlStatement.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    /**
     * @return список запросов по IP (количество + время последнего запроса)
     */
    public synchronized ArrayList<RequestsByIp> getRequestsByIP() {
        ArrayList<RequestsByIp> requestsByIpList = new ArrayList<>();
        ResultSet resultSet = null;
        PreparedStatement sqlStatement = null;
        try {
            sqlStatement = sqlConnection.prepareStatement("SELECT src_ip, COUNT(id), MAX(time) FROM requests " +
                    "GROUP BY src_ip");
            resultSet = sqlStatement.executeQuery();
            while (resultSet.next()) {
                requestsByIpList.add(new RequestsByIp(resultSet.getString(1), resultSet.getLong(2),
                        resultSet.getTimestamp(3)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return requestsByIpList;
    }

    /**
     * @return список количества запросов на переадресацию по ссылкам
     */
    public synchronized ArrayList<RedirectRequest> getRedirectRequests() {
        ArrayList<RedirectRequest> redirectRequests = new ArrayList<>();
        ResultSet resultSet = null;
        PreparedStatement sqlStatement = null;
        try {
            sqlStatement = sqlConnection.prepareStatement("SELECT uri, COUNT(id) FROM requests " +
                    "WHERE uri LIKE '/redirect?url=%' GROUP BY uri");
            resultSet = sqlStatement.executeQuery();
            while (resultSet.next()) {
                redirectRequests.add(new RedirectRequest(resultSet.getString(1).substring(14), resultSet.getLong(2)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return redirectRequests;
    }

    /**
     * @return список(лог) последних 16 запросов
     */
    public synchronized ArrayList<Request> getLast16Requests() {
        ArrayList<Request> requests = new ArrayList<>();
        ResultSet resultSet = null;
        PreparedStatement sqlStatement = null;
        try {
            //ROWNUM is a LIMIT "analogue" in Oracle database
            //on MySQL dialect query would look like this:
            //"SELECT src_ip, uri, time, sent_bytes, received_bytes, speed FROM requests ORDER BY time DESC LIMIT 16"
            sqlStatement = sqlConnection.prepareStatement("SELECT src_ip, uri, time, sent_bytes, " +
                    "received_bytes, speed FROM (SELECT src_ip, uri, time, sent_bytes, received_bytes, speed " +
                    "FROM requests ORDER BY time DESC) WHERE ROWNUM <= 16");
            resultSet = sqlStatement.executeQuery();
            while (resultSet.next()) {
                requests.add(new Request(resultSet.getString(1), resultSet.getString(2), resultSet.getTimestamp(3),
                        resultSet.getInt(4), resultSet.getInt(5), resultSet.getDouble(6)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return requests;
    }

}
