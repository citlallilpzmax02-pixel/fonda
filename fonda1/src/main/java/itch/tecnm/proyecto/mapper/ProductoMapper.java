package itch.tecnm.proyecto.mapper;

import itch.tecnm.proyecto.dto.ProductoDto;
import itch.tecnm.proyecto.entity.Producto;

public class ProductoMapper {
	//Conexion entre las entdades y el DTO
		public static ProductoDto mapToProductoDto (Producto producto){
			return new ProductoDto(
					producto.getId_producto(),
					producto.getNombreProducto(),
					producto.getDescripcionProducto(),
					producto.getPrecioProducto(),
					producto.getEstado(),
					producto.getImagen(),
					TipoMapper.mapToTipoDto(producto.getTipo())
					);
					
		}
		
		public static Producto mapToProducto(ProductoDto productoDto) {
			return new Producto(
					productoDto.getId_producto(),
					productoDto.getNombreProducto(),
					productoDto.getDescripcionProducto(),
					productoDto.getPrecioProducto(),
					productoDto.getEstado(),
					productoDto.getImagen(),
					TipoMapper.mapToTipo(productoDto.getTipo())
					);
		}
}
