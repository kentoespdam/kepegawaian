package id.perumdamts.kepegawaian.services.profil.kartuIdentitas;

import id.perumdamts.kepegawaian.entities.master.JenisKitas;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.LongStream;

class KartuIdentitasServiceTest {

    private final List<JenisKitas> jenisKitasList = List.of(
            new JenisKitas(1L),
            new JenisKitas(2L),
            new JenisKitas(3L)
    );

    @Transactional
//    @Test
    void findAll() {
        List<JenisKitas> collect = LongStream.range(1, 4)
                .mapToObj(JenisKitas::new)
                .toList();
        List<JenisKitas> list = collect.stream().map(col -> {
                    boolean contains = jenisKitasList.contains(col);
                    return contains ? col : null;
                })
                .toList();
        System.out.println(list.size());
        System.out.println(list);

        // check list has null
        if (list.contains(null)) {
            System.out.println("null detected");
        }

        for(int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i)+" == "+jenisKitasList.get(i));
            System.out.println(list.get(i)==jenisKitasList.get(i));
        }
    }
}