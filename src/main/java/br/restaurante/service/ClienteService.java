package br.restaurante.service;

import br.restaurante.dto.LoginRequest;
import br.restaurante.model.Cliente;
import br.restaurante.model.Endereco;
import br.restaurante.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ViaCepService viaCepService;

    @Autowired // Injete o password encoder
    private BCryptPasswordEncoder passwordEncoder;

    public Cliente cadastrarCliente(Cliente cliente) {
        // --- Programação Defensiva: Validação de dados de entrada ---

        // 1. Validar campos obrigatórios
        if (cliente.getNome() == null || cliente.getNome().isBlank() ||
                cliente.getCpf() == null || cliente.getCpf().isBlank() ||
                cliente.getEmail() == null || cliente.getEmail().isBlank() ||
                cliente.getSenha() == null || cliente.getSenha().isBlank()) {
            throw new InputMismatchException("Os campos Nome, CPF, Email e Senha são obrigatórios.");
        }

        // 2. Limpar a formatação do CPF e validar o tamanho
        String cpfLimpo = somenteNumeros(cliente.getCpf());
        if (!isCpfValido(cpfLimpo)) {
            throw new InputMismatchException("O CPF informado é inválido.");
        }
        cliente.setCpf(cpfLimpo);

        // 3. Validar formato do email (opcional, mas recomendado)
        Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
        if (!emailPattern.matcher(cliente.getEmail()).matches()) {
            throw new InputMismatchException("O formato do email é inválido.");
        }

        // 4. Verificar unicidade do CPF e Email
        Optional<Cliente> clienteExistenteCpf = clienteRepository.findByCpf(cliente.getCpf());
        if (clienteExistenteCpf.isPresent()) {
            throw new IllegalArgumentException("CPF já cadastrado.");
        }

        Optional<Cliente> clienteExistenteEmail = clienteRepository.findByEmail(cliente.getEmail());
        if (clienteExistenteEmail.isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado.");
        }

        // 5. Buscar e preencher o endereço a partir do CEP
        if (cliente.getCep() != null && !cliente.getCep().isBlank()) {
            Endereco endereco = viaCepService.buscaEnderecoPorCep(cliente.getCep());
            cliente.setRua(endereco.rua());
            cliente.setBairro(endereco.bairro());
            cliente.setCidade(endereco.cidade());
            cliente.setEstado(endereco.estado());
        }

        // 6. Criptografar a senha antes de salvar
        String senhaHash = passwordEncoder.encode(cliente.getSenha());
        cliente.setSenha(senhaHash);

        // 7. Salvar a entidade no banco de dados
        return clienteRepository.save(cliente);
    }

    private String somenteNumeros(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replaceAll("\\D", "");
    }

    private boolean isCpfValido(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return false;
        }

        if (cpf.chars().distinct().count() == 1) {
            return false;
        }

        try {
            int digito1 = calcularDigitoCpf(cpf, 10);
            int digito2 = calcularDigitoCpf(cpf, 11);
            return digito1 == Character.getNumericValue(cpf.charAt(9))
                    && digito2 == Character.getNumericValue(cpf.charAt(10));
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private int calcularDigitoCpf(String cpf, int pesoInicial) {
        int soma = 0;
        int peso = pesoInicial;
        for (int i = 0; i < pesoInicial - 1; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * peso--;
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    /**
     * Valida as credenciais do cliente para o login.
     * @param loginRequest Objeto com email e senha.
     * @return O objeto Cliente se as credenciais estiverem corretas, ou null.
     */
    public Cliente loginCliente(LoginRequest loginRequest) {
        Optional<Cliente> clienteOptional = clienteRepository.findByEmail(loginRequest.getEmail());

        if (clienteOptional.isPresent()) {
            Cliente cliente = clienteOptional.get();
            // Comparar a senha fornecida com a senha criptografada do banco de dados
            if (passwordEncoder.matches(loginRequest.getSenha(), cliente.getSenha())) {
                return cliente;
            }
        }
        return null; // Retorna nulo se o email não for encontrado ou a senha estiver incorreta
    }

    public Optional<Cliente> buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }


    public List<Cliente> buscarTodos() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Optional<Cliente> atualizarCliente(Long id, Cliente clienteAtualizado) {
        return clienteRepository.findById(id).map(clienteExistente -> {
            clienteExistente.setNome(clienteAtualizado.getNome());
            clienteExistente.setTelefone(clienteAtualizado.getTelefone());
            clienteExistente.setEmail(clienteAtualizado.getEmail());
            clienteExistente.setRua(clienteAtualizado.getRua());
            clienteExistente.setBairro(clienteAtualizado.getBairro());
            clienteExistente.setCidade(clienteAtualizado.getCidade());
            clienteExistente.setEstado(clienteAtualizado.getEstado());
            clienteExistente.setCep(clienteAtualizado.getCep());
            clienteExistente.setNumero(clienteAtualizado.getNumero());
            clienteExistente.setAceitaProtecaoDados(clienteAtualizado.isAceitaProtecaoDados());
            clienteExistente.setAceitaMarketing(clienteAtualizado.isAceitaMarketing());
            clienteExistente.setAceitaAtendimento(clienteAtualizado.isAceitaAtendimento());
            // A senha não deve ser atualizada diretamente aqui por segurança
            // A lógica de atualização de senha deve ser um endpoint separado
            return clienteRepository.save(clienteExistente);
        });
    }

    public boolean deletarCliente(Long id) {
        Optional<Cliente> cliente = clienteRepository.findById(id);
        if (cliente.isPresent()) {
            clienteRepository.deleteById(id);
            return true;
        }
        return false;
    }
}