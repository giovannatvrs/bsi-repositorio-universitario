
const listaDisciplinas = document.getElementById('lista-disciplinas');
const btnDisciplinas = document.getElementById('btn-disciplinas');
const botaoNovoArquivo = document.getElementById('novo');
const popUp = document.getElementById('overlay');
const fechar = document.getElementById('close');
const cancelar = document.getElementById('cancelar');
if(listaDisciplinas){
    
    btnDisciplinas.addEventListener('click', function(){
        listaDisciplinas.classList.toggle('ativa');
    })
}

if(popUp){
    novo.addEventListener('click', function(){
        popUp.classList.toggle('ativo');
    })
    fechar.addEventListener('click', function(){
        popUp.classList.remove('ativo');
    })
    cancelar.addEventListener('click', function(){
        popUp.classList.remove('ativo');
    })

}

async function fetchData(){
    urlData = window.location.pathname;
    try{
        const response = await fetch("/usuario-info",{
            credentials: 'include'
        });

        if(!response.ok){
            if(response.status == 401){
                window.location.href = "/login";
            }
            throw new Error(`Erro HTTP: ${response.status}`);
        }
        const data= await response.json();
        console.log(data);

        atualizarPerfil(data);

    }catch(error){
        console.error("Falha em carregar os dados do usuário",error);

    }


}

function atualizarPerfil(data){
    const fotoDePerfil = document.getElementById("foto-perfil");
    const nomeUsuario = document.getElementById("nome-usuario");
    if(fotoDePerfil) {
        fotoDePerfil.src = data.picture || "default.png";
    }
    if(nomeUsuario){
        nomeUsuario.textContent = data.name;
    }
}

document.addEventListener("DOMContentLoaded", fetchData);

document.addEventListener("DOMContentLoaded", atualizarNavegacao);

function atualizarNavegacao(){
    var urlData= window.location.pathname;
    const paginas = document.getElementById("paginas");
    if(urlData == "/usuario-logado"){
        paginas.innerHTML = `<a>Histórico</a>
        <a href="/usuarios">Usuários</a>
        <a>Moderadores</a>`
    }
    if(urlData == "/usuarios"){
        paginas.innerHTML = `<a onclick="history.back()">Voltar</a>
        <a>Histórico</a>
        <a>Moderadores</a>`
    }

}



