package com.ppopi.ppopihouse.pet.controller;

import com.ppopi.ppopihouse.pet.dto.request.PetCreateRequest;
import com.ppopi.ppopihouse.pet.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @GetMapping("/breeds")
    public List<String> getBreeds(@RequestParam String species) {
        return petService.getBreeds(species);
    }

    @PostMapping
    public void createPet(
            @AuthenticationPrincipal Long memberId,
            @RequestBody PetCreateRequest request
    ) {
        petService.createPet(memberId, request);
    }
}