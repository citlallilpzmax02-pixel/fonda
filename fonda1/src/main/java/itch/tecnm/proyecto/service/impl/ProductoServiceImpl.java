package itch.tecnm.proyecto.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import itch.tecnm.proyecto.dto.ProductoDto;
import itch.tecnm.proyecto.entity.Producto;
import itch.tecnm.proyecto.mapper.ProductoMapper;
import itch.tecnm.proyecto.mapper.TipoMapper;
import itch.tecnm.proyecto.repository.ProductoRepository;
import itch.tecnm.proyecto.service.ProductoService;
import lombok.AllArgsConstructor;

@Service
//Utilizando lombok para que ocupe todos los argumentos como constructor
@AllArgsConstructor
public class ProductoServiceImpl implements ProductoService{
private ProductoRepository productoRepository;
	
	@Override
	public ProductoDto createProducto(ProductoDto productoDto) {
		Producto producto = ProductoMapper.mapToProducto(productoDto);
		producto.setEstado(true);
		Producto savedProducto = productoRepository.save(producto);
		return ProductoMapper.mapToProductoDto(savedProducto);
	}

	@Override
	public ProductoDto getProductoById(Integer productoId) {
		Producto producto = productoRepository.findById(productoId).orElse(null);
		return ProductoMapper.mapToProductoDto(producto);
	}

	@Override
	public List<ProductoDto> getAllProductos() {
		List<Producto> productos = productoRepository.findAll();
		
		return productos.stream().map((producto) -> ProductoMapper.mapToProductoDto(producto))
				.collect(Collectors.toList());
	}

	@Override
	public ProductoDto updateProducto(Integer productoId, ProductoDto updateProducto) {
		//Busca el registro a modificar
		Producto producto = productoRepository.findById(productoId).orElse(null);
		//Modificar los atributos
		producto.setNombreProducto(updateProducto.getNombreProducto());
		producto.setDescripcionProducto(updateProducto.getDescripcionProducto());
		producto.setPrecioProducto(updateProducto.getPrecioProducto());
		producto.setTipo(TipoMapper.mapToTipo(updateProducto.getTipo()));
		
		if (updateProducto.getImagen() != null) {
            producto.setImagen(updateProducto.getImagen());
        }
		//Guardar cambios
		Producto updateProductoObj = productoRepository.save(producto);
		
		return ProductoMapper.mapToProductoDto(updateProductoObj);
	}
/*//Metodo para eliminar el registro
	@Override
	public void deleteProducto(Integer productoId) {
		//Buscar registro que se desea eliminar
		Producto producto = productoRepository.findById(productoId).orElse(null);
		productoRepository.deleteById(productoId);
	} */
	
	@Override
	public void deleteProducto(Integer productoId) {
	    // Buscar registro que se desea "eliminar"
	    Producto producto = productoRepository.findById(productoId)
	            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
	    // Cambiar su estado a false en lugar de borrarlo
	    producto.setEstado(false);
	    // Guardar el cambio en la base de datos
	    productoRepository.save(producto);
	}
	
	@Override
	public List<ProductoDto> buscarPorNombre(String nombre) {
	    return productoRepository.findByNombreProductoContainingIgnoreCase(nombre)
	            .stream()
	            .filter(Producto::getEstado)
	            .map(ProductoMapper::mapToProductoDto)
	            .collect(Collectors.toList());
	}

	@Override
	public List<ProductoDto> buscarPorTipo(Integer idTipo) {
	    return productoRepository.findByTipo_Id_tipo(idTipo)
	            .stream()
	            .filter(Producto::getEstado)
	            .map(ProductoMapper::mapToProductoDto)
	            .collect(Collectors.toList());
	}

	@Override
	public List<ProductoDto> buscarPorRangoPrecio(Double precioMin, Double precioMax) {
	    return productoRepository.findByPrecioProductoBetween(precioMin, precioMax)
	            .stream()
	            .filter(Producto::getEstado)
	            .map(ProductoMapper::mapToProductoDto)
	            .collect(Collectors.toList());
	}


}
