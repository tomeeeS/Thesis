package tmit.bme.telkicar.controller.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UjFuvarIgenyDTO {

    private String datum;
    private int ora;
    private int perc;
    private String honnan;
    private String hova;
    private int utasokSzama;
    @Builder.Default
    private String info = "";
    @NotNull
    @NotEmpty
    private Double destinationLat;
    @NotNull
    @NotEmpty
    private Double destinationLng;
    @NotNull
    @NotEmpty
    private String destinationAddress;
    @NotNull
    @NotEmpty
    private String destinationPlaceID;
    @NotNull
    @NotEmpty
    private Double startLat;
    @NotNull
    @NotEmpty
    private Double startLng;
    @NotNull
    @NotEmpty
    private String startAddress;
    @NotNull
    @NotEmpty
    private String startPlaceID;
    @NotNull
    @NotEmpty
    private Double timeFlexibilityPercentage;
    @NotNull
    @NotEmpty
    private Double distanceFlexibilityPercentage;
}
