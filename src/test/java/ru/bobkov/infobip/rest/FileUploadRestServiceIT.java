package ru.bobkov.infobip.rest;


import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FileUploadRestServiceIT {


    @LocalServerPort
    private int port;





    @Test
    public void getLastJson() throws Exception {

        int parralelRequestCount=100;

        ExecutorService executorService= Executors.newFixedThreadPool(parralelRequestCount);



        int contentLength = 1024 * 1024 * 50;
        byte[] b = new byte[contentLength];


        Random random = new Random(System.currentTimeMillis());

        CompletionService<Integer> ecs = new ExecutorCompletionService<Integer>(executorService);

        CyclicBarrier barrier = new CyclicBarrier(parralelRequestCount);

        for(int i=0; i<parralelRequestCount; i++) {

            Callable<Integer> callable = () -> {

                CloseableHttpClient client = HttpClients.createDefault();
                HttpPost httpPost = new HttpPost("http://localhost:" + port + "/api/v1/upload/");

                httpPost.setEntity(new ByteArrayEntity(b));
                httpPost.addHeader("X-Upload-File", "test"+random.nextInt(10000)+".zip");

                System.out.println("client wait");
                barrier.await();

                System.out.println("make post request");
                CloseableHttpResponse response = client.execute(httpPost);
                client.close();

                return response.getStatusLine().getStatusCode();

            };

            ecs.submit(callable);
        }


        for (int i = 0; i < parralelRequestCount; i++) {
            System.out.println("try to wait");
            Integer r = ecs.take().get(1L, TimeUnit.MINUTES);
            assertThat(r).isEqualTo(Response.Status.OK.getStatusCode());

        }

    }

}
