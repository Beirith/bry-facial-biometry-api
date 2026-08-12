# Desafio prático Bry - API de Biometria Facial

## Sobre o projeto

Implementação do desafio prático para a vaga de desenvolvedor Java, na empresa Bry Tecnologia.

O projeto está dividido em duas grandes partes:

### Backend (`bry-facial-biometry/backend`)

Desenvolvido em Java juntamente com o framework Spring Boot para a realização das operações de CRUD de usuários,
biometria facial (verificação e identificação) e persistência de dados no PostgreSQL.

### Frontend (`bry-facial-biometry/frontend`)

Desenvolvido em Angular para consumo da API e interação com o usuário.

## Tecnologias e versões

| Camada           | Tecnologia              | Versão                                                                                         |
|------------------|-------------------------|------------------------------------------------------------------------------------------------|
| Backend          | Java                    | 21 (Eclipse Temurin)                                                                           |
| Backend          | Spring Boot             | 4.1.0                                                                                          |
| Backend          | Maven                   | gerenciado via Maven                                                                           |
| Banco de dados   | PostgreSQL              | 15                                                                                             |
| Biometria facial | Deep Java Library (DJL) | 0.31.1 (PyTorch engine 0.30.0)                                                                 |
| Frontend         | Angular                 | CLI 22.x                                                                                       |
| Frontend         | Node.js                 | 24.x (necessário para o Angular CLI 22 — versões abaixo de 22.22.3/24.15.0 não são suportadas) |
| Containerização  | Docker + Docker Compose | v2 (sintaxe `docker compose`, sem hífen)                                                       |

## Organização de arquivos do backend

| Pacote          | Função                                                                                                           |
|-----------------|------------------------------------------------------------------------------------------------------------------|
| `biometry`      | Casos de uso de validação e identificação biométrica (por meio da lib DJL)                                       |
| `configuration` | Configuração do CORS para possibilitar consumo das APIs no navegador e inserção de usuários iniciais para testes |
| `controller`    | Controladores das requisições HTTP                                                                               |
| `dto`           | Objetos de transferência de dados, usados para receber e enviar dados dentro da API                              |
| `exception`     | Padronização e tratamento de exceções                                                                            |
| `model`         | Entidades mapeadas para as tabelas do banco de dados                                                             |
| `repository`    | Acesso e manipulação do banco de dados                                                                           |
| `service`       | Casos de uso cadastro, exibição, atualização e deleção de usuários (CRUD)                                        |
| `util`          | Classe auxiliar para conversão de vetores de características                                                     |

## Organização de arquivos do frontend

| Pacote                  | Função                                                               |
|-------------------------|----------------------------------------------------------------------|
| `shared`                | Validação do campo cpf                                               |
| `data/models`           | Modelos de dados retornados e enviados para o backend                |
| `data/services`         | Casos de uso de chamadas HTTP para o backend e navegação entre telas |
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

## Threshold de similaridade

O limiar de decisão para verificação e identificação é configurável via `application.properties`, na variável
`biometry.similarity-threshold`

Também é possível sobrescrever esse valor **por requisição**, enviando um campo `threshold` (entre 0 e 1) junto com a
verificação/identificação — nas telas do frontend, isso é feito através de um slider visual.

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

## Testes via Postman

Uma coleção do Postman com os endpoints está disponível em
`bry-facial-biometry/postman/Bry Facial Biometry API.postman_collection.json`.

## Dados de exemplo automáticos

Ao subir a aplicação com o banco vazio, um `CommandLineRunner` (`UserSeedRunner`) cadastra automaticamente 5 usuários de
exemplo, cada um com sua respectiva foto.

- Roda **apenas uma vez** — se o banco já tiver usuários, o seed é ignorado automaticamente.
- As fotos estão armazenadas em `backend/src/main/resources/seed-beatles/`.

## Cadastro e atualização em lote

Além do CRUD individual, a API permite processar múltiplos usuários numa única requisição, com cada um processado em uma thread separada (pool dedicado de 5 threads):

- `POST /api/users/batch` — cadastro em lote
- `PUT /api/users/batch` — atualização em lote (identificação por CPF)

Cada item do lote é processado de forma independente — se um usuário falhar (CPF duplicado, foto inválida, etc.), os demais continuam sendo processados normalmente, e o resultado final indica sucesso/falha por item.

A tela de listagem permite selecionar múltiplos usuários (via checkbox) e enviá-los diretamente para a tela de atualização em lote, já com CPF e nome pré-preenchidos.