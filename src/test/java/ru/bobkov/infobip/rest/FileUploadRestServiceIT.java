package ru.bobkov.infobip.rest;


import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.ws.rs.core.Response;
import java.util.Random;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FileUploadRestServiceIT {


    @LocalServerPort
    private int port;


    @Test
    public void testUploadParallel() throws Exception {
        ExecutorService executorService = null;
        CloseableHttpClient client = null;

        try {
            final int parallelRequestCount = 100;

            executorService = Executors.newFixedThreadPool(parallelRequestCount);

            final int contentLength = 1024 * 1024 * 50;
            final byte[] file = new byte[contentLength];

            final Random random = new Random(System.currentTimeMillis());

            final CompletionService<Integer> ecs = new ExecutorCompletionService<Integer>(executorService);

            final CyclicBarrier barrier = new CyclicBarrier(parallelRequestCount);

            //CloseableHttpClient client = HttpClients.createDefault();


            final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
            cm.setMaxTotal(parallelRequestCount);
            cm.setDefaultMaxPerRoute(parallelRequestCount);

            client = HttpClients.custom()
                    .setConnectionManager(cm)
                    .build();


            for (int i = 0; i < parallelRequestCount; i++) {

                CloseableHttpClient finalClient = client;
                Callable<Integer> callable = () -> {


                    HttpPost httpPost = new HttpPost("http://localhost:" + port + "/api/v1/upload/");

                    httpPost.setEntity(new ByteArrayEntity(file));
                    httpPost.addHeader("X-Upload-File", "test" + random.nextInt(10000) + ".zip");


                    barrier.await();

                    CloseableHttpResponse response = finalClient.execute(httpPost);
                    return response.getStatusLine().getStatusCode();

                };

                ecs.submit(callable);
            }


            for (int i = 0; i < parallelRequestCount; i++) {
                Integer r = ecs.take().get(1L, TimeUnit.MINUTES);
                assertThat(r).isEqualTo(Response.Status.OK.getStatusCode());
            }

        } finally {
            if (executorService != null) {
                executorService.shutdown();
            }

            if (client != null) {
                client.close();
            }
        }

    }

}
