package itch.tecnm.proyecto.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import itch.tecnm.proyecto.dto.DetalleVentaDto;
import itch.tecnm.proyecto.dto.VentaDto;
import itch.tecnm.proyecto.entity.DetalleVenta;
import itch.tecnm.proyecto.entity.Venta;

public class VentaMapper {
	// Entidad -> DTO
    public static VentaDto mapToVentaDto(Venta venta) {
        if (venta == null) return null;

        List<DetalleVentaDto> detallesDto = (venta.getDetalles() == null)
                ? null
                : venta.getDetalles().stream()
                        .map(DetalleVentaMapper::mapToDetalleVentaDto)
                        .collect(Collectors.toList());

        return new VentaDto(
                venta.getIdVenta(),
                venta.getFechaVenta(),   // read-only hacia afuera
                venta.getIdCliente(),
                venta.getIdReserva(),    // <-- NUEVO
                venta.getTotal(),        // read-only hacia afuera
                detallesDto
        );
    }

    // DTO -> Entidad
    public static Venta mapToVenta(VentaDto dto) {
        if (dto == null) return null;

        Venta v = new Venta();
        v.setIdVenta(dto.getIdVenta());
        // NO copiar fechaVenta (la pone @PrePersist/@PreUpdate)
        v.setIdCliente(dto.getIdCliente());
        v.setIdReserva(dto.getIdReserva());   // <-- NUEVO
        // NO copiar total (lo calcula el servicio)

        List<DetalleVenta> detalles = (dto.getDetalles() == null)
                ? new ArrayList<>()
                : dto.getDetalles().stream()
                        .map(DetalleVentaMapper::mapToDetalleVenta)
                        .collect(Collectors.toList());

        // Enlazar el lado dueño en cada detalle
        for (DetalleVenta d : detalles) {
            d.setVenta(v);
        }
        v.setDetalles(detalles);

        return v;
    }
}
