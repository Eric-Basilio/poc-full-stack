// mapeamento do botão de incrementar
const botao = document.getElementById('botaoIncrementar');
// Busca do valor do contador
const span = document.getElementById('valorContador');

botao.addEventListener("click", async function(){
    const resposta = await fetch("https://solid-umbrella-xpjjqgwrg55fpxg7-8081.app.github.dev/incrementar",{
        method: "POST"
    })
    const novoValor = await resposta.json();
    span.innerHTML=novoValor;
});