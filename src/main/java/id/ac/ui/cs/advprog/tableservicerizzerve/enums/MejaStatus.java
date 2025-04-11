package id.ac.ui.cs.advprog.tableservicerizzerve.enums;

import lombok.Getter;

@Getter
public enum MejaStatus {
    TERSEDIA("TERSEDIA"),
    TERPAKAI("TERPAKAI");

    private final String value;

    MejaStatus(String value) {
        this.value = value;
    }

    public static MejaStatus fromString(String status) {
        if (status == null || status.trim().isEmpty()) {
            return TERSEDIA;
        }
        for (MejaStatus mejaStatus : MejaStatus.values()) {
            if (mejaStatus.getValue().equalsIgnoreCase(status.trim())) {
                return mejaStatus;
            }
        }
        throw new IllegalArgumentException("Invalid status: " + status);
    }
}