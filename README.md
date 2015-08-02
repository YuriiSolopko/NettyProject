# NettyProject
<b><i>Описание проекта myNetty: </b></i><br>
<b>[domain]</b> : пакет с доменными объектами<br>
Request - объект запроса (ip, uri, timestamp, sent_bytes, received_bytes, speed)<br>
RequestsByIp - объект для построения таблицы запросов по IP - адресам (ip, количество запросов, время последнего запроса)<br>
RedirectRequest - объект для построения таблицы запросов переадресации (url, количество переадресация)<br>
<b>[jdbcManager]</b> : пакет классов для работы с базой данных<br>
RequestJDBCManager - интерфейс класса для работы с базой данных запросов<br>
RequestJDBCManagerImpl - реализация класса для работы с базой данных запросов<br>
<b>[service]</b> : пакет классов сервисов<br>
RequestService - интерфейс класса сервиса для обработки запросов (сохранение/получение данных). Использует класс для работы с Б.Д. - RequestJDBCManager<br>
RequestServiceImpl - реализация класса сервиса для обработки запросов (сохранение/получение данных), также хранит в себе счетчик активных подключений. Использует класс для работы с Б.Д. - RequestJDBCManager<br>
<b>[server]</b> : <br>
HttpNettyServer - класс для запуска сервера<br>
HttpNettyServerHandler - класс обработки запросов на сервер (использует класс сервиса - RequestService)<br>
HttpNettyServerInitializer - класс для инициализации сервера<br>
