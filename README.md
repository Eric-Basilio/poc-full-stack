# PoC Contador: Spring Boot + Kubernetes + PostgreSQL

Este repositório contém uma Prova de Conceito (PoC) focada na integração de uma API Java (Spring Boot) com um banco de dados PostgreSQL rodando em um cluster Kubernetes local (Kind).

O objetivo desta PoC é validar, na prática e em pequena escala, toda a cadeia tecnológica que será usada em um projeto maior de controle financeiro e investimentos (100% self-hosted, orquestrado via Kubernetes e exposto via Cloudflare Tunnel): containerização, orquestração, persistência de dados, arquitetura em camadas no Spring Boot, autenticação de usuários e boas práticas de gestão de configuração/segredos.

## 📐 Arquitetura

```
[Botão no navegador]
        │  fetch (POST)
        ▼
[Service (kubectl port-forward) :8081]
        ▼
[Pod api-contador] ── Controller → Service → Repository
        │  JDBC (rede interna do cluster)
        ▼
[PostgreSQL StatefulSet no cluster Kind]
        │
[PersistentVolumeClaim (1Gi)]
```

A API já roda **dentro do cluster Kind**, empacotada como imagem Docker e publicada via `api-contador-service`. O acesso a partir do frontend (fora do cluster) é feito hoje via `kubectl port-forward`, simulando o que futuramente será resolvido pela exposição pública via Cloudflare Tunnel.

## 🧱 Stack

- **Backend:** Java 17, Spring Boot (Web, Data JPA)
- **Banco de dados:** PostgreSQL 15
- **Containerização:** Docker (imagem da API construída a partir do `.jar` gerado pelo Maven)
- **Orquestração:** Kubernetes (via Kind) — StatefulSet + PVC + Secret + Service (Postgres), Deployment + Service (API)
- **Build:** Maven (`mvnw`)
- **Testes de API:** Postman, curl, JUnit5/Mockito

## 📂 Estrutura do projeto

```
api-contador/
└── src/main/java/br/gov/dataprev/poc/api_contador/
    ├── model/
    │   └── Contador.java          # Entidade JPA (id, valorAtual)
    ├── repository/
    │   └── ContadorRepository.java # Interface JpaRepository<Contador, Long>
    ├── service/
    │   └── ContadorService.java    # Regras de negócio: incrementar() e getContador()
    └── controller/
        └── ContadorController.java # Rotas POST /incrementar e GET /contador

frontend/
├── index.html
└── script.js

k8s/
├── postgres-pvc.yaml
├── postgres-secret-template.yaml
├── postgres-statefulset.yaml
├── postgres-service.yaml
├── api-deployment.yaml
└── api-service.yaml
```

## 🗺️ Sprints

### ✅ Concluídas

| Sprint | Entrega |
|--------|---------|
| **1 — Preparação do ambiente** | Docker Desktop (com engine WSL2) e Kind instalados e validados |
| **2 — Infraestrutura K8s (Postgres)** | PostgreSQL 15 rodando no cluster Kind via StatefulSet, com PVC (1Gi), Secret (credenciais via `envFrom`) e Service ClusterIP na porta 5432 |
| **3 — API Spring Boot** | Projeto gerado via Spring Initializr; camadas Model → Repository → Service → Controller implementadas; rota `POST /incrementar` validada de ponta a ponta contra o banco real no cluster |
| **4 — Frontend** | `index.html` + `script.js` consumindo a API via `fetch`; erro de CORS reproduzido e resolvido com `@CrossOrigin` no Controller |
| **5 — Testes automatizados** | `ContadorServiceTest` com JUnit5/Mockito cobrindo os dois cenários do método `incrementar()`; diferença prática entre teste unitário e teste de integração (`contextLoads`) explorada |
| **6 — Containerização e deploy no cluster** | `Dockerfile` da API (imagem Java 17 + `.jar`); build da imagem, `kind load docker-image`; `Deployment` e `Service` da API aplicados no cluster; API rodando como Pod dentro do Kind, acessível via `kubectl port-forward svc/api-contador-service` |

### 🔜 Próximas

| Sprint | Objetivo | Notas |
|--------|----------|-------|
| **7 — Autenticação** | Adicionar uma camada de login/autenticação na API, testando na PoC o que será obrigatório no projeto final | Ponto de partida: `spring-boot-starter-security` (ainda não está no `pom.xml`); decidir entre autenticação stateless via JWT (mais próxima do que o projeto final vai precisar) ou sessão simples primeiro, como exercício incremental; vai exigir uma entidade `Usuario`, endpoint de login e proteção das rotas existentes (`/incrementar`, `/contador`) atrás de autenticação |
| **8 — Exposição pública (Cloudflare Tunnel)** | Expor a API (e o frontend) fora da rede local, através de domínio próprio | Faz mais sentido vir depois da autenticação, já que expor a API publicamente sem login é mais arriscado |

