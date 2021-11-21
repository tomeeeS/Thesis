# telki-ride-share

## Szükséges környezet
1. <a href="https://www.oracle.com/java/technologies/javase-jdk11-downloads.html">Java 11 SDK</a>
2. MySQL (windowson <a href="https://www.apachefriends.org/hu/download.html" >Xampp</a>) vagy <a href="https://docs.docker.com/get-docker/">Docker</a>
3. PostgreSQL <a href="https://www.2ndquadrant.com/en/blog/pginstaller-install-postgresql/">install</a> (<a href="https://www.pgadmin.org/download/">PgAdmin</a>)

## Első indítás
1. MySQL szerver indítása (windows: Xampp control paner MySQL + Apache start)
2. Adatbázis létrehozása: MySQL <a href="http://localhost/phpmyadmin/server_sql.php">admin panelben</a>
 lefuttatni a projekt_root/[create_database.sql](create_databse.sql) tartalmát
 	- Vagy Docker-ben: 
	<pre>
	> docker build -t telki-car-mysql ./docker_db
	> docker run -p 3306:3306 --name telki-db -d telki-car-mysql
	</pre>
3. Postgres vagy lokálisan kézzel + lefuttatni a docker_postgres/[create_database.sql](create_databse.sql) tartalmát
    - vagy docker-rel:
	<pre>
	> sudo docker build -t telki-car-postgres ./docker_postgres
	> sudo docker run -p 5432:5432 --name telki-postgres -d telki-car-postgres
	</pre>
		
4. Futtatható a Spring app pl Idea-ból vagy `> gradlew bootRun`

EGYÉB:
- ha valamelyik sima lokális használat után térsz át container-ből használatra, make sure, hogy nem fut már sima instance a gépen ami lefogná ugyan azt a portot
- Docker-compose out of order! (for now)
##
test login: user:admin@admin.hu pw:a
<br> <a href="http://localhost:8080/felhasznalok">http://localhost:8080/felhasznalok</a>: db, rest

<pre>
docker exec telki-db /usr/bin/mysqldump -u dbadmin --password=dbadmin --no-tablespaces ride_share > backup_timestamp/version.sql
</pre>
to import: mysql -u mysql_user -p DATABASE < backup.sql

##
Timezone-ra vonatkozó hibaüzenet esetén a mysql konzolba:
<pre>
SET GLOBAL time_zone = '+1:00';
</pre>
