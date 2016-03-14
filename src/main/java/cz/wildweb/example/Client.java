package cz.wildweb.example;

import cz.wildweb.api.HttpClient;

public class Client {

    public static void main(String[] args) {
        HttpClient.get().open("localhost", 8081).request(response -> {
            System.out.println(response);
        });
    }

}
