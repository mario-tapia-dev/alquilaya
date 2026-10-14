package com.api.alquilaya.services;

import com.api.alquilaya.exceptions.ReglaNegocioException;
import com.api.alquilaya.models.ClienteModel;
import com.api.alquilaya.repositories.AlquilerRepository;
import com.api.alquilaya.repositories.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final AlquilerRepository alquilerRepository;

    public ClienteService(ClienteRepository clienteRepository, AlquilerRepository alquilerRepository) {
        this.clienteRepository = clienteRepository;
        this.alquilerRepository = alquilerRepository;
    }

    public List<ClienteModel> obtenerTodos() {
        return clienteRepository.findAll();
    }

    public ClienteModel obtenerPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Cliente no encontrado con id: " + id));
    }

    public ClienteModel crear(ClienteModel cliente) {
        validarUnicos(cliente, null);
        cliente.setIdCliente(null);
        cliente.setAlquileres(null);
        return clienteRepository.save(cliente);
    }

    public ClienteModel actualizar(Long id, ClienteModel cliente) {
        ClienteModel existente = obtenerPorId(id);
        validarUnicos(cliente, id);
        existente.setNombre(cliente.getNombre());
        existente.setApellido(cliente.getApellido());
        existente.setEmail(cliente.getEmail());
        existente.setLicenciaConducir(cliente.getLicenciaConducir());
        existente.setTelefono(cliente.getTelefono());
        return clienteRepository.save(existente);
    }

    public void eliminar(Long id) {
        ClienteModel existente = obtenerPorId(id);
        if (alquilerRepository.existsByCliente_IdCliente(id)) {
            throw new ReglaNegocioException("No se puede eliminar el cliente porque tiene alquileres registrados");
        }
        clienteRepository.delete(existente);
    }

    // El correo y la licencia deben ser unicos; idActual es el cliente que se esta editando (null al crear)
    private void validarUnicos(ClienteModel cliente, Long idActual) {
        clienteRepository.findByEmail(cliente.getEmail())
                .filter(otro -> !otro.getIdCliente().equals(idActual))
                .ifPresent(otro -> {
                    throw new ReglaNegocioException("Ya existe un cliente con ese correo");
                });
        clienteRepository.findByLicenciaConducir(cliente.getLicenciaConducir())
                .filter(otro -> !otro.getIdCliente().equals(idActual))
                .ifPresent(otro -> {
                    throw new ReglaNegocioException("Ya existe un cliente con esa licencia de conducir");
                });
    }
}
