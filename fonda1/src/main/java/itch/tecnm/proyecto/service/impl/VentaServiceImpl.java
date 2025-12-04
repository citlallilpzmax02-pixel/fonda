package itch.tecnm.proyecto.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import feign.FeignException;
//import itch.tecnm.proyecto.client.ClienteClient;
import itch.tecnm.proyecto.dto.DetalleVentaDto;
import itch.tecnm.proyecto.dto.VentaDto;
import itch.tecnm.proyecto.entity.DetalleVenta;
import itch.tecnm.proyecto.entity.Producto;
import itch.tecnm.proyecto.entity.Venta;
import itch.tecnm.proyecto.mapper.VentaMapper;
import itch.tecnm.proyecto.repository.ProductoRepository;
import itch.tecnm.proyecto.repository.VentaRepository;
import itch.tecnm.proyecto.service.VentaService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VentaServiceImpl implements VentaService {
	
	private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    
    @Override
    @Transactional
    public VentaDto createVenta(VentaDto ventaDto) {
        // Validaciones básicas
        if (ventaDto.getIdCliente() == null) {
            throw new IllegalArgumentException("El idCliente es obligatorio");
        }
        if (ventaDto.getDetalles() == null || ventaDto.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La venta debe tener al menos un detalle");
        }

        // 1️⃣ Mapear DTO a entidad
        Venta venta = VentaMapper.mapToVenta(ventaDto);

        double total = 0.0;

        // 2️⃣ Procesar cada detalle
        for (DetalleVenta det : venta.getDetalles()) {
            det.setVenta(venta); // vincular detalle con venta

            // Validar cantidad
            if (det.getCantidad() == null || det.getCantidad() <= 0) {
                det.setCantidad(1);
            }

            // Validar producto
            Integer idProd = (det.getProducto() != null) ? det.getProducto().getId_producto() : null;
            if (idProd == null) {
                throw new IllegalArgumentException("Cada detalle debe incluir un idProducto válido");
            }

            // Obtener precio si no se envía desde el frontend
            if (det.getPrecioUnitario() == null || det.getPrecioUnitario() <= 0) {
                Producto p = productoRepository.findById(idProd)
                        .orElseThrow(() -> new IllegalArgumentException("Producto no existe: " + idProd));
                det.setPrecioUnitario(p.getPrecioProducto());
            }

            // Calcular subtotal
            total += det.getCantidad() * det.getPrecioUnitario();
        }

        // 3️⃣ Calcular total y guardar
        venta.setTotal(total);
        Venta guardada = ventaRepository.save(venta);

        // 4️⃣ Retornar DTO
        return VentaMapper.mapToVentaDto(guardada);
    }

    @Override
    @Transactional
    public VentaDto updateVenta(Integer ventaId, VentaDto updateVenta) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));

        // 1️⃣ Actualizar cliente si cambió
        if (updateVenta.getIdCliente() != null && !updateVenta.getIdCliente().equals(venta.getIdCliente())) {
            venta.setIdCliente(updateVenta.getIdCliente());
        }

        // 2️⃣ Actualizar detalles (si vienen en el DTO)
        Map<Integer, DetalleVenta> existentes = new HashMap<>();
        if (venta.getDetalles() != null) {
            for (DetalleVenta d : venta.getDetalles()) {
                existentes.put(d.getIdDetalle(), d);
            }
        }

        double total = 0.0;

        if (updateVenta.getDetalles() != null && !updateVenta.getDetalles().isEmpty()) {
            for (DetalleVentaDto dDto : updateVenta.getDetalles()) {
                Integer idProd = dDto.getIdProducto();
                if (idProd == null) {
                    throw new IllegalArgumentException("idProducto es obligatorio en cada detalle");
                }

                Producto prod = productoRepository.findById(idProd)
                        .orElseThrow(() -> new IllegalArgumentException("Producto no existe: " + idProd));

                int cantidad = (dDto.getCantidad() == null || dDto.getCantidad() <= 0) ? 1 : dDto.getCantidad();
                Double precio = (dDto.getPrecioUnitario() == null || dDto.getPrecioUnitario() <= 0)
                        ? prod.getPrecioProducto()
                        : dDto.getPrecioUnitario();

                // Si existe, actualiza; si no, crea nuevo
                if (dDto.getIdDetalle() != null) {
                    DetalleVenta existente = existentes.remove(dDto.getIdDetalle());
                    if (existente == null) {
                        throw new IllegalArgumentException("El detalle " + dDto.getIdDetalle() + " no existe en esta venta");
                    }
                    existente.setProducto(prod);
                    existente.setCantidad(cantidad);
                    existente.setPrecioUnitario(precio);
                } else {
                    DetalleVenta nuevo = new DetalleVenta();
                    nuevo.setVenta(venta);
                    nuevo.setProducto(prod);
                    nuevo.setCantidad(cantidad);
                    nuevo.setPrecioUnitario(precio);
                    venta.getDetalles().add(nuevo);
                }

                total += cantidad * precio;
            }

            // Eliminar los detalles que no vinieron
            for (DetalleVenta eliminar : existentes.values()) {
                venta.getDetalles().remove(eliminar);
            }

        } else {
            // Si no mandaron detalles nuevos, recalcular total
            total = venta.getDetalles().stream()
                    .mapToDouble(d -> d.getCantidad() * d.getPrecioUnitario())
                    .sum();
        }

        // 3️⃣ Actualizar total
        venta.setTotal(total);

        // 4️⃣ Guardar y retornar
        Venta guardada = ventaRepository.save(venta);
        return VentaMapper.mapToVentaDto(guardada);
    }

    
    //private final ClienteClient clienteClient;
    /* Metodos utilizando el clienteclient cuando se conecta al servidor de eureka
    @Override
    public VentaDto createVenta(VentaDto ventaDto) {
        if (ventaDto.getIdCliente() == null) {
            throw new IllegalArgumentException("idCliente es obligatorio");
        }

        // 1) Validar cliente con Restaurante
        try {
            clienteClient.findById(ventaDto.getIdCliente());
        } catch (FeignException.NotFound nf) {
            throw new IllegalArgumentException("Cliente no existe: " + ventaDto.getIdCliente());
        } catch (FeignException fe) {
            throw new IllegalStateException("Error al consultar Restaurante: " + fe.getMessage());
        }

        // 2) Mapear y procesar detalles
        Venta venta = VentaMapper.mapToVenta(ventaDto);
        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La venta debe tener al menos un detalle");
        }

        double total = 0.0;
        for (DetalleVenta det : venta.getDetalles()) {
            det.setVenta(venta);

            if (det.getCantidad() == null || det.getCantidad() <= 0) det.setCantidad(1);

            Integer idProd = (det.getProducto() != null) ? det.getProducto().getId_producto() : null;
            if (idProd == null) throw new IllegalArgumentException("idProducto es obligatorio en cada detalle");

            if (det.getPrecioUnitario() == null || det.getPrecioUnitario() <= 0) {
                Producto p = productoRepository.findById(idProd)
                        .orElseThrow(() -> new IllegalArgumentException("Producto no existe: " + idProd));
                det.setPrecioUnitario(p.getPrecioProducto());
            }

            total += det.getCantidad() * det.getPrecioUnitario();
        }

        venta.setTotal(total); // fechaVenta la sella @PrePersist
        Venta guardada = ventaRepository.save(venta);
        return VentaMapper.mapToVentaDto(guardada);
    }

    @Override
    public VentaDto updateVenta(Integer ventaId, VentaDto updateVenta) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));

        // 1) Cambiar cliente (si viene y cambió)
        if (updateVenta.getIdCliente() != null && !updateVenta.getIdCliente().equals(venta.getIdCliente())) {
            try {
                clienteClient.findById(updateVenta.getIdCliente());
            } catch (FeignException.NotFound nf) {
                throw new IllegalArgumentException("Cliente no existe: " + updateVenta.getIdCliente());
            } catch (FeignException fe) {
                throw new IllegalStateException("Error al consultar Restaurante: " + fe.getMessage());
            }
            venta.setIdCliente(updateVenta.getIdCliente());
        }

        if (venta.getDetalles() == null) {
            venta.setDetalles(new ArrayList<>());
        }

        // 2) Indexar detalles existentes por id
        Map<Integer, DetalleVenta> existentes = new HashMap<>();
        for (DetalleVenta d : venta.getDetalles()) {
            // Ajusta el nombre del getter según tu entidad: getIdDetalle() vs getIdDetalleVenta()
            existentes.put(d.getIdDetalle(), d);
        }

        double total;

        if (updateVenta.getDetalles() != null) {
            total = 0.0;

            for (DetalleVentaDto dDto : updateVenta.getDetalles()) {
                // a) Producto
                Integer idProd = dDto.getIdProducto();
                if (idProd == null) throw new IllegalArgumentException("idProducto es obligatorio en cada detalle");

                Producto prod = productoRepository.findById(idProd)
                        .orElseThrow(() -> new IllegalArgumentException("Producto no existe: " + idProd));

                // b) Cantidad y precio
                int cantidad = (dDto.getCantidad() == null || dDto.getCantidad() <= 0) ? 1 : dDto.getCantidad();
                Double precio = dDto.getPrecioUnitario();
                if (precio == null || precio <= 0) precio = prod.getPrecioProducto();
                if (precio == null || precio <= 0) throw new IllegalArgumentException("precioUnitario inválido");

                // c) Actualizar o crear
                if (dDto.getIdDetalle() != null) { // ajusta el nombre del campo si es idDetalleVenta
                    DetalleVenta existente = existentes.remove(dDto.getIdDetalle());
                    if (existente == null) {
                        throw new IllegalArgumentException("El detalle " + dDto.getIdDetalle() + " no existe en esta venta");
                    }
                    existente.setProducto(prod);
                    existente.setCantidad(cantidad);
                    existente.setPrecioUnitario(precio);
                } else {
                    DetalleVenta nuevo = new DetalleVenta();
                    nuevo.setVenta(venta);
                    nuevo.setProducto(prod);
                    nuevo.setCantidad(cantidad);
                    nuevo.setPrecioUnitario(precio);
                    venta.getDetalles().add(nuevo);
                }

                total += cantidad * precio;
            }

            // 3) Eliminar los que ya no vinieron en el DTO
            for (DetalleVenta aEliminar : existentes.values()) {
                venta.getDetalles().remove(aEliminar); // orphanRemoval los borra en DB
            }

        } else {
            // 🔴 FIX: Si no mandan detalles en el update, **no poner total=0**.
            // Recalcular desde los detalles que ya tiene la venta.
            total = venta.getDetalles().stream()
                    .mapToDouble(d -> d.getCantidad() * d.getPrecioUnitario())
                    .sum();
        }

        // 4) Total final
        venta.setTotal(total);

        Venta guardada = ventaRepository.save(venta);
        return VentaMapper.mapToVentaDto(guardada);
    }
*/
    @Override
    @Transactional(readOnly = true)
    public VentaDto getVentaById(Integer ventaId) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));
        return VentaMapper.mapToVentaDto(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaDto> getAllVentas() {
        return ventaRepository.findAll().stream()
                .map(VentaMapper::mapToVentaDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteVenta(Integer ventaId) {
        if (!ventaRepository.existsById(ventaId)) {
            throw new IllegalArgumentException("Venta no encontrada");
        }
        ventaRepository.deleteById(ventaId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<VentaDto> getVentasByFecha(LocalDate fecha) {
        // Rango completo del día (00:00 a 23:59:59)
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.atTime(23, 59, 59);

        return ventaRepository.findByFechaVentaBetween(inicio, fin)
                .stream()
                .map(VentaMapper::mapToVentaDto)
                .collect(Collectors.toList());
    }



}

