package id.perumdamts.kepegawaian.enums;

import lombok.Getter;

@Getter
public enum AppPermission {
    MASTER_READ("MASTER:READ", "Master Read", "MASTER"),
    MASTER_WRITE("MASTER:WRITE", "Master Write", "MASTER"),
    MASTER_DELETE("MASTER:DELETE", "Master Delete", "MASTER"),

    PEGAWAI_READ("PEGAWAI:READ", "Pegawai Read", "PEGAWAI"),
    PEGAWAI_WRITE("PEGAWAI:WRITE", "Pegawai Write", "PEGAWAI"),
    PEGAWAI_DELETE("PEGAWAI:DELETE", "Pegawai Delete", "PEGAWAI"),

    KEPEGAWAIAN_READ("KEPEGAWAIAN:READ", "Kepegawaian Read", "KEPEGAWAIAN"),
    KEPEGAWAIAN_WRITE("KEPEGAWAIAN:WRITE", "Kepegawaian Write", "KEPEGAWAIAN"),
    KEPEGAWAIAN_DELETE("KEPEGAWAIAN:DELETE", "Kepegawaian Delete", "KEPEGAWAIAN"),

    PROFIL_READ("PROFIL:READ", "Profil Read", "PROFIL"),
    PROFIL_UPDATE("PROFIL:UPDATE", "Profil Update", "PROFIL"),
    PROFIL_APPROVE("PROFIL:APPROVE", "Profil Approve", "PROFIL"),

    CUTI_READ("CUTI:READ", "Cuti Read", "CUTI"),
    CUTI_CREATE("CUTI:CREATE", "Cuti Create", "CUTI"),
    CUTI_APPROVE("CUTI:APPROVE", "Cuti Approve", "CUTI"),

    PENGGAJIAN_READ("PENGGAJIAN:READ", "Penggajian Read", "PENGGAJIAN"),
    PENGGAJIAN_WRITE("PENGGAJIAN:WRITE", "Penggajian Write", "PENGGAJIAN"),
    PENGGAJIAN_PROCESS("PENGGAJIAN:PROCESS", "Penggajian Process", "PENGGAJIAN"),

    SYSTEM_MANAGE_USER("SYSTEM:MANAGE_USER", "System Manage User", "SYSTEM"),
    SYSTEM_MANAGE_ROLE("SYSTEM:MANAGE_ROLE", "System Manage Role", "SYSTEM"),

    CUTI_WRITE("CUTI:WRITE", "Cuti Write", "CUTI"),
    LAPORAN_READ("LAPORAN:READ", "Laporan Read", "LAPORAN"),
    PENGGAJIAN_DELETE("PENGGAJIAN:DELETE", "Penggajian Delete", "PENGGAJIAN");

    private final String authority;
    private final String label;
    private final String group;

    AppPermission(String authority, String label, String group) {
        this.authority = authority;
        this.label = label;
        this.group = group;
    }

    public static class Authority {
        public static final String MASTER_READ = "MASTER:READ";
        public static final String MASTER_WRITE = "MASTER:WRITE";
        public static final String MASTER_DELETE = "MASTER:DELETE";
        public static final String PEGAWAI_READ = "PEGAWAI:READ";
        public static final String PEGAWAI_WRITE = "PEGAWAI:WRITE";
        public static final String PEGAWAI_DELETE = "PEGAWAI:DELETE";
        public static final String KEPEGAWAIAN_READ = "KEPEGAWAIAN:READ";
        public static final String KEPEGAWAIAN_WRITE = "KEPEGAWAIAN:WRITE";
        public static final String KEPEGAWAIAN_DELETE = "KEPEGAWAIAN:DELETE";
        public static final String PROFIL_READ = "PROFIL:READ";
        public static final String PROFIL_UPDATE = "PROFIL:UPDATE";
        public static final String PROFIL_APPROVE = "PROFIL:APPROVE";
        public static final String CUTI_READ = "CUTI:READ";
        public static final String CUTI_CREATE = "CUTI:CREATE";
        public static final String CUTI_APPROVE = "CUTI:APPROVE";
        public static final String PENGGAJIAN_READ = "PENGGAJIAN:READ";
        public static final String PENGGAJIAN_WRITE = "PENGGAJIAN:WRITE";
        public static final String PENGGAJIAN_PROCESS = "PENGGAJIAN:PROCESS";
        public static final String SYSTEM_MANAGE_USER = "SYSTEM:MANAGE_USER";
        public static final String SYSTEM_MANAGE_ROLE = "SYSTEM:MANAGE_ROLE";
        public static final String CUTI_WRITE = "CUTI:WRITE";
        public static final String LAPORAN_READ = "LAPORAN:READ";
        public static final String PENGGAJIAN_DELETE = "PENGGAJIAN:DELETE";
    }
}
