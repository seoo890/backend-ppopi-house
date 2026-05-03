package com.ppopi.ppopihouse.pet.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PetCreateRequest {

    private String name;
    private String species;
    private String breed;
    private int age;
    private String sex;
    private int color;
}