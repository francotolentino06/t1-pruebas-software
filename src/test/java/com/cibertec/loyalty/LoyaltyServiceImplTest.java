package com.cibertec.loyalty;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoyaltyServiceImplTest {

    @Mock
    private LoyaltyRepository loyaltyRepository;

    @InjectMocks
    private LoyaltyServiceImpl loyaltyService;

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException y nunca registrar puntos si el monto es menor o igual a cero")
    void debeLanzarIllegalArgumentExceptionYNuncaRegistrarPuntosCuandoMontoEsMenorOIgualACero() {
        Long userId = 15L;
        BigDecimal montoInvalido = BigDecimal.ZERO;

        IllegalArgumentException excepcion = assertThrows(
                IllegalArgumentException.class,
                () -> loyaltyService.calculateAndAssignPoints(userId, montoInvalido)
        );

        assertEquals("El monto de compra debe ser mayor a cero", excepcion.getMessage());
        verify(loyaltyRepository, never()).addPoints(anyLong(), anyInt());
        verify(loyaltyRepository, never()).isVipUser(anyLong());
    }

    @Test
    @DisplayName("Debe otorgar el doble de puntos y actualizar el repositorio cuando el cliente es VIP")
    void debeOtorgarDobleDePuntosYActualizarRepositorioCuandoClienteEsVip() {
        // Arrange
        Long userId = 25L;
        BigDecimal montoCompra = new BigDecimal("92.80"); // basePoints = 92 -> VIP = 184
        when(loyaltyRepository.isVipUser(userId)).thenReturn(true);

        // Act
        LoyaltyResponse respuesta = loyaltyService.calculateAndAssignPoints(userId, montoCompra);

        // Assert
        assertNotNull(respuesta);
        assertEquals(userId, respuesta.userId());
        assertEquals(184, respuesta.pointsEarned());
        assertTrue(respuesta.isVip());

        verify(loyaltyRepository, times(1)).isVipUser(userId);
        verify(loyaltyRepository, times(1)).addPoints(userId, 184);
    }
}