package itch.tecnm.proyecto.feign;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@FeignClient(
	    name = "reservaciones",
	    contextId = "reservaFeign",
	    configuration = itch.tecnm.proyecto.config.FeignClientConfig.class
	)
public interface ReservaFeign {

    @GetMapping("/api/reserva/{id}")
    ReservaDto getReservaById(@PathVariable("id") Integer idReserva);

    @JsonIgnoreProperties(ignoreUnknown = true)
    class ReservaDto {

        private Integer idReserva;
        private LocalDate fecha;
        private LocalTime hora;
        private String estado;

        @JsonAlias({"idMesa"})
        private Integer idMesa;

        private Integer idCliente;

        public Integer getIdReserva() { return idReserva; }
        public void setIdReserva(Integer idReserva) { this.idReserva = idReserva; }

        public LocalDate getFecha() { return fecha; }
        public void setFecha(LocalDate fecha) { this.fecha = fecha; }

        public LocalTime getHora() { return hora; }
        public void setHora(LocalTime hora) { this.hora = hora; }

        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }

        public Integer getIdMesa() { return idMesa; }
        public void setIdMesa(Integer idMesa) { this.idMesa = idMesa; }

        public Integer getIdCliente() { return idCliente; }
        public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }
    }
}


