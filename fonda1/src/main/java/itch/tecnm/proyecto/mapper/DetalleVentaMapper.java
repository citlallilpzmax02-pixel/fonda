package itch.tecnm.proyecto.mapper;

import itch.tecnm.proyecto.dto.DetalleVentaDto;
import itch.tecnm.proyecto.entity.DetalleVenta;
import itch.tecnm.proyecto.entity.Producto;

public class DetalleVentaMapper {
	public static DetalleVentaDto mapToDetalleVentaDto(DetalleVenta dv) {
        if (dv == null) return null;

        Integer idProducto = (dv.getProducto() != null)
                ? dv.getProducto().getId_producto()
                : null;

        return new DetalleVentaDto(
                dv.getIdDetalle(),
                idProducto,
                dv.getCantidad(),
                dv.getPrecioUnitario()
        );
    }

    public static DetalleVenta mapToDetalleVenta(DetalleVentaDto dto) {
        if (dto == null) return null;

        // Producto "placeholder" con solo el ID (no cargamos toda la entidad)
        Producto p = null;
        if (dto.getIdProducto() != null) {
            p = new Producto();
            p.setId_producto(dto.getIdProducto());
        }

        // venta se setea después (en VentaMapper o en el Service)
        return new DetalleVenta(
                dto.getIdDetalle(),
                null,
                p,
                dto.getCantidad(),
                dto.getPrecioUnitario()
        );
    }
}
