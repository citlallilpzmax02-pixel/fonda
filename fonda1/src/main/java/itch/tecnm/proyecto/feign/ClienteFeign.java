package itch.tecnm.proyecto.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@FeignClient(
	    name = "f93ee95b8a53",
	    contextId = "clienteFeign",
	    configuration = itch.tecnm.proyecto.config.FeignClientConfig.class
	)

public interface ClienteFeign {

    @GetMapping("https://restaurante-production-1b90.up.railway.app/api/cliente/{id}")
    ClienteDto getClienteById(@PathVariable("id") Integer idCliente);

    // =============================
    //     DTO ANIDADO PARA FEIGN
    // =============================
    @JsonIgnoreProperties(ignoreUnknown = true)
    class ClienteDto {

        @JsonAlias({"id_cliente", "idCliente"})
        private Integer idCliente;

        private String nombreCliente;
        private String telefonoCliente;
        private String correoCliente;

        public ClienteDto() {}

        public Integer getIdCliente() { return idCliente; }
        public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }

        public String getNombreCliente() { return nombreCliente; }
        public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

        public String getTelefonoCliente() { return telefonoCliente; }
        public void setTelefonoCliente(String telefonoCliente) { this.telefonoCliente = telefonoCliente; }

        public String getCorreoCliente() { return correoCliente; }
        public void setCorreoCliente(String correoCliente) { this.correoCliente = correoCliente; }
    }
}