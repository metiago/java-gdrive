package io.zbx.controllers;

import io.zbx.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

    @Autowired
    private FileService fileService;

    @RequestMapping(value = {"/"}, method = RequestMethod.GET)
    public ModelAndView index() {
        return new ModelAndView("index");
    }

    @RequestMapping(value = {"/auth-code"}, method = RequestMethod.GET)
    public ModelAndView authorize() throws Exception {

        return new ModelAndView("index");
    }
}
