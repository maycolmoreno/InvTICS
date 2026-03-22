package com.uisrael.consumogestionactivosapi.service;

import java.util.List;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.TicketRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.TicketResponseDTO;

public interface ITicketsServicio {
    List<TicketResponseDTO> listar(String estado, Integer idEquipo, String odooTicketId);
    TicketResponseDTO crear(TicketRequestDTO dto);
    TicketResponseDTO obtenerPorId(Integer idTicket);
    TicketResponseDTO asignar(Integer idTicket, Integer idTecnico);
    TicketResponseDTO cerrarYCrearMantenimiento(Integer idTicket);
}
