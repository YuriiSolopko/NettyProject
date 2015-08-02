# NettyProject
<b><i>Описание проекта myNetty: </b></i><br>
<b>[domain]</b> : пакет с доменными объектами<br>
<i>Request</i> - объект запроса (ip, uri, timestamp, sent_bytes, received_bytes, speed)<br>
<i>RequestsByIp</i> - объект для построения таблицы запросов по IP - адресам (ip, количество запросов, время последнего запроса)<br>
<i>RedirectRequest</i> - объект для построения таблицы запросов переадресации (url, количество переадресация)<br>
<b>[jdbcManager]</b> : пакет классов для работы с базой данных<br>
<i>RequestJDBCManager</i> - интерфейс класса для работы с базой данных запросов<br>
<i>RequestJDBCManagerImpl</i> - реализация класса для работы с базой данных запросов<br>
<b>[service]</b> : пакет классов сервисов<br>
<i>RequestService</i> - интерфейс класса сервиса для обработки запросов (сохранение/получение данных). Использует класс для работы с Б.Д. - RequestJDBCManager<br>
<i>RequestServiceImpl</i> - реализация класса сервиса для обработки запросов (сохранение/получение данных), также хранит в себе счетчик активных подключений. Использует класс для работы с Б.Д. - RequestJDBCManager<br>
<b>[server]</b> : <br>
<i>HttpNettyServer</i> - класс для запуска сервера<br>
<i>HttpNettyServerHandler</i> - класс обработки запросов на сервер (использует класс сервиса - RequestService)<br>
<i>HttpNettyServerInitializer</i> - класс для инициализации сервера<br>
<br>
Методы класса реализующего интерфейс RequestJDBCManager(работы с Б.Д.) синхронизированы.<br>
В проекте используется база данных Oracle 10g<br>
