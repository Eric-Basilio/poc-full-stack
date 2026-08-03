# PoC Contador: Spring Boot + Kubernetes + PostgreSQL

Este repositório contém uma Prova de Conceito (PoC) focada na integração de uma API Java (Spring Boot) com um banco de dados PostgreSQL rodando em um cluster Kubernetes local (Kind).

O objetivo desta PoC é validar, na prática e em pequena escala, toda a cadeia tecnológica que será usada em um projeto maior de controle financeiro e investimentos (100% self-hosted, orquestrado via Kubernetes e exposto via Cloudflare Tunnel): containerização, orquestração, persistência de dados, arquitetura em camadas no Spring Boot e boas práticas de gestão de configuração/segredos.

## 📐 Arquitetura

```
[Botão no navegador]
        │  fetch (POST)
        ▼
[API Spring Boot :8081] ── Controller → Service → Repository
        │  JDBC (via kubectl port-forward :5432)
        ▼
[PostgreSQL StatefulSet no cluster Kind]
        │
[PersistentVolumeClaim (1Gi)]
```

A API roda localmente (fora do cluster, por enquanto) e se conecta ao banco de dados através de um túnel `kubectl port-forward`, simulando a comunicação que futuramente ocorrerá entre Pods dentro do próprio cluster.

## 🧱 Stack

- **Backend:** Java 17, Spring Boot (Web, Data JPA)
- **Banco de dados:** PostgreSQL 15
- **Orquestração:** Kubernetes (via Kind), StatefulSet + PersistentVolumeClaim + Secret + Service
- **Build:** Maven (`mvnw`)
- **Testes de API:** Postman, curl

## 📂 Estrutura do projeto

```
api-contador/
└── src/main/java/br/gov/dataprev/poc/api_contador/
    ├── model/
    │   └── Contador.java          # Entidade JPA (id, valorAtual)
    ├── repository/
    │   └── ContadorRepository.java # Interface JpaRepository<Contador, Long>
    ├── service/
    │   └── ContadorService.java    # Regra de negócio: incrementar()
    └── controller/
        └── ContadorController.java # Rota POST /incrementar
```

Manifestos Kubernetes (PVC, Secret, StatefulSet, Service do Postgres) mantidos separadamente na pasta de infraestrutura do projeto.

## ✅ Status atual

- [x] Cluster Kind local criado e validado
- [x] PostgreSQL rodando no cluster via StatefulSet + PVC + Secret + Service
- [x] Camada Model (entidade JPA `Contador`)
- [x] Camada Repository (`ContadorRepository`)
- [x] Camada Service (`ContadorService`, lógica de incremento)
- [x] Camada Controller (`ContadorController`, rota `POST /incrementar`)
- [x] Fluxo de incremento validado de ponta a ponta via `curl`/Postman, com persistência confirmada no banco
- [x] Frontend estático (HTML/CSS/JS) consumindo a API
- [x] Testes automatizados (JUnit/Mockito) do `ContadorService`
- [ ] Empacotamento da API em container Docker e deploy dentro do próprio cluster
- [ ] Exposição pública via Cloudflare Tunnel

## 🛠️ Guia de Execução e Comandos Úteis

### 1 Ao iniciar o cluster do zero
É necessário criar o cluste com o kind rodando o comando kind create cluster. É essencial para aplicar os arquivos YAML que residirão no clustes.

### 1. Comunicação com o Kubernetes (Túnel)
Para que a API Java local consiga enxergar o banco de dados que está isolado dentro do cluster Kubernetes (no `ClusterIP`), é necessário abrir um túnel de rede:

```bash
kubectl port-forward svc/postgres-service 5432:5432
```
*(Mantenha este terminal aberto enquanto estiver desenvolvendo).*

### 2. Gerenciamento de Secrets (Base64)
O Kubernetes exige que os valores no arquivo de `Secret` sejam codificados. Utilize os comandos abaixo no Linux para manipular as credenciais de forma rápida:

**Para codificar texto em Base64:**
```bash
echo -n "SUA_PALAVRA_AQUI" | base64
```

**Para decodificar texto de Base64:**
```bash
echo -n "SUA_PALAVRA_AQUI" | base64 -d
```

### 3. Execução da API Spring Boot
Seguindo as boas práticas de segurança e os princípios do **12-Factor App**, as credenciais do banco não ficam no código (hardcoded). Elas são injetadas como variáveis de ambiente na inicialização do servidor web (Tomcat):

```bash
DB_NOME=BANCO_TESTE DB_USUARIO=USUARIO DB_SENHA=SENHA ./mvnw spring-boot:run
```
*(Substitua os valores fictícios pelas credenciais reais decodificadas configuradas no seu Secret do Kubernetes).*

A API sobe em `http://localhost:8081`.

## 🔌 Endpoints disponíveis

| Método | Rota           | Descrição                                            | Resposta            |
|--------|----------------|-------------------------------------------------------|---------------------|
| POST   | `/incrementar` | Soma +1 ao contador persistido no banco e retorna o valor atualizado | `Integer` (ex: `5`) |

### Exemplo de teste rápido via curl

```bash
curl -X POST http://localhost:8081/incrementar
```

### rodar os testes com o mvnw  corretamente 
Para rodar os testes unitários e o que já vem com o spring initializr é necessário estar com o port-foward rodando e também passar as variáveis de ambiente do banco de dados com o comando ./mvnw test. Fica DB_NOME=BANCO_TESTE DB_USUARIO=USUARIO DB_SENHA=SENHA ./mvnw test

## 🔍 Inspecionando o banco diretamente

Para verificar os dados persistidos sem passar pela API, é possível acessar o `psql` diretamente dentro do Pod do Postgres:

```bash
kubectl get pods
kubectl exec -it NOME_DO_POD -- psql -U NOME_DO_USUARIO -d NOME_DO_BANCO
```

Dentro do `psql`:

```sql
SELECT * FROM contador;
```

## 📚 Contexto de aprendizado

Este projeto foi construído de forma incremental e propositalmente manual (sem código gerado pronto), como exercício prático de estudo para os tópicos de Desenvolvimento de Software, Banco de Dados, Kubernetes/Docker e Arquitetura de Software cobrados em processos seletivos de TI.