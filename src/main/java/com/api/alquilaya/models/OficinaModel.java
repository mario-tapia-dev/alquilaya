package com.api.alquilaya.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "oficina_alquiler")
public class OficinaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_oficina")
    private Long idOficina;

    @NotBlank(message = "El nombre de la oficina es obligatorio")
    @Size(max = 100, message = "El nombre admite máximo 100 caracteres")
    @Column(name = "nombre_oficina", nullable = false, length = 100)
    private String nombreOficina;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 150, message = "La dirección admite máximo 150 caracteres")
    @Column(nullable = false, length = 150)
    private String direccion;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 50, message = "La ciudad admite máximo 50 caracteres")
    @Column(nullable = false, length = 50)
    private String ciudad;

    @NotBlank(message = "La región es obligatoria")
    @Size(max = 50, message = "La región admite máximo 50 caracteres")
    @Column(nullable = false, length = 50)
    private String region;

    @OneToMany(mappedBy = "oficina", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("oficina")
    private List<VehiculoModel> vehiculos;

    public OficinaModel() {
    }

    public OficinaModel(String nombreOficina, String direccion, String ciudad, String region) {
        this.nombreOficina = nombreOficina;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.region = region;
    }

    // Getters y Setters
    public Long getIdOficina() {
        return idOficina;
    }

    public void setIdOficina(Long idOficina) {
        this.idOficina = idOficina;
    }

    public String getNombreOficina() {
        return nombreOficina;
    }

    public void setNombreOficina(String nombreOficina) {
        this.nombreOficina = nombreOficina;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public List<VehiculoModel> getVehiculos() {
        return vehiculos;
    }

    public void setVehiculos(List<VehiculoModel> vehiculos) {
        this.vehiculos = vehiculos;
    }
}
