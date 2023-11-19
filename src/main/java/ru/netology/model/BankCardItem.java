package ru.netology.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;


    @Data
    @RequiredArgsConstructor
    public class BankCardItem {
        private final String name;
        private final String city;
        private final String date;
        private final String phone;

    }

