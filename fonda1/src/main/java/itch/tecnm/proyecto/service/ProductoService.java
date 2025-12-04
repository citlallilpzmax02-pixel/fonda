package itch.tecnm.proyecto.service;

import java.util.List;

import itch.tecnm.proyecto.dto.ProductoDto;

public interface ProductoService {
	//Agregar un producto
		ProductoDto createProducto (ProductoDto productoDto);
		
		//Buscar un producto por id
		ProductoDto getProductoById(Integer productoId);
		
		//Obtener todos los datos de los productos
		List<ProductoDto> getAllProductos();
		
		//Construir el REST API para modificar
		ProductoDto updateProducto(Integer productoId, ProductoDto updateProducto);
		
		//Contruir el DELETE REST API de Productos
		void deleteProducto(Integer productoId);
		
		List<ProductoDto> buscarPorNombre(String nombre);
		List<ProductoDto> buscarPorTipo(Integer idTipo);
		List<ProductoDto> buscarPorRangoPrecio(Double precioMin, Double precioMax);

}
