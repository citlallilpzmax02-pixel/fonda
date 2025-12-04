package itch.tecnm.proyecto.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import itch.tecnm.proyecto.dto.ProductoDto;
import itch.tecnm.proyecto.dto.TipoDto;
import itch.tecnm.proyecto.service.ProductoService;
import lombok.AllArgsConstructor;
//Para conectar con react
@CrossOrigin("*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/producto")
public class ProductoController {
	//Inyección de dependencia
		private ProductoService productoService;
		
		@Autowired
		private Cloudinary cloudinary;

		
		//Construir el REST API para agregar un producto
		/*@PostMapping
		public ResponseEntity<ProductoDto> crearProducto(@RequestBody ProductoDto productoDto){
			ProductoDto guardarProducto = productoService.createProducto(productoDto);
			//Respose entity permite utilicar información a traves de http
			return new ResponseEntity<>(guardarProducto, HttpStatus.CREATED);
		}*/
		@PostMapping
		public ResponseEntity<?> crearProducto(
		        @RequestParam("nombreProducto") String nombreProducto,
		        @RequestParam("descripcionProducto") String descripcionProducto,
		        @RequestParam("precioProducto") double precioProducto,
		        @RequestParam("estado") Boolean estado,
		        @RequestParam("tipo") Integer idTipo, // ✅ AGREGADO
		        @RequestParam(value = "file", required = false) MultipartFile file) {

		    try {
		        ProductoDto productoDto = new ProductoDto();
		        productoDto.setNombreProducto(nombreProducto);
		        productoDto.setDescripcionProducto(descripcionProducto);
		        productoDto.setPrecioProducto(precioProducto);
		        productoDto.setEstado(estado);

		        // ✅ Asignar tipo correctamente
		        TipoDto tipoDto = new TipoDto();
		        tipoDto.setId_tipo(idTipo);
		        productoDto.setTipo(tipoDto);

		        // ✅ Si hay imagen, subirla a Cloudinary
		        if (file != null && !file.isEmpty()) {
		            if (!file.getContentType().startsWith("image/")) {
		                return ResponseEntity.badRequest().body("Solo se permiten archivos de imagen");
		            }

		            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
		            String urlImagen = (String) uploadResult.get("secure_url");
		            productoDto.setImagen(urlImagen);
		        }

		        ProductoDto guardarProducto = productoService.createProducto(productoDto);
		        return new ResponseEntity<>(guardarProducto, HttpStatus.CREATED);

		    } catch (Exception e) {
		        e.printStackTrace();
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                .body("Error al crear el producto: " + e.getMessage());
		    }
		}


		
		//Construir el get del producto REST API
				@GetMapping("{id}")
				public ResponseEntity<ProductoDto> getProductoById(@PathVariable("id") Integer productoId){
					
					ProductoDto productoDto = productoService.getProductoById(productoId);
					return ResponseEntity.ok(productoDto);
				}

			//REST API. Construir el get para todos los productos
			@GetMapping 
			public ResponseEntity<List<ProductoDto>> getAllProductos(){
				List<ProductoDto> productos = productoService.getAllProductos();
				return ResponseEntity.ok(productos);
			}
			
			// Construir REST API Update Producto.
				/*// Exponer un endpoint HTTP PUT para actualizar un producto
				@PutMapping("{id}")
				public ResponseEntity<ProductoDto> updateProducto(@PathVariable("id") Integer id,
						@RequestBody ProductoDto updateProducto) {
					ProductoDto productoDto = productoService.updateProducto(id, updateProducto);
					return ResponseEntity.ok(productoDto);
				}*/
			@PutMapping("{id}")
			public ResponseEntity<?> updateProducto(
			        @PathVariable("id") Integer id,
			        @RequestParam("nombreProducto") String nombreProducto,
			        @RequestParam("descripcionProducto") String descripcionProducto,
			        @RequestParam("precioProducto") double precioProducto,
			        @RequestParam("estado") Boolean estado,
			        @RequestParam("tipo") Integer idTipo, // ✅ Agregado igual que en el POST
			        @RequestParam(value = "file", required = false) MultipartFile file) {

			    try {
			        ProductoDto productoDto = new ProductoDto();
			        productoDto.setNombreProducto(nombreProducto);
			        productoDto.setDescripcionProducto(descripcionProducto);
			        productoDto.setPrecioProducto(precioProducto);
			        productoDto.setEstado(estado);

			        // ✅ Asignar tipo correctamente
			        TipoDto tipoDto = new TipoDto();
			        tipoDto.setId_tipo(idTipo);
			        productoDto.setTipo(tipoDto);

			        // ✅ Si se manda una imagen nueva, súbela a Cloudinary
			        if (file != null && !file.isEmpty()) {
			            if (!file.getContentType().startsWith("image/")) {
			                return ResponseEntity.badRequest().body("Solo se permiten archivos de imagen");
			            }

			            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
			            String urlImagen = (String) uploadResult.get("secure_url");
			            productoDto.setImagen(urlImagen);
			        }

			        ProductoDto actualizado = productoService.updateProducto(id, productoDto);
			        return ResponseEntity.ok(actualizado);

			    } catch (Exception e) {
			        e.printStackTrace();
			        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			                .body("Error al actualizar el producto: " + e.getMessage());
			    }
			}


			
			//Construir delete producto REST API
				@DeleteMapping("{id}")
				public ResponseEntity<String> deleteProducto(@PathVariable("id") Integer productoId){
					productoService.deleteProducto(productoId);
					return ResponseEntity.ok("Registro de producto eliminado");
				}
				
				
				// 🔍 Buscar por nombre
				@GetMapping("/buscar/nombre/{nombre}")
				public ResponseEntity<List<ProductoDto>> buscarPorNombre(@PathVariable("nombre") String nombre) {
				    return ResponseEntity.ok(productoService.buscarPorNombre(nombre));
				}

				// 🔍 Buscar por tipo
				@GetMapping("/buscar/tipo/{idTipo}")
				public ResponseEntity<List<ProductoDto>> buscarPorTipo(@PathVariable("idTipo") Integer idTipo) {
				    return ResponseEntity.ok(productoService.buscarPorTipo(idTipo));
				}

				// 🔍 Buscar por rango de precios
				@GetMapping("/buscar/precio")
				public ResponseEntity<List<ProductoDto>> buscarPorRangoPrecio(
				        @RequestParam("min") Double precioMin,
				        @RequestParam("max") Double precioMax) {
				    return ResponseEntity.ok(productoService.buscarPorRangoPrecio(precioMin, precioMax));
				}

}
