package br.restaurante.controller;

import br.restaurante.model.ItemRestaurante;
import br.restaurante.service.ItemRestauranteService;
import br.restaurante.service.FileStorageService; // Importação necessária
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // Importação necessária

import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/itens", produces = "application/json")
public class ItemRestauranteController {

    @Autowired
    private ItemRestauranteService itemRestauranteService;

    @Autowired
    private FileStorageService fileStorageService; // Injetando o serviço de armazenamento

    @PostMapping
    public ResponseEntity<ItemRestaurante> cadastrarItem(@RequestBody ItemRestaurante itemRestaurante) {
        try {
            System.out.println("Cadastrando item: " + itemRestaurante.getNome());
            System.out.println("URL da imagem recebida: " + itemRestaurante.getImagemUrl());
            System.out.println("Restaurante ID: " + (itemRestaurante.getRestaurante() != null ? itemRestaurante.getRestaurante().getId() : "null"));
            
            ItemRestaurante novoItem = itemRestauranteService.cadastrarItem(itemRestaurante);
            
            System.out.println("Item cadastrado com sucesso. ID: " + novoItem.getId());
            System.out.println("URL da imagem salva: " + novoItem.getImagemUrl());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(novoItem);
        } catch (InputMismatchException e) {
            System.err.println("Erro ao cadastrar item: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // NOVO ENDPOINT DE UPLOAD DE IMAGEM
    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<?> uploadImagem(@RequestPart("file") MultipartFile file) {
        try {
            System.out.println("Recebendo upload de arquivo: " + file.getOriginalFilename());
            System.out.println("Tamanho do arquivo: " + file.getSize() + " bytes");
            
            // Usa o FileStorageService para salvar o arquivo na pasta 'itens'
            String url = fileStorageService.saveFile(file, "itens");
            System.out.println("Arquivo salvo com sucesso. URL: " + url);
            
            // Retorna a URL para o frontend
            return ResponseEntity.ok(Map.of("url", url));
        } catch (Exception e) {
            System.err.println("Erro no upload: " + e.getMessage());
            e.printStackTrace();
            // Em caso de erro, retorna uma resposta de erro com a mensagem
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<ItemRestaurante>> buscarTodosItens() {
        List<ItemRestaurante> itens = itemRestauranteService.buscarTodosItens();
        return ResponseEntity.ok(itens);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemRestaurante> buscarItemPorId(@PathVariable Long id) {
        Optional<ItemRestaurante> item = itemRestauranteService.buscarItemPorId(id);
        return item.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemRestaurante> atualizarItem(@PathVariable Long id, @RequestBody ItemRestaurante itemAtualizado) {
        Optional<ItemRestaurante> item = itemRestauranteService.atualizarItem(id, itemAtualizado);
        return item.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarItem(@PathVariable Long id) {
        boolean removido = itemRestauranteService.deletarItem(id);
        if (removido) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/restaurante/{restauranteId}")
    public ResponseEntity<List<ItemRestaurante>> buscarItensPorRestaurante(@PathVariable Long restauranteId) {
        List<ItemRestaurante> itens = itemRestauranteService.buscarItensPorRestaurante(restauranteId);
        return ResponseEntity.ok(itens);
    }
}