package com.fast0n.wififinder.main;

public class Main {

    String ssid, location, pwdtype, datetime, nameLocation;
    int counter, size;

    public Main(int counter, String ssid, String location, String pwdtype, String datetime, String nameLocation, int size) {
        this.counter = counter;
        this.ssid = ssid;
        this.location = location;
        this.pwdtype = pwdtype;
        this.datetime = datetime;
        this.nameLocation = nameLocation;
        this.size = size;

    }

}