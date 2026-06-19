package id.perumdamts.kepegawaian.dto.master.profesi;

/**
 * Row projection for the {@code alat_kerja} master table.
 *
 * <p>Java 16 record — required by JOOQ {@code Records.mapping(RowType::new)}
 * because the functional interface expects an unambiguous canonical constructor.
 * The previous Lombok @Data class exposed both a no-arg and a 2-arg ctor, which
 * broke the compiler's ability to choose the target.
 */
public record AlatKerjaRow(Long id, String nama) {}
