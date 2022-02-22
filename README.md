# CountryManagement microservice

This is a microservice which provides a list of countries and more detailed information per country.
Implemented reactive and traditional REST API separately by Spring Boot.

## How to run

This application is packaged as a war which has Tomcat embedded.

* Clone the repository
* JDK 1.8 and Maven 3.x in your environment
* Build the project by running 
     
  ``` mvn clean package ```

* Once build successfully, run the application by either of these two methods:
```
     java -jar target/country-0.0.1.jar
or
     mvn spring-boot:run
```

You should see log message below if you run it successfully: 
```
    2022-02-22 19:14:43.433  INFO 7604 --- [           main] com.test.country.CountryApplication      : Started CountryApplication in 5.889 seconds (JVM running for 7.044)
```

##About APIs

The service is a simple country query REST service.

You can consume the API with either reactive or traditional way, API will distinguish it by header in request:

```
    Accept: text/event-stream
or
    Accept: application/json
```

In reactive REST API, there is a field "type" to indicate if exception happens, and will
have error information if "type" is ERROR

Response without error:
```
data:{"country":{"name":"Finland","country_code":"FI","capital":"Helsinki","population":5530719,"flag_file_url":"https://flagcdn.com/w320/fi.png"},"type":"DATA"}
```
Response with error:
```
data:{"type":"ERROR","error":{"status":404,"message":"Not Found"}}
```

Here are some endpoints:

### Get all countries

```
      http://localhost:8080/countries
```
Try this API with either Postman or curl in command terminal, here is the example for curl:

Reactive
```
      curl --location --request GET 'http://localhost:8080/countries' --header 'Accept: text/event-stream'
```
Or traditional
```
      curl --location --request GET 'http://localhost:8080/countries' --header 'Accept: application/json' 
```

### Get detailed information per country by name

```
      http://localhost:8080/countries/name/{name}
```

Here is the example for curl:

Reactive
```
      curl --location --request GET 'http://localhost:8080/countries/name/Finland' --header 'Accept: text/event-stream'
```
Or traditional
```
      curl --location --request GET 'http://localhost:8080/countries/name/Finland' --header 'Accept: application/json'
```

