package id.perumdamts.kepegawaian.entities.commons;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sqids.Sqids;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class HashIdTest {
    private Sqids sqids;
    private Sqids encoder;

    @BeforeEach
    void setup() {
        sqids = Sqids.builder()
                .minLength(16)
                .build();
        encoder = Sqids.builder()
//                .minLength(16)
//                .alphabet("4xYiXRmja3hF9A")
                .build();
    }

    @Test
    public void hash() {
        String first = "UkLWZg9DAJQ7Xlrz";
        String second = "UkLWZg9DAJQ7Xlrz";
        Sqids build = Sqids.builder()
                .minLength(16)
                .build();
        Long id = 1L;
        log.info("id : {}", id);
        String hashed = build.encode(List.of(1L));
        assertNotNull(hashed);
        log.info("hashed : {}", hashed);
        List<Long> decode = build.decode(hashed);
        assertNotNull(decode);
        log.info("decode : {}", decode);
        Long id2 = decode.getFirst();
        assertEquals(id, id2);
        log.info("id : {}, id2 : {}", id, id2);

        List<Long> decode1 = build.decode(first);
        log.info("decode1: {}", decode1.getFirst());
        List<Long> decode2 = build.decode(second);
        log.info("decode2: {}", decode2.getFirst());
    }

    @Test
    void testAgain() {
        Long id = 88L;
        List<String> encodedList = new ArrayList<>();
        LocalTime time = LocalTime.now();
        Long jam = (long) time.getHour();
        Long menit = (long) time.getMinute();
        Long second = (long) time.getSecond();
        Long nano=(long) time.getNano();

        IntStream.range(0, 5).forEach(i -> {
            try {
                String encoded = encoder.encode(List.of(id, jam, menit, second, nano));
                encodedList.add(encoded);
                TimeUnit.SECONDS.sleep(1);
            }catch (Exception e){
                log.info(e.getMessage());
            }
        });

        log.info("encoded list: {}", encodedList);
        log.info("decoded: {}", sqids.decode("qTFrufVwobyTcvJX"));
//        assertEquals("3T2WpPTcf5WsrXq2", encodedList.getFirst());
    }
}