## ✅ Status atual (checklist)

- [x] Cluster Kind local criado e validado
- [x] PostgreSQL rodando no cluster via StatefulSet + PVC + Secret + Service
- [x] Camada Model (entidade JPA `Contador`)
- [x] Camada Repository (`ContadorRepository`)
- [x] Camada Service (`ContadorService`, lógica de incremento)
- [x] Camada Controller (`ContadorController`, rotas `POST /incrementar` e `GET /contador`)
- [x] Fluxo de incremento validado de ponta a ponta via `curl`/Postman, com persistência confirmada no banco
- [x] Frontend estático (HTML/CSS/JS) consumindo a API
- [x] Testes automatizados (JUnit/Mockito) do `ContadorService`
- [x] Empacotamento da API em container Docker e deploy dentro do próprio cluster
- [ ] Camada de autenticação (login) na API
- [ ] Exposição pública via Cloudflare Tunnel

## 🛠️ Guia de Execução e Comandos Úteis

### 1. Ao iniciar o cluster do zero
É necessário criar o cluster com o Kind rodando o comando `kind create cluster`. É essencial para aplicar os arquivos YAML que residirão no cluster.

### 2. Comunicação com o Kubernetes (Túnel)
Para que a API Java local consiga enxergar o banco de dados que está isolado dentro do cluster Kubernetes (no `ClusterIP`), é necessário abrir um túnel de rede:

```bash
kubectl port-forward svc/postgres-service 5432:5432
```
*(Mantenha este terminal aberto enquanto estiver desenvolvendo).*

### 3. Gerenciamento de Secrets (Base64)
O Kubernetes exige que os valores no arquivo de `Secret` sejam codificados. Utilize os comandos abaixo no Linux para manipular as credenciais de forma rápida:

**Para codificar texto em Base64:**
```bash
echo -n "SUA_PALAVRA_AQUI" | base64
```

**Para decodificar texto de Base64:**
```bash
echo -n "SUA_PALAVRA_AQUI" | base64 -d
```

### 4. Execução da API Spring Boot (localmente, fora do cluster)
Seguindo as boas práticas de segurança e os princípios do **12-Factor App**, as credenciais do banco não ficam no código (hardcoded). Elas são injetadas como variáveis de ambiente na inicialização do servidor web (Tomcat):

```bash
DB_NOME=BANCO_TESTE DB_USUARIO=USUARIO DB_SENHA=SENHA ./mvnw spring-boot:run
```
*(Substitua os valores fictícios pelas credenciais reais decodificadas configuradas no seu Secret do Kubernetes).*

A API sobe em `http://localhost:8081`.

### 5. Rodando os testes
Para rodar os testes unitários e o que já vem com o Spring Initializr, é necessário estar com o `port-forward` do Postgres rodando e também passar as variáveis de ambiente do banco de dados:

```bash
DB_NOME=BANCO_TESTE DB_USUARIO=USUARIO DB_SENHA=SENHA ./mvnw test
```

### 6. Empacotamento e deploy da API dentro do cluster

- Build do `.jar` pulando os testes:
```bash
./mvnw clean package -DskipTests
```
- Build da imagem Docker (`Dockerfile` puxa uma imagem Linux com Java 17 e copia o `.jar` gerado):
```bash
docker build -t api-contador .
```
- Importar a imagem para dentro do Kind (o Kind não enxerga o Docker local por padrão):
```bash
kind load docker-image api-contador:latest
```
- Aplicar o Deployment que orienta o cluster a criar um Pod a partir da imagem:
```bash
kubectl apply -f ./k8s/api-deployment.yaml
```
- Expor a API para o frontend (que está fora do cluster) via o Service `api-service.yaml`:
```bash
kubectl port-forward svc/api-contador-service 8081:8081
```

## 🔌 Endpoints disponíveis

| Método | Rota           | Descrição                                            | Resposta            |
|--------|----------------|-------------------------------------------------------|---------------------|
| POST   | `/incrementar` | Soma +1 ao contador persistido no banco e retorna o valor atualizado | `Integer` (ex: `5`) |
| GET    | `/contador`    | Retorna o valor atual do contador sem incrementar     | `Integer` (ex: `5`) |

### Exemplo de teste rápido via curl

```bash
curl -X POST http://localhost:8081/incrementar
curl http://localhost:8081/contador
```

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

Este projeto foi construído de forma incremental e propositalmente manual (sem código gerado pronto), como exercício prático de estudo para os tópicos de Desenvolvimento de Software, Banco de Dados, Kubernetes/Docker, Segurança/Autenticação e Arquitetura de Software cobrados em processos seletivos de TI.