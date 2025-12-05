package itch.tecnm.proyecto.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@FeignClient(
	    name = "428b7cb710e9",
	    contextId = "atenderFeign",
	    configuration = itch.tecnm.proyecto.config.FeignClientConfig.class
	)
public interface AtenderFeign {

    @GetMapping("/api/atender/venta/{idVenta}")
    AtenderDto getAtenderByVenta(@PathVariable("idVenta") Integer idVenta);

    // DTO anidado + tolerante a cambios en JSON
    @JsonIgnoreProperties(ignoreUnknown = true)
    class AtenderDto {
        private Integer idAtender;
        private Integer idEmpleado;
        private Integer idVenta;

        public Integer getIdAtender() { return idAtender; }
        public void setIdAtender(Integer idAtender) { this.idAtender = idAtender; }

        public Integer getIdEmpleado() { return idEmpleado; }
        public void setIdEmpleado(Integer idEmpleado) { this.idEmpleado = idEmpleado; }

        public Integer getIdVenta() { return idVenta; }
        public void setIdVenta(Integer idVenta) { this.idVenta = idVenta; }
    }
}