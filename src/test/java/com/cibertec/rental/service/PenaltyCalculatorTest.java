package com.cibertec.rental.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PenaltyCalculatorTest {

    private PenaltyCalculator penaltyCalculator;

    @BeforeEach
    void setUp() {
        penaltyCalculator = new PenaltyCalculator();
    }

    @Test
    @DisplayName("Debe lanzar IllegalStateException cuando los días de retraso son negativos")
    void debeLanzarIllegalStateExceptionCuandoDiasRetrasoSonNegativos() {
        IllegalStateException excepcion = assertThrows(
                IllegalStateException.class,
                () -> penaltyCalculator.calculateLatePenalty(-4)
        );
        assertEquals("Los días de retraso no pueden ser negativos", excepcion.getMessage());
    }

    @Test
    @DisplayName("Debe retornar 0 de penalización cuando la devolución es a tiempo (0 días)")
    void debeRetornarCeroCuandoDevolucionEsATiempoConCeroDiasRetraso() {
        int penalizacion = penaltyCalculator.calculateLatePenalty(0);

        assertEquals(0, penalizacion);
    }

    @Test
    @DisplayName("Debe calcular penalidad base ($15/día) sin recargo cuando el retraso es menor o igual a 7 días")
    void debeCalcularPenalidadBaseSinSeveridadCuandoRetrasoEsMenorOIgualASieteDias() {
        // 4 días * $15 = $60
        assertEquals(60, penaltyCalculator.calculateLatePenalty(4));

        // Límite de 7 días * $15 = $105
        assertEquals(105, penaltyCalculator.calculateLatePenalty(7));
    }

    @Test
    @DisplayName("Debe aplicar el tope máximo comercial de $300 cuando el retraso es prolongado")
    void debeAplicarTopeMaximoDeTrescientosCuandoRetrasoEsProlongado() {
        // 18 días -> (18 * 15) + (11 * 10) = 270 + 110 = 380 -> Tope: 300
        int penalizacion = penaltyCalculator.calculateLatePenalty(18);

        assertEquals(300, penalizacion);
    }
}