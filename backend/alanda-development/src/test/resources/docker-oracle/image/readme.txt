The files in this folder are from 
https://github.com/oracle/docker-images/tree/master/OracleDatabase/SingleInstance/dockerfiles/11.2.0.2

1. Set the password you want for the database in the xe.rsp file.
2. Follow the instructions in the Dockerfile to build an oracle image
   (When connect to oracle does not work from inside the container http://www.dadbm.com/how-to-fix-ora-12547-tns-lost-contact-when-try-to-connect-to-oracle/)
3. Use the included docker-compose file to start the local oracle db for development (TODO)

4. create a user for 
create user alanda identified by alanda;

grant connect,resource, create any view to alanda;
