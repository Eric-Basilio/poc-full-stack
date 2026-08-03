// mapeamento do botão de incrementar
const botao = document.getElementById('botaoIncrementar');
// Busca do valor do contador
const span = document.getElementById('valorContador');

async function carregarContador(){
    const resposta = await fetch("http://localhost:8081/contador", {
        method: "GET"
    })
    const valor = await resposta.json();
    span.innerHTML = valor;
}

carregarContador();

botao.addEventListener("click", async function(){
    const resposta = await fetch("http://localhost:8081/incrementar",{
        method: "POST"
    })
    //const novoValor = await resposta.json();
    //span.innerHTML=novoValor;
    carregarContador();
});