package ru.bobkov.infobip;


import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.swagger.Swagger2Feature;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import ru.bobkov.infobip.rest.FileUploadRestServiceImpl;

import java.util.Collections;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class TaskApplication {


    @Bean
    public Server rsServer(final Bus bus) {
        JAXRSServerFactoryBean endpoint = new JAXRSServerFactoryBean();
        endpoint.setBus(bus);
        endpoint.setServiceBeans(Collections.singletonList(new FileUploadRestServiceImpl("D:/test/")));
        endpoint.setProviders(Collections.singletonList(new JacksonJsonProvider()));
        endpoint.setAddress("/");
        endpoint.setFeatures(Collections.singletonList(createSwaggerFeature()));
        return endpoint.create();
    }


    private Swagger2Feature createSwaggerFeature() {
        Swagger2Feature swagger2Feature = new Swagger2Feature();
        swagger2Feature.setPrettyPrint(true);
        swagger2Feature.setTitle("Infobip test project");
        swagger2Feature.setContact("serj.bobkov@gmail.com");
        swagger2Feature.setDescription("Test project for Infobip");
        swagger2Feature.setVersion("1.0.0");
        swagger2Feature.setSupportSwaggerUi(true);
        return swagger2Feature;
    }


    public static void main(final String[] args) {
        SpringApplication.run(TaskApplication.class, args);
    }

}
