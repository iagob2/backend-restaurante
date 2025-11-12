package br.restaurante.repository;

import br.restaurante.model.ItemRestaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRestauranteRepository extends JpaRepository<ItemRestaurante, Long> {

    // Métodos de busca personalizados, que o Spring Data JPA implementa automaticamente.

    /**
     * Busca todos os pratos de um restaurante específico, ordenados pelo nome.
     * @param restauranteId O ID do restaurante.
     * @return Uma lista de pratos.
     */
    List<ItemRestaurante> findByRestauranteIdOrderByNomeAsc(Long restauranteId);

    /**
     * Busca todos os pratos ativos de um restaurante específico, ordenados pelo nome.
     * @param restauranteId O ID do restaurante.
     * @return Uma lista de pratos ativos.
     */
    List<ItemRestaurante> findByRestauranteIdAndAtivoTrueOrderByNomeAsc(Long restauranteId);

    /**
     * Busca todos os pratos ativos.
     * @return Uma lista de pratos ativos.
     */
    List<ItemRestaurante> findByAtivoTrueOrderByNomeAsc();

    /**
     * Busca pratos pelo nome, ignorando maiúsculas e minúsculas.
     * @param nome O nome do prato.
     * @return Uma lista de pratos que correspondem ao nome.
     */
    List<ItemRestaurante> findByNomeContainingIgnoreCase(String nome);

    /**
     * Busca pratos com preço menor ou igual a um valor específico.
     * @param preco O valor máximo do preço.
     * @return Uma lista de pratos que correspondem ao critério.
     */
    List<ItemRestaurante> findByPrecoLessThanEqual(Double preco);
}