# Desafio prático Bry - API de Biometria Facial

## Sobre o projeto

Implementação do desafio prático para a vaga de desenvolvedor Java, na empresa Bry Tecnologia.

O projeto está dividido em duas grandes partes:

### Backend (`bry-facial-biometry/backend`)

Desenvolvido em Java juntamente com o framework Spring Boot para a realização das operações de CRUD de usuários,
biometria facial (verificação e identificação) e persistência de dados no PostgreSQL.

Nâo tive tempo para implementar um threshold parametrizável, então optei por deixar um valor padrão de 0.85, definido no
arquivo `application.properties`, na variável `biometry.similarity-threshold`.

Também não tive tempo para melhorar o processamento do facial_template do usuário, de modo que as requisições de criação
estão demorando mais do que o esperado. Minha ideia era adicionar threads, de maneira semelhante ao realizado no
cadastro/atualização em lote.

### Frontend (`bry-facial-biometry/frontend`)

Desenvolvido em Angular para consumo da API e interação com o usuário.

## Organização de arquivos do backend

| Pacote          | Função                                                                              |
|-----------------|-------------------------------------------------------------------------------------|
| `biometry`      | Casos de uso de validação e identificação biométrica (por meio da lib DJL)          |
| `configuration` | Configuração do CORS para possibilitar consumo das APIs no navegador                |
| `controller`    | Controladores das requisições HTTP                                                  |
| `dto`           | Objetos de transferência de dados, usados para receber e enviar dados dentro da API |
| `exception`     | Padronização e tratamento de exceções                                               |
| `model`         | Entidades mapeadas para as tabelas do banco de dados                                |
| `repository`    | Acesso e manipulação do banco de dados                                              |
| `service`       | Casos de uso cadastro, exibição, atualização e deleção de usuários (CRUD)           |
| `util`          | Classe auxiliar para conversão de vetores de características                        |

## Organização de arquivos do frontend

| Pacote                  | Função                                                               |
|-------------------------|----------------------------------------------------------------------|
| `shared`                | Validação do campo cpf                                               |
| `data/models`           | Modelos de dados retornados e enviados para o backend                |
| `data/services`         | Casos de uso de chamadas HTTP para o backend e navegação entre telas |
| `presentation/home`     | Tela inicial com acesso a todas as funcionalidades                   |
| `presentation/users`    | Telas de listagem, cadastro individual e em lote, edição e detalhes  |
| `presentation/biometry` | Telas de verificação (1:1) e identificação (1:n)                     |

## Organização do banco de dados

O banco de dados é dividido em duas tabelas:

### users

Armazena os dados dos usuários cadastrados no sistema.

| Coluna    | Tipo      | Descrição                                       |
|-----------|-----------|-------------------------------------------------|
| `id`      | `bigint`  | Identificador único do usuário (chave primária) |
| `name`    | `varchar` | Nome do usuário                                 |
| `cpf`     | `varchar` | CPF do usuário (único)                          |
| `picture` | `bytea`   | Foto do usuário, armazenada em formato binário  |

### facial_templates

Armazena o template facial (vetor de características) extraído da foto de cada usuário, utilizado nos processos de
verificação e identificação.

| Coluna           | Tipo     | Descrição                                                       |
|------------------|----------|-----------------------------------------------------------------|
| `id`             | `bigint` | Identificador único do template (chave primária)                |
| `feature_vector` | `bytea`  | Vetor de características faciais, armazenado em formato binário |
| `user_id`        | `bigint` | Chave estrangeira, referenciando o usuário dono do template     |

A tabela `facial_templates` possui uma relação **um-para-um** com a tabela `users`, com exclusão em cascata
(`ON DELETE CASCADE`) de modo que, ao excluir um usuário, o template facial associado a ele é removido automaticamente.

## Modo de uso

### Executando com Docker

Na raiz do projeto, executar o seguinte comando para subir os container (backend, frontend e banco de dados):

```bash
docker compose up --build
```

Após isso, é possível acessar a aplicação em: http://localhost:4200

### Executando manualmente

Em caso de execução manuall, é necessário se ater as configurações pré definidas do banco
(`bry-facial-biometry-api/backend/src/main/resources/application.properties`)

**Banco de dados**

```bash
docker run --name bry-postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=bry_facial_biometry -p 5432:5432 -d postgres:15
```

**Backend**

```bash
cd backend
./mvnw spring-boot:run
```

**Frontend**

```bash
cd frontend
npm install
ng serve
```

Após isso, é possível acessar a aplicação em: http://localhost:4200

### Testes via Postman

Uma coleção do Postman com os endpoints está disponível em
`bry-facial-biometry/postman/Bry Facial Biometry API.postman_collection.json`.

