package com.cibertec.subscription;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private PaymentGatewayClient paymentGatewayClient;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    @Test
    @DisplayName("Debe lanzar ExpiredCardException, suspender cuenta y no realizar cobros cuando la tarjeta venció")
    void debeLanzarExpiredCardExceptionYSuspenderCuentaSinCobrosCuandoTarjetaEstaVencida() {
        // Arrange
        Long subscriptionId = 301L;
        Subscription suscripcionActual = new Subscription(subscriptionId, "ACTIVE", LocalDate.of(2026, 11, 20));

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(suscripcionActual);
        when(paymentGatewayClient.isCardValid(subscriptionId)).thenReturn(false);

        // Act & Assert
        ExpiredCardException excepcion = assertThrows(
                ExpiredCardException.class,
                () -> subscriptionService.renewSubscription(subscriptionId)
        );

        assertEquals("La tarjeta asociada ha expirado", excepcion.getMessage());

        verify(subscriptionRepository, times(1)).updateStatus(subscriptionId, "SUSPENDED");
        verify(paymentGatewayClient, never()).chargeMonthlyFee(anyLong());
        verify(subscriptionRepository, never()).updateBillingDate(anyLong(), any());
    }

    @Test
    @DisplayName("Debe procesar el cobro y extender la fecha de facturación en un mes cuando la tarjeta es válida")
    void debeProcesarCobroYExtenderFechaFacturacionEnUnMesCuandoTarjetaEsValida() {
        // Arrange
        Long subscriptionId = 405L;
        LocalDate fechaInicial = LocalDate.of(2026, 11, 20);
        LocalDate fechaEsperada = LocalDate.of(2026, 12, 20);
        Subscription suscripcionActual = new Subscription(subscriptionId, "ACTIVE", fechaInicial);

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(suscripcionActual);
        when(paymentGatewayClient.isCardValid(subscriptionId)).thenReturn(true);

        // Act
        Subscription suscripcionRenovada = subscriptionService.renewSubscription(subscriptionId);

        // Assert
        assertNotNull(suscripcionRenovada);
        assertEquals(subscriptionId, suscripcionRenovada.id());
        assertEquals("ACTIVE", suscripcionRenovada.status());
        assertEquals(fechaEsperada, suscripcionRenovada.nextBillingDate());

        verify(paymentGatewayClient, times(1)).chargeMonthlyFee(subscriptionId);
        verify(subscriptionRepository, times(1)).updateBillingDate(subscriptionId, fechaEsperada);
    }
}