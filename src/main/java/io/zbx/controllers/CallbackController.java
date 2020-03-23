package io.zbx.controllers;

import io.zbx.dto.TokenDTO;
import io.zbx.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/callback")
public class CallbackController {

    @Autowired
    private TokenService tokenService;

    @RequestMapping(value = "/auth-code", method = RequestMethod.POST)
    public void authCode(@RequestBody TokenDTO token, HttpServletRequest request) throws Exception {
        tokenService.save(token, request);
    }
}
