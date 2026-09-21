package com.renova.renova.Pck_Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teste")
public class TestMapping {

    @GetMapping()
    public String teste(){
        return "isso é um teste de mapping!!! fé em Deus que agora vai";
    }
}
