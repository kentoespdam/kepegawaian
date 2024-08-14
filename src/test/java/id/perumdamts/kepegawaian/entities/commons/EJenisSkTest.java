package id.perumdamts.kepegawaian.entities.commons;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EJenisSkTest {

    @org.junit.jupiter.api.Test
    void value() {
        assertEquals("SK Kenaikan Pangkat/Gol", EJenisSk.SK_KENAIKAN_PANGKAT_GOLONGAN.value);
        assertEquals("SK Capeg", EJenisSk.SK_CAPEG.value);
        assertEquals("SK Pegawai Tetap", EJenisSk.SK_PEGAWAI_TETAP.value);
        assertEquals("SK Jabatan", EJenisSk.SK_JABATAN.value);
        assertEquals("SK Mutasi", EJenisSk.SK_MUTASI.value);
        assertEquals("SK Pensiun", EJenisSk.SK_PENSIUN.value);
        assertEquals("SK Lainnya", EJenisSk.SK_LAINNYA.value);
        assertEquals("SK Penyesuaian Gaji", EJenisSk.SK_PENYESUAIAN_GAJI.value);
        assertEquals("SK Kenaikan Gaji Berkala", EJenisSk.SK_KENAIKAN_GAJI_BERKALA.value);
        System.out.println(EJenisSk.SK_CAPEG);
    }

    @Test
    void getArray() {
        List<Map<String, Object>> list1 = Arrays.stream(EJenisSk.values()).map(eJenisSk -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", eJenisSk);
            map.put("nama", eJenisSk.value);
            return map;
        }).toList();
        System.out.println(list1);
    }
}