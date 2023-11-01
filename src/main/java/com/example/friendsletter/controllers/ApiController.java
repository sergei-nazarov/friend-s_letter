package com.example.friendsletter.controllers;


import com.example.friendsletter.data.Letter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api1")
public class ApiController {


    @GetMapping("/qwe")
    Letter cdcdcdcd() {
        return new Letter("qwe", "dwdwdwdwd", LocalDateTime.now(), true, true);
    }


}
