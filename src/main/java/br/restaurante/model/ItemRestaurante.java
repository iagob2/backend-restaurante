package br.restaurante.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@Entity
public class ItemRestaurante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String descricao; // Usado para 'ingredientes' no front-end

    private Double preco; // Usado para 'valor' no front-end

    @Column(length = 50)
    private String categoria; // Ex.: "Prato quente", "Salada", "Bebida"

    @Column(length = 2048)
    private String imagemUrl; // Novo campo para a imagem do front-end

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean ativo = true;

    @ManyToOne
    @JoinColumn(name = "restaurante_id", nullable = false)
    @JsonIgnoreProperties({"avaliacoes", "senha"})
    private Restaurante restaurante;

    @OneToMany(mappedBy = "itemRestaurante", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<AvaliacaoPrato> avaliacoes;

    // Construtor, getters e setters (gerados manualmente ou pelo Lombok)

    public ItemRestaurante() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Restaurante getRestaurante() {
        return restaurante;
    }

    public void setRestaurante(Restaurante restaurante) {
        this.restaurante = restaurante;
    }

    public List<AvaliacaoPrato> getAvaliacoes() {
        return avaliacoes;
    }

    public void setAvaliacoes(List<AvaliacaoPrato> avaliacoes) {
        this.avaliacoes = avaliacoes;
    }
}