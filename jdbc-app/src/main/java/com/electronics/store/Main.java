package com.electronics.store;

import com.electronics.store.service.StoreService;

public class Main {
    public static void main(String[] args) {
        StoreService service = new StoreService();
        service.start();
    }
}