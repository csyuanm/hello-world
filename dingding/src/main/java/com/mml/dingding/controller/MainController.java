package com.mml.dingding.controller;

import com.mml.dingding.biz.ACBiz;
import com.mml.dingding.model.ACRequest;
import com.mml.dingding.model.ACResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/index")
public class MainController {

    @RequestMapping("/hello")
    public String hello() {
        return "hello";
    }


    //    controller call biz layout
    @Autowired
    ACBiz acBiz;

    @RequestMapping("/ac")
    public ACResponse ac(ACRequest acRequest) {
        ACResponse response = null;
        if (acRequest != null) {
            response = acBiz.addContact(acRequest);
        }
        return response;
    }
}