package org.miasta.atcsimulator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PlanesController {

    @Autowired
    private Simulation simulation;

    @GetMapping("/planes")
    public void planes(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("planes", simulation.planes());
    }

}

