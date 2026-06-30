package id.perumdamts.kepegawaian.helpers.cuti;

public class MinimalCutiRule {
    public static void check(int totalHariKerja, int totalSisaKuota) {
        if (totalHariKerja < 3) {
            if (totalSisaKuota >= 3) {
                throw new RuntimeException("Pengambilan Cuti minimal 3 hari");
            } else if (totalHariKerja < totalSisaKuota) {
                throw new RuntimeException("Sisa Kuota Cuti " + totalSisaKuota + " hari harus diambil semua");
            }
        }
    }
}
