package com.bufalari.supplier.entity;

import jakarta.persistence.Column; // Import correct annotation
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Embeddable class for address details.
 * Classe embutível para detalhes de endereço.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressEmbeddable {
	

    @Column(length = 255) // Define length constraints
    private String street;

    @Column(length = 50)
    private String number;

    @Column(length = 100)
    private String complement;

    @Column(length = 100)
    private String neighbourhood;

    @Column(length = 100)
    private String city;

    @Column(length = 100) // Province/State can have longer names
    private String province;

    @Column(length = 20)
    private String postalCode;

    @Column(length = 100)
    private String country;
}