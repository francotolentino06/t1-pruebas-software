package com.cibertec.loyalty.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class LoyaltyPointsCalculatorTest {

    private LoyaltyPointsCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new LoyaltyPointsCalculator();
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el monto de compra es nulo, cero o negativo")
    void debeLanzarIllegalArgumentExceptionCuandoMontoCompraEsNuloCeroONegativo() {
        IllegalArgumentException excepcionNulo = assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculateAccumulatedPoints(null, "REGULAR")
        );
        assertEquals("El monto de compra debe ser mayor a cero", excepcionNulo.getMessage());

        assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculateAccumulatedPoints(BigDecimal.ZERO, "VIP")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculateAccumulatedPoints(new BigDecimal("-25.50"), "REGULAR")
        );
    }

    @Test
    @DisplayName("Debe calcular 1 punto por cada unidad entera redondeada hacia abajo para cliente regular")
    void debeCalcularPuntosSimplesRedondeadosHaciaAbajoCuandoClienteEsRegular() {
        BigDecimal montoCompra = new BigDecimal("140.85");
        String estadoCliente = "REGULAR";

        int puntosObtenidos = calculator.calculateAccumulatedPoints(montoCompra, estadoCliente);

        assertEquals(140, puntosObtenidos);
    }

    @Test
    @DisplayName("Debe duplicar los puntos redondeados hacia abajo cuando el cliente tiene estatus VIP")
    void debeCalcularElDobleDePuntosRedondeadosHaciaAbajoCuandoClienteEsVip() {
        BigDecimal montoCompra = new BigDecimal("75.60");
        String estadoCliente = "VIP";

        int puntosObtenidos = calculator.calculateAccumulatedPoints(montoCompra, estadoCliente);

        assertEquals(151, puntosObtenidos);
    }
}