package com.api.alquilaya.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "vehiculo")
public class VehiculoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehiculo")
    private Long idVehiculo;

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 50, message = "La marca admite máximo 50 caracteres")
    @Column(nullable = false, length = 50)
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    @Size(max = 50, message = "El modelo admite máximo 50 caracteres")
    @Column(nullable = false, length = 50)
    private String modelo;

    @NotNull(message = "El año es obligatorio")
    @Min(value = 1900, message = "El año no puede ser menor a 1900")
    @Max(value = 2100, message = "El año no puede ser mayor a 2100")
    @Column(nullable = false)
    private Integer anio;

    @NotBlank(message = "La matrícula es obligatoria")
    @Size(max = 20, message = "La matrícula admite máximo 20 caracteres")
    @Column(nullable = false, unique = true, length = 20)
    private String matricula;

    @NotNull(message = "La tarifa diaria es obligatoria")
    @DecimalMin(value = "0.01", message = "La tarifa diaria debe ser mayor a 0")
    @Column(name = "tarifa_diaria", nullable = false, precision = 10, scale = 2)
    private BigDecimal tarifaDiaria;

    @NotBlank(message = "El estado es obligatorio")
    @Column(nullable = false, length = 20)
    private String estado;

    @NotNull(message = "Selecciona una oficina")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_oficina", nullable = false)
    @JsonIgnoreProperties("vehiculos")
    private OficinaModel oficina;

    @OneToMany(mappedBy = "vehiculo", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("vehiculo") 
    private List<AlquilerModel> alquileres;

    @OneToMany(mappedBy = "vehiculo", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("vehiculo")
    private List<MantenimientoModel> mantenimientos;

    public VehiculoModel() {
    }

    public VehiculoModel(String marca, String modelo, Integer anio, String matricula, BigDecimal tarifaDiaria, String estado, OficinaModel oficina) {
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.matricula = matricula;
        this.tarifaDiaria = tarifaDiaria;
        this.estado = estado;
        this.oficina = oficina;
    }

    // Getters y Setters
    public Long getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(Long idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public BigDecimal getTarifaDiaria() {
        return tarifaDiaria;
    }

    public void setTarifaDiaria(BigDecimal tarifaDiaria) {
        this.tarifaDiaria = tarifaDiaria;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<AlquilerModel> getAlquileres() {
        return alquileres;
    }

    public void setAlquileres(List<AlquilerModel> alquileres) {
        this.alquileres = alquileres;
    }

    public OficinaModel getOficina() {
        return oficina;
    }

    public void setOficina(OficinaModel oficina) {
        this.oficina = oficina;
    }

    public List<MantenimientoModel> getMantenimientos() {
        return mantenimientos;
    }

    public void setMantenimientos(List<MantenimientoModel> mantenimientos) {
        this.mantenimientos = mantenimientos;
    }
}
