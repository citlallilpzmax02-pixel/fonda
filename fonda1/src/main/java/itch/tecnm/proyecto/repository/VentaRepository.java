package itch.tecnm.proyecto.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import itch.tecnm.proyecto.entity.Venta;

public interface VentaRepository extends JpaRepository<Venta,Integer>{
	// Buscar todas las ventas dentro de un rango de fecha y hora
    List<Venta> findByFechaVentaBetween(LocalDateTime inicio, LocalDateTime fin);
}
