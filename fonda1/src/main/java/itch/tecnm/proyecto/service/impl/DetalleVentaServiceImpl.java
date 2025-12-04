package itch.tecnm.proyecto.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import itch.tecnm.proyecto.dto.DetalleVentaDto;
import itch.tecnm.proyecto.entity.DetalleVenta;
import itch.tecnm.proyecto.entity.Venta;
import itch.tecnm.proyecto.feign.AtenderFeign;
import itch.tecnm.proyecto.feign.ClienteFeign;
import itch.tecnm.proyecto.feign.EmpleadoFeign;
import itch.tecnm.proyecto.feign.MesaFeign;
import itch.tecnm.proyecto.feign.ReservaFeign;
import itch.tecnm.proyecto.mapper.DetalleVentaMapper;
import itch.tecnm.proyecto.repository.DetalleVentaRepository;
import itch.tecnm.proyecto.service.DetalleVentaService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DetalleVentaServiceImpl implements DetalleVentaService {

    private final DetalleVentaRepository detalleVentaRepository;
    private final ClienteFeign clienteFeign;
    private final EmpleadoFeign empleadoFeign;
    private final ReservaFeign reservaFeign;
    private final MesaFeign mesaFeign;
    private final AtenderFeign atenderFeign;

    @Transactional(readOnly = true)
    @Override
    public DetalleVentaDto getDetalleById(Integer detalleId) {
        DetalleVenta dv = detalleVentaRepository.findById(detalleId)
                .orElseThrow(() -> new IllegalArgumentException("Detalle de venta no encontrado"));
        return DetalleVentaMapper.mapToDetalleVentaDto(dv);
    }

    @Transactional(readOnly = true)
    @Override
    public List<DetalleVentaDto> getAllDetallesVenta() {
        return detalleVentaRepository.findAll().stream()
                .map(DetalleVentaMapper::mapToDetalleVentaDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<DetalleVentaDto> getDetallesByVenta(Integer idVenta) {
        return detalleVentaRepository.findByVenta_IdVenta(idVenta).stream()
                .map(DetalleVentaMapper::mapToDetalleVentaDto)
                .collect(Collectors.toList());
    }

    // ==========================================================
    // 🧾 GENERAR TICKET PDF CON FONDO Y LOGO
    // ==========================================================
    @Transactional(readOnly = true)
    @Override
    public void generarTicketPdf(Integer idVenta, HttpServletResponse response)
            throws IOException, DocumentException {

        // 1️⃣ Buscar detalles de la venta
        List<DetalleVenta> detalles = detalleVentaRepository.findByVenta_IdVenta(idVenta);
        if (detalles.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron detalles para la venta " + idVenta);
        }

        Venta venta = detalles.get(0).getVenta();

     // 2️⃣ Obtener cliente
        var cliente = clienteFeign.getClienteById(venta.getIdCliente());

        // 3️⃣ Obtener atención (mesero que atendió)
        AtenderFeign.AtenderDto atender = atenderFeign.getAtenderByVenta(idVenta);

        EmpleadoFeign.EmpleadoDto empleado = (atender != null)
                ? empleadoFeign.getEmpleadoById(atender.getIdEmpleado())
                : null;

     // 4️⃣ Obtener reserva y mesa
        String mesaNumero = "—";

        if (venta.getIdReserva() != null) {

            ReservaFeign.ReservaDto reserva = reservaFeign.getReservaById(venta.getIdReserva());

            if (reserva != null && reserva.getIdMesa() != null) {

                MesaFeign.MesaDto mesa = mesaFeign.getMesaById(reserva.getIdMesa());

                mesaNumero = (mesa != null && mesa.getNumero() != null)
                        ? mesa.getNumero().toString()
                        : "—";
            }

        }



        // 5️⃣ Configuración del PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=ticket_" + idVenta + ".pdf");

        Document doc = new Document(new Rectangle(270, 500)); // tamaño tipo ticket
        PdfWriter writer = PdfWriter.getInstance(doc, response.getOutputStream());

        // 🎨 Fondo color #e9ebe7
        writer.setPageEvent(new PdfPageEventHelper() {
            @Override
            public void onEndPage(PdfWriter writer, Document document) {
                PdfContentByte canvas = writer.getDirectContentUnder();
                Rectangle rect = document.getPageSize();
                canvas.setColorFill(new BaseColor(233, 235, 231)); // RGB del color #e9ebe7
                canvas.rectangle(rect.getLeft(), rect.getBottom(), rect.getWidth(), rect.getHeight());
                canvas.fill();
            }
        });

        doc.open();

        // ===============================
        // 🖼️ LOGO
        // ===============================
        try {
            Image logo = Image.getInstance(
                    getClass().getResource("/templates/Images/logo2.png"));
            logo.scaleToFit(70, 70);
            logo.setAlignment(Image.ALIGN_CENTER);
            doc.add(logo);
        } catch (Exception e) {
            System.err.println("⚠️ No se pudo cargar el logo: " + e.getMessage());
        }

        // ===============================
        // 🧾 ENCABEZADO
        // ===============================
        Font fTitulo = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        Font fNormal = new Font(Font.FontFamily.HELVETICA, 8);

        Paragraph titulo = new Paragraph("Ticket de Venta #" + idVenta, fTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        doc.add(titulo);

        doc.add(new Paragraph("--------------------------------------------------------------------------", fNormal));

        doc.add(new Paragraph("Cliente: " + cliente.getNombreCliente(), fNormal));
        doc.add(new Paragraph("Mesero: " + (empleado != null ? empleado.getNombre() : "—"), fNormal));
        doc.add(new Paragraph("Mesa: " + mesaNumero, fNormal));
        doc.add(new Paragraph("Fecha: " + new java.util.Date(), fNormal));
        doc.add(Chunk.NEWLINE);

        // ===============================
        // 🛒 TABLA DE PRODUCTOS
        // ===============================
        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidths(new int[]{30, 70, 60, 60});
        tabla.setWidthPercentage(100);

        tabla.addCell(new Phrase("Cant.", fNormal));
        tabla.addCell(new Phrase("Producto", fNormal));
        tabla.addCell(new Phrase("Precio", fNormal));
        tabla.addCell(new Phrase("Subtotal", fNormal));

        double total = 0;
        for (DetalleVenta d : detalles) {
            tabla.addCell(String.valueOf(d.getCantidad()));
            tabla.addCell(d.getProducto().getNombreProducto());
            tabla.addCell(String.format("$%.2f", d.getPrecioUnitario()));
            double subtotal = d.getCantidad() * d.getPrecioUnitario();
            total += subtotal;
            tabla.addCell(String.format("$%.2f", subtotal));
        }
        doc.add(tabla);
        doc.add(new Paragraph(" "));
        doc.add(new Paragraph("--------------------------------------------------------------------------", fNormal));
        Paragraph totalP = new Paragraph(String.format("TOTAL: $%.2f", total), fTitulo);
        totalP.setAlignment(Element.ALIGN_RIGHT);
        doc.add(totalP);

        doc.add(new Paragraph(" "));
        Paragraph gracias = new Paragraph("¡Gracias por su compra! 🍽️", fNormal);
        gracias.setAlignment(Element.ALIGN_CENTER);
        doc.add(gracias);

        doc.close();
    }
}
