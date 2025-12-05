package itch.tecnm.proyecto.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@FeignClient(
	    name = "428b7cb710e9",
	    contextId = "mesaFeign",
	    configuration = itch.tecnm.proyecto.config.FeignClientConfig.class
	)
public interface MesaFeign {

    @GetMapping("/api/mesa/{id}")
    MesaDto getMesaById(@PathVariable("id") Integer idMesa);

    // =============================
    //   DTO ANIDADO PARA FEIGN
    // =============================
    @JsonIgnoreProperties(ignoreUnknown = true)
    class MesaDto {

        private Integer idMesa;
        private Integer numero;
        private Integer capacidad;
        private String ubicacion;
        private Boolean estado;

        public MesaDto() {}

        public Integer getIdMesa() { return idMesa; }
        public void setIdMesa(Integer idMesa) { this.idMesa = idMesa; }

        public Integer getNumero() { return numero; }
        public void setNumero(Integer numero) { this.numero = numero; }

        public Integer getCapacidad() { return capacidad; }
        public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }

        public String getUbicacion() { return ubicacion; }
        public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

        public Boolean getEstado() { return estado; }
        public void setEstado(Boolean estado) { this.estado = estado; }
    }
}
