package org.notificationengine.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HelloWorldController {

	@RequestMapping("/HelloWorld.do")
    public String helloWorld(Model model) {
        model.addAttribute("message", "Notification Engine says Hello World !");
        return "HelloWorld";
    }

    @RequestMapping("/")
    public String adminConsole() {
        return "redirect:/admin/index.html";
    }
}
