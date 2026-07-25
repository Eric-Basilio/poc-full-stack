# PoC Contador: Spring Boot + Kubernetes + PostgreSQL

Este repositório contém uma Prova de Conceito (PoC) focada na integração de uma API Java (Spring Boot) com um banco de dados PostgreSQL rodando em um cluster Kubernetes local (Kind). 

## 🛠️ Guia de Execução e Comandos Úteis

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
DB_NOME=SEU_BANCO DB_USUARIO=SEU_USUARIO DB_SENHA=SUA_SENHA ./mvnw spring-boot:run
```
*(Substitua os valores fictícios pelas credenciais reais decodificadas configuradas no seu Secret do Kubernetes).*