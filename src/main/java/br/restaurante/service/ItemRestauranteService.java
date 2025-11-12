package br.restaurante.service;

import br.restaurante.model.ItemRestaurante;
import br.restaurante.model.Restaurante;
import br.restaurante.repository.ItemRestauranteRepository;
import br.restaurante.repository.RestauranteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;

@Service
public class ItemRestauranteService {

    @Autowired
    private ItemRestauranteRepository itemRestauranteRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    /**
     * Salva um novo item de cardápio com validações.
     * @param itemRestaurante O item a ser salvo.
     * @return O item salvo.
     * @throws InputMismatchException se a validação falhar.
     */
    public ItemRestaurante cadastrarItem(ItemRestaurante itemRestaurante) {
        // --- Programação Defensiva: Validação de dados de entrada ---

        // 1. Validar campos obrigatórios
        if (itemRestaurante.getNome() == null || itemRestaurante.getNome().isBlank() ||
                itemRestaurante.getPreco() == null || itemRestaurante.getPreco() < 0) {
            throw new InputMismatchException("O nome e o preço do item são obrigatórios e o preço deve ser positivo.");
        }

        // 2. Validar se o restaurante associado existe
        if (itemRestaurante.getRestaurante() == null || itemRestaurante.getRestaurante().getId() == null) {
            throw new InputMismatchException("É necessário associar o item a um restaurante.");
        }

        Optional<Restaurante> restauranteExistente = restauranteRepository.findById(itemRestaurante.getRestaurante().getId());
        if (restauranteExistente.isEmpty()) {
            throw new InputMismatchException("Restaurante não encontrado. Verifique o ID do restaurante.");
        }

        // Atribui a referência completa do restaurante à entidade antes de salvar
        itemRestaurante.setRestaurante(restauranteExistente.get());

        // Garante que o item esteja ativo ao ser criado
        itemRestaurante.setAtivo(true);

        // 3. Salvar a entidade no banco de dados
        return itemRestauranteRepository.save(itemRestaurante);
    }

    /**
     * Busca todos os itens do cardápio.
     * @return Uma lista de todos os itens.
     */
    public List<ItemRestaurante> buscarTodosItens() {
        return itemRestauranteRepository.findByAtivoTrueOrderByNomeAsc();
    }

    /**
     * Busca um item do cardápio por ID.
     * @param id O ID do item.
     * @return Um Optional contendo o item, se ele for encontrado.
     */
    public Optional<ItemRestaurante> buscarItemPorId(Long id) {
        return itemRestauranteRepository.findById(id);
    }

    /**
     * Atualiza um item do cardápio existente.
     * @param id O ID do item a ser atualizado.
     * @param itemAtualizado O objeto com os dados atualizados.
     * @return Um Optional contendo o item atualizado, se a operação for bem-sucedida.
     */
    public Optional<ItemRestaurante> atualizarItem(Long id, ItemRestaurante itemAtualizado) {
        return itemRestauranteRepository.findById(id).map(item -> {
            item.setNome(itemAtualizado.getNome());
            item.setDescricao(itemAtualizado.getDescricao());
            item.setPreco(itemAtualizado.getPreco());
            item.setImagemUrl(itemAtualizado.getImagemUrl());
            item.setCategoria(itemAtualizado.getCategoria());
            // Lógica de validação pode ser adicionada aqui, se necessário
            return itemRestauranteRepository.save(item);
        });
    }

    /**
     * Deleta um item do cardápio por ID.
     * @param id O ID do item a ser deletado.
     */
    public boolean deletarItem(Long id) {
        return itemRestauranteRepository.findById(id)
                .map(item -> {
                    item.setAtivo(false);
                    itemRestauranteRepository.save(item);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Busca todos os itens de um restaurante específico.
     * @param restauranteId O ID do restaurante.
     * @return Uma lista de itens do restaurante.
     */
    public List<ItemRestaurante> buscarItensPorRestaurante(Long restauranteId) {
        return itemRestauranteRepository.findByRestauranteIdAndAtivoTrueOrderByNomeAsc(restauranteId);
    }
}