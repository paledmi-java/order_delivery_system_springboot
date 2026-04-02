package org.pavelleonov.spring.springboot.order_delivery_system_springboot.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientCreateDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.item_dto.ItemResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions.ItemNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.BucketService;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.ClientService;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MenuController {

    ClientService clientService;
    ItemService itemService;
    BucketService bucketService;

    public MenuController(ClientService clientService, ItemService itemService, BucketService bucketService) {
        this.clientService = clientService;
        this.itemService = itemService;
        this.bucketService = bucketService;
    }

//    @GetMapping("/")
//    public String showHomeScreen(Principal principal, Model model){
//        if (principal != null) {
//            Client client = clientService.findClientByName(principal.getName());
//            model.addAttribute("client", client);
//        }
//        return "home-screen";
//    }

//    @GetMapping("/app/public/menu")
//    public String getAvailableItemsList(Model model){
//        List<Item> availableItemsList= itemService.findByIsAvailableTrue();
//        model.addAttribute("itemsList", availableItemsList);
//        return "available-items";
//    }

    @GetMapping("/app/public/menu/item")
    public String getItem(@RequestParam int itemId, Model model){
        ItemResponseDto itemDto = itemService.findItemDtoByItemId(itemId)
                .orElseThrow(()-> new ItemNotFoundException("Item not found"));

        model.addAttribute("item", itemDto);
        return "one-item-screen";
    }

//    @PostMapping("/app/user/putItemInBasket")
//    public String putItemInBasket(@RequestParam int itemId, Principal principal, Model model){
//        Client client = clientService.findClientByName(principal.getName());
//        Item item = itemService.findItemEntityByItemId(itemId);
//
//        Bucket bucket = bucketService.addItemToBucket(client, item, 1);
////        client.setBucket(bucket);
//
//        model.addAttribute("item", item);
//        return "one-item-screen";
//    }

//    @GetMapping("/app/user/bucket")
//    public String showClientBucket(Principal principal, Model model){
//        Client client = clientService.findClientByName(principal.getName());
//        List<BucketItem> clientBucketItems = client.getBucket().getBucketItems();
//        model.addAttribute("clientBucketItems", clientBucketItems);
//        return "client-bucket";
//    }

    @GetMapping("/login")
    public String login(){
        return "login-screen";
    }

    @GetMapping("/app/public/register")
    public String register(Model model){
        model.addAttribute("client", new ClientCreateDTO());
        return "register-screen";
    }

    @PostMapping("/addClient")
    public String addClient(@Valid @ModelAttribute ClientCreateDTO clientCreateDTO,
                            BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            return "register-screen";
        }

        clientService.saveClient(clientCreateDTO);

        return "redirect:/login";
    }

    @PostMapping("/logout")
    public String logOut(HttpSession session){
        session.invalidate();
        return "redirect:/login?logout";
    }

//    @GetMapping("/app/user/client-home-screen")
//    public String showClientHomeScreen(Principal principal, Model model){
//
//        if (principal == null) {
//            return "redirect:/login"; // нет пользователя → редирект
//        }
//
//        Client client = clientService.findByCredentialsLogin(principal.getName())
//                .orElseThrow(() -> new ClientNotFoundException("Client not found"));;
//
//        ClientViewDTO clientViewDTO = ClientViewDTO.builder()
//                .name(client.getName())
//                .login(client.getLogin())
//                .build();
//
//        model.addAttribute("client", clientViewDTO);
//        return "client-home-screen";
//    }
}
