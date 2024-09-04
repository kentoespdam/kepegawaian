package id.perumdamts.kepegawaian.entities.commons;

public enum     EJenisSp {
    TEGURAN_LISAN("Teguran Lisan"),
    SP_1("SP 1"),
    SP_2("SP 2"),
    SP_3("SP 3"),
    SANKSI_DENGAN_SK("Sanksi Dengan SK");

    public final String value;

    EJenisSp(String value) {
        this.value = value;
    }
}
