package ro.casalazi.utilizatori;

import org.junit.jupiter.api.Test;
import ro.casalazi.cabinete.Cabinet;
import ro.casalazi.firme.Firma;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UtilizatorCurentTest {

    private final UUID utilizatorId = UUID.fromString("a0000000-0000-0000-0000-000000000001");
    private final UUID cabinetId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final UtilizatorRepository utilizatori = mock(UtilizatorRepository.class);
    private final UtilizatorCurent curent = new UtilizatorCurent(utilizatorId, utilizatori);

    @Test
    void returnsCabinetOfConfiguredActiveAccountant() {
        Cabinet cabinet = mock(Cabinet.class);
        when(cabinet.getId()).thenReturn(cabinetId);
        Utilizator contabil = new Utilizator("ana@example.com", "test-hash", "Ana",
                Rol.CONTABIL, cabinet, null);
        when(utilizatori.findById(utilizatorId)).thenReturn(Optional.of(contabil));

        assertEquals(cabinetId, curent.cabinetId());
        verify(utilizatori).findById(utilizatorId);
    }

    @Test
    void rejectsMissingUser() {
        when(utilizatori.findById(utilizatorId)).thenReturn(Optional.empty());
        assertEquals("Utilizatorul curent configurat nu există.",
                assertThrows(IllegalStateException.class, curent::cabinetId).getMessage());
    }

    @Test
    void rejectsInactiveAccountant() {
        Utilizator contabil = new Utilizator("ana@example.com", "test-hash", "Ana",
                Rol.CONTABIL, new Cabinet("Cabinet A"), null);
        contabil.setActiv(false);
        when(utilizatori.findById(utilizatorId)).thenReturn(Optional.of(contabil));

        assertEquals("Utilizatorul curent este inactiv.",
                assertThrows(IllegalStateException.class, curent::cabinetId).getMessage());
    }

    @Test
    void rejectsCashier() {
        Firma firma = new Firma(new Cabinet("Cabinet A"), "Firma A", "18452300",
                "J16/412/2016", "Adresă", true);
        Utilizator casier = new Utilizator("ion@example.com", "test-hash", "Ion",
                Rol.CASIER, null, firma);
        when(utilizatori.findById(utilizatorId)).thenReturn(Optional.of(casier));

        assertEquals("Utilizatorul curent trebuie să aibă rolul CONTABIL.",
                assertThrows(IllegalStateException.class, curent::cabinetId).getMessage());
    }

    @Test
    void rejectsAccountantWithoutCabinet() {
        Utilizator contabil = mock(Utilizator.class);
        when(contabil.isActiv()).thenReturn(true);
        when(contabil.getRol()).thenReturn(Rol.CONTABIL);
        when(utilizatori.findById(utilizatorId)).thenReturn(Optional.of(contabil));

        assertThrows(IllegalStateException.class, curent::cabinetId);
    }

    @Test
    void rejectsCabinetWithoutId() {
        Utilizator contabil = new Utilizator("ana@example.com", "test-hash", "Ana",
                Rol.CONTABIL, new Cabinet("Nesalvat"), null);
        when(utilizatori.findById(utilizatorId)).thenReturn(Optional.of(contabil));

        assertThrows(IllegalStateException.class, curent::cabinetId);
    }
}
