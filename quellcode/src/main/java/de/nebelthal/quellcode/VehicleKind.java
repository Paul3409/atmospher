package de.nebelthal.quellcode;

public enum VehicleKind {
    MZF_ELW("mzf_elw", "MZF / ELW", "FL NT 10/18-01", 1.9f, 2.65f, 0.32),
    DLK_18("dlk_18", "DLK 18", "FL NT 10/32-01", 2.4f, 3.35f, 0.24),
    LF_10("lf_10", "LF 10", "FL NT 10/42-01", 2.4f, 3.25f, 0.25),
    TSF_W("tsf_w", "TSF-W", "FL NT 10/48-01", 2.05f, 2.85f, 0.27),
    MZB("mzb", "MZB", "FL NT 10/79-01", 2.1f, 1.8f, 0.28),
    RW_2("rw_2", "RW 2", "FL NT 10/52-01", 2.4f, 3.25f, 0.24);

    public final String id, title, callsign;
    public final float width, height;
    public final double maxSpeed;
    VehicleKind(String id, String title, String callsign, float width, float height, double maxSpeed) {
        this.id = id; this.title = title; this.callsign = callsign;
        this.width = width; this.height = height; this.maxSpeed = maxSpeed;
    }
}
