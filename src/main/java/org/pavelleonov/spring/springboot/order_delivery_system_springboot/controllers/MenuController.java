package org.pavelleonov.spring.springboot.order_delivery_system_springboot.controllers;

import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Client;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Item;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.ClientService;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class MenuController {

    ClientService clientService;
    ItemService itemService;

    public MenuController(ClientService clientService, ItemService itemService) {
        this.clientService = clientService;
        this.itemService = itemService;
    }

    @GetMapping("/")
    public String showHomeScreen(Principal principal, Model model){
        if (principal != null) {
            Client client = clientService.findClientByName(principal.getName());
            model.addAttribute("client", client);
        }
        return "home-screen";
    }

    @GetMapping("/app/public/menu")
    public String getAvailableItemsList(Model model){
        List<Item> availableItemsList= itemService.findByIsAvailableTrue();
        model.addAttribute("itemsList", availableItemsList);
        return "available-items";
    }
}
