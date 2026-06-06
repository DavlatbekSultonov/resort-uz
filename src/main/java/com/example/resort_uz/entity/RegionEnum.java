package com.example.resort_uz.entity;

public enum RegionEnum {
    TOSHKENT_VILOYATI(1, "Toshkent viloyati"),
    TOSHKENT_SHAHAR(2, "Toshkent shahar"),
    ANDIJON(3, "Andijon"),
    FARGONA(4, "Farg'ona"),
    NAMANGAN(5, "Namangan"),
    SAMARQAND(6, "Samarqand"),
    BUXORO(7, "Buxoro"),
    NAVOIY(8, "Navoiy"),
    QASHQADARYO(9, "Qashqadaryo"),
    SURXONDARYO(10, "Surxondaryo"),
    JIZZAX(11, "Jizzax"),
    SIRDARYO(12, "Sirdaryo"),
    XORAZM(13, "Xorazm"),
    QORAQALPOGISTON(14, "Qoraqalpog'iston");

    private final int id;
    private final String name;

    RegionEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }
}