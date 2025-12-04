package itch.tecnm.proyecto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import feign.Param;
import itch.tecnm.proyecto.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Integer>{
	// 🔍 Buscar productos por nombre (insensible a mayúsculas)
    List<Producto> findByNombreProductoContainingIgnoreCase(String nombreProducto);

    @Query("SELECT p FROM Producto p WHERE p.tipo.id_tipo = :idTipo")
    List<Producto> findByTipo_Id_tipo(@Param("idTipo") Integer idTipo);


    // 🔍 Buscar productos en un rango de precio
    List<Producto> findByPrecioProductoBetween(Double precioMin, Double precioMax);
}
