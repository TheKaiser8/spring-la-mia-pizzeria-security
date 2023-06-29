package org.lessons.springlamiapizzeriacrud.controller;

import jakarta.validation.Valid;
import org.lessons.springlamiapizzeriacrud.model.Offer;
import org.lessons.springlamiapizzeriacrud.model.Pizza;
import org.lessons.springlamiapizzeriacrud.repository.OfferRepository;
import org.lessons.springlamiapizzeriacrud.repository.PizzaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/offers")
public class OfferController {

    // per fare una query su database ho bisogno del repository
    @Autowired
    PizzaRepository pizzaRepository;
    @Autowired
    OfferRepository offerRepository;

    // CREATE METHODS
    @GetMapping("/create")
    public String create(@RequestParam("pizzaId") Integer pizzaId, Model model) {
        // creo una nuova offerta
        Offer offer = new Offer();
        // precarica la data di inizio offerta con la data odierna
        offer.setStartOfferDate(LocalDate.now());
        // precarico la pizza con quella passata come parametro
        Optional<Pizza> pizza = pizzaRepository.findById(pizzaId);
        if (pizza.isEmpty()) {
            // ritorno un HTTP Status 404 Not Found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La pizza con ID " + pizzaId + " non è stata trovata");
        }
        offer.setPizza(pizza.get()); // se la pizza viene trovata imposto l'offerta su quella pizza
        // aggiungo al model l'attributo con l'offerta
        model.addAttribute("offer", offer);
        return "/offers/form";
    }

    @PostMapping("/create")
    public String store(@Valid @ModelAttribute("offer") Offer formOffer, BindingResult bindingResult) {
        // verifico se in validazione ci sono stati errori
        if (bindingResult.hasErrors()) {
            // se ci sono errori ricreo il template del form (unico per create ed edit)
            return "/offers/form";
        }
        // se non ci sono errori salvo l'offerta
        offerRepository.save(formOffer);
        // faccio una redirect alla pagina di dettaglio della pizza
        return "redirect:/pizzas/" + formOffer.getPizza().getId(); // concateno la stringa di reindirizzamento prendendo l'id della pizza attraverso l'input hidden del form dell'offerta (formOffer)
    }

    // EDIT METHODS
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer offerId, Model model) {
        // verificare se su database esiste l'offerta con quell'id
        Optional<Offer> offer = offerRepository.findById(offerId);
        if (offer.isEmpty()) {
            // ritorno un HTTP Status 404 Not Found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "L'offerta con ID " + offerId + " non è stata trovata");
        }
        // recupero i dati dell'offerta passandoli al model
        model.addAttribute("offer", offer.get());
        // ritorno il template del form
        return "/offers/form";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Integer id, @Valid @ModelAttribute("offer") Offer formOffer, BindingResult bindingResult) {
        // verificare se su database esiste l'offerta con quell'id
        Optional<Offer> offerToEdit = offerRepository.findById(id);
        if (bindingResult.hasErrors()) {
            // se ci sono errori ricreo il template del form (unico per create ed edit)
            return "/offers/form";
        }
        if (offerToEdit.isEmpty()) {
            // ritorno un HTTP Status 404 Not Found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "L'offerta con ID " + id + " non è stata trovata");
        }
        // setto l'id dell'offerta al formOffer
        formOffer.setId(id);
        // salvo il formOffer su database (update)
        offerRepository.save(formOffer);
        // faccio una redirect alla pagina di dettaglio della pizza
        return "redirect:/pizzas/" + formOffer.getPizza().getId(); // concateno la stringa di reindirizzamento prendendo l'id della pizza attraverso l'input hidden del form dell'offerta (formOffer)
    }
}
