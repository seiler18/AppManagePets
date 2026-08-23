package cl.jesus.appmanagepets;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Comprueba las reglas de acceso, que es lo que se rompe sin darse cuenta al
 * tocar SecurityConfig.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SeguridadWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void elHomeEsPublico() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk());
    }

    @Test
    void sinSesionMisMascotasMandaAlLogin() throws Exception {
        mockMvc.perform(get("/mascotas"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "demo", roles = "USER")
    void unUsuarioNormalNoEntraAlPanelDeAdmin() throws Exception {
        mockMvc.perform(get("/admin")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN", "USER" })
    void elAdminEntraAlPanel() throws Exception {
        mockMvc.perform(get("/admin")).andExpect(status().isOk());
    }

    /**
     * Regresion: borrar una mascota ajena devolvia 405 en vez de 403. El
     * acceso se denegaba bien, pero accessDeniedPage hace un forward que
     * conserva el POST y la pagina de aviso solo aceptaba GET.
     */
    @Test
    @WithMockUser(username = "demo", roles = "USER")
    void borrarUnaMascotaAjenaDa403YNo405() throws Exception {
        mockMvc.perform(post("/mascotas/999999/eliminar").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "demo", roles = "USER")
    void unPostSinTokenCsrfDa403YNo405() throws Exception {
        mockMvc.perform(post("/mascotas/999999/eliminar"))
                .andExpect(status().isForbidden());
    }
}
