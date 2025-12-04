package itch.tecnm.proyecto.service;

import java.time.LocalDate;
import java.util.List;

import itch.tecnm.proyecto.dto.VentaDto;

public interface VentaService {
	// Crea la venta con su lista de detalles; calcula total y asigna fechaVenta si viene null
    VentaDto createVenta(VentaDto ventaDto);

    // Consulta por id
    VentaDto getVentaById(Integer ventaId);

    // Lista todas
    List<VentaDto> getAllVentas();

    // Actualiza la venta (incluyendo reemplazar/ajustar detalles)
    VentaDto updateVenta(Integer ventaId, VentaDto updateVenta);

    // Elimina la venta (y sus detalles por cascade)
    void deleteVenta(Integer ventaId);
    
    List<VentaDto> getVentasByFecha(LocalDate fecha);

}
