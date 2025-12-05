package itch.tecnm.proyecto.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@FeignClient(
	    name = "428b7cb710e9",
	    contextId = "empleadoFeign",
	    configuration = itch.tecnm.proyecto.config.FeignClientConfig.class
	)
public interface EmpleadoFeign {

    @GetMapping("/api/empleado/{id}")
    EmpleadoDto getEmpleadoById(@PathVariable("id") Integer idEmpleado);

    // =============================
    //   DTO ANIDADO PARA FEIGN
    // =============================
    @JsonIgnoreProperties(ignoreUnknown = true)
    class EmpleadoDto {

        private Integer idEmpleado;
        private String nombre;
        private String puesto;

        public EmpleadoDto() {}

        public Integer getIdEmpleado() { return idEmpleado; }
        public void setIdEmpleado(Integer idEmpleado) { this.idEmpleado = idEmpleado; }

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public String getPuesto() { return puesto; }
        public void setPuesto(String puesto) { this.puesto = puesto; }
    }
}