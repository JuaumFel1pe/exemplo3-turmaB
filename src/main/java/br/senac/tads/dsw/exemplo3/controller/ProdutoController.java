package br.senac.tads.dsw.exemplo3.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.senac.tads.dsw.exemplo3.model.Produto;
import br.senac.tads.dsw.exemplo3.repository.ProdutoRepository;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {
    
    private final ProdutoRepository repository;

    public ProdutoController(ProdutoRepository repository) {
        this.repository = repository;
    }

    //Recebendo um nome e um preço
    @PostMapping
    public ResponseEntity<Produto> criarProduto(@RequestBody Produto produto){
        
        //Usa o repository, pega 
        Produto produtoSalvo = repository.save(produto);

        //Criando um novo endereço do post
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest() //pegando a url base
            .path("/{id}") //adicionar o id
            .buildAndExpand(produtoSalvo.getId())
            .toUri();    
    
        return ResponseEntity.created(location).body(produtoSalvo);
    }
    //por padrão, assim que você faz um post ele retorna o json da modificação feita

        //Endpoint get para listar todos os produtos
    @GetMapping 
    public List<Produto>listarTodos(){
        return repository.findAll();
    }

    @GetMapping("/{id}")                      //PathVariable serve para ele saber que está recebendo o id junto com o caminho
    public ResponseEntity<Produto>buscarPorId(@PathVariable  Long id){
        
        //serve para ser opcional, pra caso receber um valor nulo, ele não dar erro.
        //Caso o produto não exista, não dará erro 
        Optional<Produto> produtoBuscado = repository.findById(id);

        //isPresent para ver se realmente existe algo
        if(produtoBuscado.isPresent()){
            //se existir ele retornará um ok, retornando um get com o produto especificado
            return ResponseEntity.ok(produtoBuscado.get());
        }
        else{
            //Informará que não foi encontrada (build serve apenas para construir a estrutura da resposta)
            return ResponseEntity.notFound().build();
        }
        
    }

    @PutMapping("/{id}")//Recebe o id porque vai atualizar um produto em especifico
    public ResponseEntity<Produto> atualizarProduto(@PathVariable Long id, @RequestBody Produto produtoAtualizado){
        //Optional porque o produto pode não existir
        Optional<Produto> produtoBuscado = repository.findById(id);

        if(produtoBuscado.isPresent()){
            Produto produtoExistente = produtoBuscado.get();

            //Sobrepondo os nomes e preços do produto existente para os novos dados
            produtoExistente.setNome(produtoAtualizado.getNome());
            produtoExistente.setPreco(produtoAtualizado.getPreco());

            Produto produtoSalvo = repository.save(produtoExistente);

            return ResponseEntity.ok(produtoSalvo);
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagarProduto(@PathVariable Long id){
        Optional<Produto> produtoBuscado = repository.findById(id);

        if(produtoBuscado.isPresent()){
            repository.deleteById(id);

            //Usa o noContent porque não precisa retornar nada, já que ele apaga o produto
            return ResponseEntity.noContent().build();
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }
    
}
