package com.mercadolibre.desafiospring.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mercadolibre.desafiospring.model.Article;
import com.mercadolibre.desafiospring.utils.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ArticleRepository {

    private List<Article> articles = new ArrayList<Article>();
    private final String PATH = "src/main/resources/articles.json";
    private FileUtils fileUtils = new FileUtils();

    private static ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public Article createArticle(Article article) {
        try {
            articles = getArticles();
            articles.add(article);
            fileUtils.writeFile(PATH, articles);
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao criar Produto");
        }
        return article;
    }

    public Article update(Long id, Article article) {
        try {
            if (!getArticleById(id).equals(null)) {
                article.setProductId(id);
                int index = articles.indexOf(getArticleById(id));
                articles.set(index, article);
                fileUtils.writeFile(PATH, articles);

            }
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao atualizar Produto");
        }
        return article;
    }

    public void delete(Long id) {
        try {
            if (!getArticleById(id).equals(null)) {
                List<Article> collect = articles.stream().filter(a -> !a.getProductId().equals(id)).collect(Collectors.toList());
                fileUtils.writeFile(PATH, collect);
            }
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao deletar Produto");
        }
    }

    public List<Article> getArticles() {
        List<Article> result = new ArrayList<Article>();
        try {
            String jsonString = FileUtils.GetFileToString(PATH);
            Article[] arrArticle = objectMapper.readValue(jsonString, Article[].class);

            for (int i = 0; i < arrArticle.length; i++) {
                result.add(arrArticle[i]);
            }

        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao acessar arquivo");
        }
        return result;
    }

    public Article getArticleById(Long productId) {
        Article article = null;
        try {
            String jsonString = FileUtils.GetFileToString(PATH);
            articles = Arrays.asList(objectMapper.readValue(jsonString, Article[].class));
            article = articles.stream().filter(a -> a.getProductId() == productId).findFirst().orElse(null);
            if (article.equals(null)) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Produto não encontrado");
            }
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao recuperar Produto");
        }
        return article;
    }

}