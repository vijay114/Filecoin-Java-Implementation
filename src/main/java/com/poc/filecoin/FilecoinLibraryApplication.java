package com.poc.filecoin;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class FilecoinLibraryApplication {


    public static void main(String[] args) throws Exception {
        SpringApplication.run(FilecoinLibraryApplication.class, args);
    }

}
