package id.perumdamts.kepegawaian.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kepegawaian.slip-gaji")
public record SlipGajiProperties(
        Kop kop
) {
    public record Kop(
            String instansi,
            String unitKerja,
            String alamat,
            String telepon,
            String fax,
            String website,
            String email,
            String logoPath
    ) {
        public Kop {
            if (instansi == null) { instansi = "Perusahaan Umum Daerah Air Minum Tirta Satria"; }
            if (unitKerja == null) { unitKerja = "KABUPATEN BANYUMAS"; }
            if (alamat == null) { alamat = "Jl. Prof. Dr. Suharso No. 52 PURWOKERTO 53114"; }
            if (telepon == null) { telepon = "(0281) 635831"; }
            if (fax == null) { fax = "(0281) 635831"; }
            if (website == null) { website = "www.tirtasatria.co.id"; }
            if (email == null) { email = "info@tirtasatria.co.id"; }
            if (logoPath == null) { logoPath = "images/logo-tirta-satria.png"; }
        }
    }
}
